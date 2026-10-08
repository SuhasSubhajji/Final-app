package com.example.data.glossary

import com.example.data.local.AppDatabase
import com.example.data.local.GlossaryTermEntity
import com.example.data.model.FeedSource
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class GlossaryTerm(
    val term: String,
    val definition: String,
    val industryStandard: String,
    val practicalExample: String,
    val category: String,
    val isAiGenerated: Boolean = false
)

class GlossaryRepository(
    private val database: AppDatabase,
    private val geminiService: GeminiService = GeminiService()
) {
    private val threatDao = database.threatDao()

    val allTermsFlow: Flow<List<GlossaryTerm>> = threatDao.getAllGlossaryTermsFlow().map { entities ->
        entities.map {
            GlossaryTerm(
                term = it.term,
                definition = it.definition,
                industryStandard = it.industryStandard,
                practicalExample = it.practicalExample,
                category = it.category,
                isAiGenerated = it.isAiGenerated
            )
        }
    }

    suspend fun initializeSeedsIfNeeded() {
        val seedEntities = getCuratedGlossarySeeds().map {
            GlossaryTermEntity(
                term = it.term,
                definition = it.definition,
                industryStandard = it.industryStandard,
                practicalExample = it.practicalExample,
                category = it.category,
                isAiGenerated = false
            )
        }
        threatDao.insertGlossaryTerms(seedEntities)
    }

    suspend fun getTermDefinition(term: String): GlossaryTerm? {
        val cached = threatDao.getGlossaryTerm(term)
        return cached?.let {
            GlossaryTerm(
                term = it.term,
                definition = it.definition,
                industryStandard = it.industryStandard,
                practicalExample = it.practicalExample,
                category = it.category,
                isAiGenerated = it.isAiGenerated
            )
        }
    }

    suspend fun lookupOrGenerateWithGemini(term: String): GlossaryTerm {
        val existing = getTermDefinition(term)
        if (existing != null) return existing

        // Query Gemini for real industry standard definition
        val prompt = """
            You are a Senior Information Security Officer and NIST/MITRE standardization expert.
            Provide an authoritative definition for the cybersecurity / DevSecOps term: "$term".
            Respond strictly in the following format:
            ===DEFINITION===
            (Clear, concise explanation in 2-3 sentences)
            ===STANDARD===
            (Primary industry standard or framework, e.g. "NIST SP 800-53", "MITRE ATT&CK Txxxx", "OWASP Top 10", "CISA KEV")
            ===EXAMPLE===
            (A real-world technical scenario or concrete command demonstrating this concept)
            ===CATEGORY===
            (One category: "AppSec", "Cloud", "Offensive", "Defensive", "DevSecOps", "Identity")
        """.trimIndent()

        val dummyThreat = ThreatItem(
            id = "glossary-$term",
            title = term,
            description = "Lookup glossary term: $term",
            source = FeedSource.DEVSECOPS_WEEKLY,
            link = "https://cisa.gov",
            pubDateMillis = System.currentTimeMillis(),
            severity = ThreatSeverity.INFO,
            category = ThreatCategory.DEVSECOPS
        )

        val rawAnswer = geminiService.askCustomQuestion(dummyThreat, prompt)
        val parsed = parseGeminiGlossary(term, rawAnswer)

        // Cache in Room DB for offline persistence
        threatDao.insertOrUpdateGlossaryTerm(
            GlossaryTermEntity(
                term = parsed.term,
                definition = parsed.definition,
                industryStandard = parsed.industryStandard,
                practicalExample = parsed.practicalExample,
                category = parsed.category,
                isAiGenerated = true
            )
        )

        return parsed
    }

    private fun parseGeminiGlossary(term: String, text: String): GlossaryTerm {
        var def = ""
        var std = "Industry Standard (NIST / MITRE)"
        var ex = "Applied in security controls and threat modeling."
        var cat = "Security"

        try {
            Regex("===DEFINITION===\\s*([\\s\\S]*?)(?====|$)").find(text)?.let { def = it.groupValues[1].trim() }
            Regex("===STANDARD===\\s*([\\s\\S]*?)(?====|$)").find(text)?.let { std = it.groupValues[1].trim() }
            Regex("===EXAMPLE===\\s*([\\s\\S]*?)(?====|$)").find(text)?.let { ex = it.groupValues[1].trim() }
            Regex("===CATEGORY===\\s*([\\s\\S]*?)(?====|$)").find(text)?.let { cat = it.groupValues[1].trim() }
        } catch (_: Exception) {}

        if (def.isBlank()) {
            def = text.take(280).trim().ifEmpty { "A cybersecurity mechanism and architectural control." }
        }

        return GlossaryTerm(
            term = term,
            definition = def,
            industryStandard = std,
            practicalExample = ex,
            category = cat,
            isAiGenerated = true
        )
    }

    private fun getCuratedGlossarySeeds(): List<GlossaryTerm> = listOf(
        GlossaryTerm(
            term = "CVE",
            definition = "Common Vulnerabilities and Exposures (CVE) is a dictionary of publicly disclosed cybersecurity vulnerabilities, managed by MITRE and the National Vulnerability Database (NVD).",
            industryStandard = "NIST SP 800-115 / MITRE CVE",
            practicalExample = "CVE-2025-0282 identifies a critical buffer overflow vulnerability in Ivanti Connect Secure gateways.",
            category = "Vulnerability Intel"
        ),
        GlossaryTerm(
            term = "CVSS",
            definition = "Common Vulnerability Scoring System (CVSS) is an open framework for communicating the severity of software vulnerabilities on a scale from 0.0 to 10.0.",
            industryStandard = "FIRST.org CVSS v3.1 / v4.0",
            practicalExample = "A CVSS 9.8 vulnerability indicates unauthenticated network access requiring low complexity and no user interaction.",
            category = "Risk Management"
        ),
        GlossaryTerm(
            term = "Zero-Day",
            definition = "A flaw in software or hardware that is unknown to the vendor or for which no official patch or mitigation has yet been released, leaving systems vulnerable to active exploitation.",
            industryStandard = "CISA Known Exploited Vulnerabilities (KEV)",
            practicalExample = "Threat actors weaponized a zero-day vulnerability in SonicOS firewalls prior to vendor disclosure.",
            category = "Threat Intel"
        ),
        GlossaryTerm(
            term = "RCE",
            definition = "Remote Code Execution (RCE) occurs when an attacker executes arbitrary commands or machine code on a target server over the network without physical system access.",
            industryStandard = "MITRE ATT&CK T1203 / OWASP A03:2021",
            practicalExample = "Exploiting Log4Shell (CVE-2021-44228) by injecting JNDI lookup strings that triggered rogue class loading.",
            category = "AppSec"
        ),
        GlossaryTerm(
            term = "BOLA",
            definition = "Broken Object Level Authorization (BOLA) occurs when an API endpoint handles object identifiers without validating whether the client has permission to read or modify the requested resource.",
            industryStandard = "OWASP API Security Top 10 (API1:2023)",
            practicalExample = "GET /api/documents/102 modified to /api/documents/103 returns another customer's confidential financial report.",
            category = "API Security"
        ),
        GlossaryTerm(
            term = "Kerberoasting",
            definition = "A post-exploitation attack where an authenticated domain user requests Kerberos Ticket Granting Service (TGS) tickets for service accounts with Service Principal Names (SPNs), then cracks the password hash offline.",
            industryStandard = "MITRE ATT&CK T1558.003",
            practicalExample = "Running `GetUserSPNs.py -request` in an Active Directory environment to harvest TGS tickets encrypted with RC4-HMAC.",
            category = "Offensive"
        ),
        GlossaryTerm(
            term = "Poisoned Pipeline Execution (PPE)",
            definition = "A DevSecOps attack vector where an adversary injects malicious commands into CI/CD configuration files (e.g. GitHub Actions workflows) via pull requests to steal runner credentials or inject backdoors.",
            industryStandard = "OWASP Top 10 CI/CD Security Risks (CICD-SEC-2)",
            practicalExample = "Submitting a pull request modifying a workflow script triggered by `pull_request_target` with write repository permissions.",
            category = "DevSecOps"
        ),
        GlossaryTerm(
            term = "SBOM",
            definition = "A Software Bill of Materials (SBOM) is a formal, machine-readable inventory of software components, libraries, and hierarchical dependencies used in building a software application.",
            industryStandard = "NTIA Minimum Elements / CISA / SPDX / CycloneDX",
            practicalExample = "Generating `sbom.json` using Syft in a CI/CD build step to detect vulnerable transitive dependencies.",
            category = "Supply Chain"
        ),
        GlossaryTerm(
            term = "eBPF",
            definition = "Extended Berkeley Packet Filter (eBPF) allows safe, sandboxed programs to run directly within the Linux operating system kernel without modifying kernel source code or loading modules, enabling low-overhead runtime security.",
            industryStandard = "Linux Foundation / CNCF / CIS Linux Benchmark",
            practicalExample = "Cilium and Tetragon use eBPF to trace process executions, syscalls, and network namespaces inside Kubernetes nodes.",
            category = "Cloud & Kernel"
        ),
        GlossaryTerm(
            term = "CSPM",
            definition = "Cloud Security Posture Management (CSPM) automates security risk identification, misconfiguration detection, and compliance auditing across multi-cloud environments (AWS, Azure, GCP).",
            industryStandard = "CIS Cloud Benchmarks / ISO 27017",
            practicalExample = "Continuously alerting when an Amazon S3 bucket policy is modified to allow public anonymous read access (`*`).",
            category = "Cloud Security"
        ),
        GlossaryTerm(
            term = "Zero Trust",
            definition = "A strategic cybersecurity model based on the principle of 'never trust, always verify', requiring all users and devices to be authenticated and authorized before granting access to network resources.",
            industryStandard = "NIST SP 800-207 / DoD Zero Trust Strategy",
            practicalExample = "Replacing perimeter VPNs with identity-aware proxies requiring biometric MFA and device compliance health checks.",
            category = "Architecture"
        ),
        GlossaryTerm(
            term = "SAST & DAST",
            definition = "Static Application Security Testing (SAST) inspects source code for flaws without executing it; Dynamic Application Security Testing (DAST) tests running applications externally for exploitable HTTP/API vulnerabilities.",
            industryStandard = "OWASP OpenSAMM / NIST SP 800-64",
            practicalExample = "Running Semgrep in pull requests (SAST) and OWASP ZAP against staging environments (DAST).",
            category = "DevSecOps"
        )
    )
}
