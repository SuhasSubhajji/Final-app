package com.example.data.learning

import com.example.data.local.AppDatabase
import com.example.data.local.DynamicRoadmapTopicEntity
import com.example.data.model.FeedSource
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class DynamicRoadmapManager(
    private val database: AppDatabase,
    private val geminiService: GeminiService = GeminiService()
) {
    private val threatDao = database.threatDao()

    fun getDynamicTopicsForTrack(trackId: String): Flow<List<DynamicRoadmapTopicEntity>> {
        return threatDao.getDynamicTopicsForTrackFlow(trackId)
    }

    suspend fun generateNewTopicsForTrack(track: LearningTrack): List<DynamicRoadmapTopicEntity> {
        val prompt = """
            You are a Principal Security Curriculum Engineer.
            Generate 1 brand new, cutting-edge advanced training module for the following cybersecurity learning track:
            Track: ${track.title} (${track.roleBadge})
            Overview: ${track.overview}

            Format your response strictly as:
            ===TITLE===
            (Clear technical title, e.g. "eBPF Kernel Exploitation & Defense" or "OIDC Token Impersonation in CI/CD")
            ===PHASE===
            (Integer 5 or 6, representing advanced next-level phases)
            ===DESCRIPTION===
            (Comprehensive breakdown explaining the technical concept, attack mechanics, and practical relevance)
            ===TOOLS===
            (Comma-separated list of 3-5 real tools or technologies)
            ===CODE===
            (A realistic terminal command, YAML policy, or code snippet with inline explanation)
        """.trimIndent()

        val dummyThreat = ThreatItem(
            id = "dynamic-topic-${track.id}",
            title = "Enrich Track: ${track.title}",
            description = track.overview,
            source = FeedSource.DEVSECOPS_WEEKLY,
            link = "https://cisa.gov",
            pubDateMillis = System.currentTimeMillis(),
            severity = ThreatSeverity.HIGH,
            category = ThreatCategory.DEVSECOPS
        )

        val rawAnswer = geminiService.askCustomQuestion(dummyThreat, prompt)
        val parsed = parseGeminiRoadmapTopic(track.id, rawAnswer)

        threatDao.insertDynamicTopics(listOf(parsed))
        return listOf(parsed)
    }

    private fun parseGeminiRoadmapTopic(trackId: String, text: String): DynamicRoadmapTopicEntity {
        var title = "Advanced Telemetry & Defense"
        var phase = 5
        var desc = "Emerging adversarial vectors and modern mitigation baselines."
        var tools = "Trivy, Falco, Semgrep, eBPF"
        var code = "# Verify active security posture\nkubectl get pods -A"

        try {
            Regex("===TITLE===\\s*([\\s\\S]*?)(?====|$)").find(text)?.let { title = it.groupValues[1].trim() }
            Regex("===PHASE===\\s*(\\d+)").find(text)?.let { phase = it.groupValues[1].toIntOrNull() ?: 5 }
            Regex("===DESCRIPTION===\\s*([\\s\\S]*?)(?====|$)").find(text)?.let { desc = it.groupValues[1].trim() }
            Regex("===TOOLS===\\s*([\\s\\S]*?)(?====|$)").find(text)?.let { tools = it.groupValues[1].trim() }
            Regex("===CODE===\\s*([\\s\\S]*?)(?====|$)").find(text)?.let { code = it.groupValues[1].trim() }
        } catch (_: Exception) {}

        return DynamicRoadmapTopicEntity(
            id = UUID.randomUUID().toString(),
            trackId = trackId,
            phaseNumber = phase,
            title = title,
            description = desc,
            toolsAndSkillsJson = tools,
            codeSnippet = code,
            isAiGenerated = true
        )
    }
}
