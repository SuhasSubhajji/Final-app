package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ThreatItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service dedicated to transforming raw CVE descriptions and advisory data
 * into concise, human-readable threat summaries using the Gemini API (model: gemini-3.5-flash).
 * Includes smart fallback extraction for offline or unconfigured environments.
 */
class CveSummaryService(
    private val geminiApiService: GeminiApiService = RetrofitClient.service
) {
    companion object {
        private const val TAG = "CveSummaryService"
        private const val MODEL_NAME = "gemini-3.5-flash"
    }

    /**
     * Sends raw CVE and vulnerability description data to Gemini API and
     * returns a concise, human-readable threat summary.
     *
     * @param cveIds List of CVE IDs (e.g. listOf("CVE-2025-0282"))
     * @param rawDescription The raw description text from CVE, NVD, or threat feeds
     * @param contextTitle The advisory title if available
     * @return Concise human-readable threat summary (typically 1-3 sentences)
     */
    suspend fun summarizeRawCveDescription(
        cveIds: List<String>,
        rawDescription: String,
        contextTitle: String = ""
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val cveLabel = if (cveIds.isNotEmpty()) cveIds.joinToString(", ") else "CVE Vulnerability"

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "Gemini API key is unconfigured. Generating smart deterministic summary for $cveLabel.")
            return@withContext generateHeuristicCveSummary(cveIds, rawDescription, contextTitle)
        }

        val prompt = """
            You are a Principal Cyber Threat Intelligence Analyst.
            Analyze the following raw CVE vulnerability description and generate a concise, human-readable threat summary for SOC analysts and DevSecOps engineers.
            
            Vulnerability Identifiers: $cveLabel
            Context Title: $contextTitle
            Raw Description / Advisory Data:
            $rawDescription
            
            Guidelines:
            - Provide 1 to 2 concise, crisp sentences.
            - Plainly explain the vulnerable component, the attack vector (e.g. remote code execution, authentication bypass, buffer overflow, SQLi), and the technical impact.
            - Do not include preamble, markdown headers, or conversational text.
            - Ensure it is immediately understandable to non-expert engineers and executive stakeholders.
        """.trimIndent()

        try {
            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                ),
                systemInstruction = GeminiContent(
                    parts = listOf(
                        GeminiPart(
                            text = "You are a specialized cybersecurity assistant that distills raw technical CVE data into concise, human-readable executive summaries."
                        )
                    )
                )
            )

            val execResult = GeminiModelDispatcher.executeWithQuotaFallback { model ->
                geminiApiService.generateContent(
                    model = model.modelId,
                    apiKey = apiKey,
                    request = request
                )
            }

            when (execResult) {
                is QuotaExecutionResult.Success -> {
                    val text = execResult.data.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                    if (!text.isNullOrBlank()) {
                        cleanSummaryText(text)
                    } else {
                        generateHeuristicCveSummary(cveIds, rawDescription, contextTitle)
                    }
                }
                is QuotaExecutionResult.Error -> {
                    Log.e(TAG, "Error querying Gemini API for CVE summary: ${execResult.throwable.message}", execResult.throwable)
                    generateHeuristicCveSummary(cveIds, rawDescription, contextTitle)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying Gemini API for CVE summary: ${e.message}", e)
            generateHeuristicCveSummary(cveIds, rawDescription, contextTitle)
        }
    }

    /**
     * Convenience method to summarize a ThreatItem directly.
     */
    suspend fun summarizeThreat(threat: ThreatItem): String {
        val content = if (threat.fullContent.isNotBlank()) threat.fullContent else threat.description
        return summarizeRawCveDescription(
            cveIds = threat.cveIds,
            rawDescription = content,
            contextTitle = threat.title
        )
    }

    private fun cleanSummaryText(text: String): String {
        return text
            .replace(Regex("^(Summary|Threat Summary|Overview):\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("^[*#-]+\\s*"), "")
            .trim()
    }

    /**
     * Deterministic heuristic fallback ensuring crisp, human-readable summaries
     * even when offline or before the API key is configured.
     */
    private fun generateHeuristicCveSummary(
        cveIds: List<String>,
        rawDescription: String,
        contextTitle: String
    ): String {
        val lowerDesc = rawDescription.lowercase()
        val cvePrefix = if (cveIds.isNotEmpty()) "[${cveIds.first()}] " else ""

        return when {
            lowerDesc.contains("buffer overflow") || lowerDesc.contains("memory corruption") -> {
                "${cvePrefix}Critical memory safety flaw allowing unauthenticated remote attackers to trigger buffer overflows and execute arbitrary code with elevated system privileges."
            }
            lowerDesc.contains("remote code execution") || lowerDesc.contains(" rce") -> {
                "${cvePrefix}High-severity remote code execution vulnerability enabling attackers to execute arbitrary system commands via unvalidated input parameters."
            }
            lowerDesc.contains("injection") || lowerDesc.contains("ingress") -> {
                "${cvePrefix}Dangerous injection flaw permitting malicious directives or payloads to tamper with service configurations and extract protected environment tokens."
            }
            lowerDesc.contains("privilege escalation") || lowerDesc.contains("access control") -> {
                "${cvePrefix}Improper access control allowing an authenticated local or adjacent attacker to bypass security boundaries and escalate to administrator permissions."
            }
            lowerDesc.contains("bypass") || lowerDesc.contains("authentication") -> {
                "${cvePrefix}Authentication bypass vulnerability allowing remote threat actors to evade identity validation filters and access administrative endpoints."
            }
            lowerDesc.contains("supply chain") || lowerDesc.contains("pipeline") || lowerDesc.contains("action") -> {
                "${cvePrefix}Supply chain and pipeline execution risk exposing release artifacts, repository credentials, and build secrets to untrusted contributors."
            }
            rawDescription.isNotBlank() -> {
                val cleaned = rawDescription.replace(Regex("<[^>]*>"), " ").trim()
                if (cleaned.length > 180) {
                    val periodIndex = cleaned.indexOf('.', 60)
                    if (periodIndex != -1) cleaned.substring(0, periodIndex + 1)
                    else cleaned.take(180) + "..."
                } else {
                    cleaned
                }
            }
            contextTitle.isNotBlank() -> {
                "${cvePrefix}Security advisory highlighting exploitable system vectors: $contextTitle."
            }
            else -> {
                "${cvePrefix}Common Vulnerabilities and Exposures advisory requiring vendor patching and perimeter isolation."
            }
        }
    }
}
