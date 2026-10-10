package com.example.data.model

import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityLow
import com.example.ui.theme.SeverityMedium
import androidx.compose.ui.graphics.Color

/**
 * Detailed metadata representation for a specific CVE (Common Vulnerabilities and Exposures).
 * Covers affected software versions, CVSS v3.1 metrics, vector string, CWE taxonomy,
 * exploit status, mitigation guidance, and direct authoritative external references.
 */
data class CveDetail(
    val cveId: String,
    val title: String,
    val description: String,
    val cvssScore: Float,
    val cvssSeverity: ThreatSeverity,
    val cvssVector: String,
    val cweId: String,
    val cweName: String,
    val affectedSoftware: List<AffectedSoftware>,
    val exploitationStatus: CveExploitationStatus,
    val publishedDate: String,
    val lastModifiedDate: String,
    val sourceAdvisory: String,
    val remediationGuidance: String,
    val patchAvailability: String,
    val externalReferences: List<CveExternalReference>
)

data class AffectedSoftware(
    val vendor: String,
    val product: String,
    val affectedVersions: String,
    val fixedVersion: String,
    val packageEcosystem: String = "Enterprise / OS"
)

enum class CveExploitationStatus(val label: String, val badgeColor: Color) {
    ACTIVE_IN_THE_WILD("Exploited In The Wild (CISA KEV)", SeverityCritical),
    PUBLIC_POC_AVAILABLE("Public PoC Exploit Available", SeverityHigh),
    THEORETICAL_PROOF("Reported & Verified", SeverityMedium),
    PATCHED_NO_EXPLOIT("Vendor Patched / No Active Exploit", CyberEmerald)
}

data class CveExternalReference(
    val title: String,
    val url: String,
    val tag: String // e.g. "NIST NVD", "CISA KEV", "Vendor Advisory", "MITRE ATT&CK", "Exploit-DB", "GitHub Security"
)
