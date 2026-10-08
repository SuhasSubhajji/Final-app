package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityCriticalContainer
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityHighContainer
import com.example.ui.theme.SeverityInfo
import com.example.ui.theme.SeverityInfoContainer
import com.example.ui.theme.SeverityLow
import com.example.ui.theme.SeverityLowContainer
import com.example.ui.theme.SeverityMedium
import com.example.ui.theme.SeverityMediumContainer

enum class ThreatSeverity(
    val label: String,
    val color: Color,
    val containerColor: Color,
    val rank: Int
) {
    CRITICAL("CRITICAL", SeverityCritical, SeverityCriticalContainer, 5),
    HIGH("HIGH", SeverityHigh, SeverityHighContainer, 4),
    MEDIUM("MEDIUM", SeverityMedium, SeverityMediumContainer, 3),
    LOW("LOW", SeverityLow, SeverityLowContainer, 2),
    INFO("INFO", SeverityInfo, SeverityInfoContainer, 1);

    companion object {
        fun fromString(value: String): ThreatSeverity {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: INFO
        }
    }
}

enum class FeedSource(
    val displayName: String,
    val feedUrl: String,
    val websiteUrl: String,
    val category: String
) {
    CISA("CISA Alerts", "https://www.cisa.gov/cybersecurity-advisories/all.xml", "https://www.cisa.gov", "Government / Advisory"),
    THE_HACKER_NEWS("The Hacker News", "https://feeds.feedburner.com/TheHackersNews", "https://thehackernews.com", "Cyber News"),
    BLEEPING_COMPUTER("BleepingComputer", "https://www.bleepingcomputer.com/feed/", "https://www.bleepingcomputer.com", "Threat Intel"),
    GITHUB_SECURITY("GitHub Security", "https://github.blog/feed/", "https://github.blog/tag/security/", "DevSecOps"),
    NIST_NVD("NIST NVD CVEs", "https://nvd.nist.gov/feeds/xml/cve/misc/nvd-rss.xml", "https://nvd.nist.gov", "CVE Vulnerabilities"),
    DARK_READING("Dark Reading", "https://www.darkreading.com/rss.xml", "https://www.darkreading.com", "Enterprise"),
    DEVSECOPS_WEEKLY("DevSecOps Weekly", "https://www.devsecops.org/feed.xml", "https://www.devsecops.org", "DevSecOps / CI/CD"),
    OWASP("OWASP Alerts", "https://owasp.org/feed.xml", "https://owasp.org", "AppSec / Best Practices");

    companion object {
        fun fromString(value: String): FeedSource {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CISA
        }
    }
}

enum class ThreatCategory(val displayName: String) {
    DEVSECOPS("DevSecOps & CI/CD"),
    ZERO_DAY("Zero-Day Exploit"),
    RANSOMWARE("Ransomware & Malware"),
    CLOUD_SECURITY("Cloud & Kubernetes"),
    SUPPLY_CHAIN("Supply Chain & Dependencies"),
    WEB_API("API & Web Vulnerability"),
    NETWORK("Network & Infrastructure");

    companion object {
        fun fromString(value: String): ThreatCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: DEVSECOPS
        }
    }
}

enum class DateRangeFilter(val displayName: String, val durationMillis: Long?) {
    ALL("All Time", null),
    PAST_24_HOURS("Past 24 Hours", 24L * 60 * 60 * 1000),
    PAST_7_DAYS("Past 7 Days", 7L * 24 * 60 * 60 * 1000),
    PAST_30_DAYS("Past 30 Days", 30L * 24 * 60 * 60 * 1000)
}

data class ThreatItem(
    val id: String,
    val title: String,
    val description: String,
    val fullContent: String = "",
    val source: FeedSource,
    val link: String,
    val pubDateMillis: Long,
    val severity: ThreatSeverity,
    val category: ThreatCategory,
    val cveIds: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val aiSummary: String? = null,
    val aiRemediationSteps: List<String>? = null,
    val aiRiskScore: Int? = null,
    val aiMitigationCode: String? = null
)

data class SecurityAlert(
    val id: String,
    val threatItemId: String,
    val title: String,
    val snippet: String,
    val severity: ThreatSeverity,
    val timestampMillis: Long,
    val isAcknowledged: Boolean = false,
    val remediationQuickFix: String
)

data class FeedFilterState(
    val searchQuery: String = "",
    val selectedSources: Set<FeedSource> = emptySet(),
    val selectedSeverities: Set<ThreatSeverity> = emptySet(),
    val selectedCategories: Set<ThreatCategory> = emptySet(),
    val dateRange: DateRangeFilter = DateRangeFilter.ALL,
    val onlyBookmarks: Boolean = false,
    val onlyUnread: Boolean = false
) {
    val hasActiveFilters: Boolean
        get() = searchQuery.isNotBlank() ||
                selectedSources.isNotEmpty() ||
                selectedSeverities.isNotEmpty() ||
                selectedCategories.isNotEmpty() ||
                dateRange != DateRangeFilter.ALL ||
                onlyBookmarks ||
                onlyUnread

    val activeFilterCount: Int
        get() = (if (searchQuery.isNotBlank()) 1 else 0) +
                selectedSources.size +
                selectedSeverities.size +
                selectedCategories.size +
                (if (dateRange != DateRangeFilter.ALL) 1 else 0) +
                (if (onlyBookmarks) 1 else 0) +
                (if (onlyUnread) 1 else 0)
}

data class ThreatStats(
    val totalCount: Int,
    val criticalCount: Int,
    val highCount: Int,
    val devSecOpsCount: Int,
    val unreadAlertsCount: Int
)

data class TopicOfTheDay(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val attackVector: String,
    val defenseStrategy: String,
    val practicalExample: String,
    val keyTakeaways: List<String>
)

data class AlertTrendPoint(
    val label: String,
    val count: Int,
    val timestampMillis: Long
)

data class CveTimelineTrendPoint(
    val label: String,
    val dateStr: String,
    val timestampMillis: Long,
    val totalCves: Int,
    val criticalCount: Int,
    val highCount: Int,
    val mediumCount: Int,
    val lowCount: Int,
    val cveIds: List<String> = emptyList(),
    val averageSeverityScore: Float = 0f
)

data class CveSeverityBreakdownSummary(
    val totalCveRecords: Int,
    val criticalTotal: Int,
    val highTotal: Int,
    val mediumTotal: Int,
    val lowTotal: Int,
    val topCveList: List<String>,
    val timelinePoints: List<CveTimelineTrendPoint>
)

data class VulnerabilityTypeStat(
    val typeName: String,
    val count: Int,
    val percentage: Float,
    val color: Color
)

data class SourceBreakdownStat(
    val source: FeedSource,
    val count: Int,
    val percentage: Float
)

data class CyberArticle(
    val id: String,
    val title: String,
    val aiSummary: String,
    val source: String,
    val publishDate: String,
    val severity: ThreatSeverity,
    val link: String = "",
    val category: String = "Threat Intelligence",
    val isBookmarked: Boolean = false
) {
    companion object {
        fun fromThreatItem(item: ThreatItem): CyberArticle {
            return CyberArticle(
                id = item.id,
                title = item.title,
                aiSummary = item.aiSummary ?: item.description,
                source = item.source.displayName,
                publishDate = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(item.pubDateMillis)),
                severity = item.severity,
                link = item.link,
                category = item.category.displayName,
                isBookmarked = item.isBookmarked
            )
        }
    }
}

data class AttackScenario(
    val id: String,
    val organization: String,
    val year: Int,
    val industry: String,
    val recordsAffected: String,
    val attackVector: String,
    val scenarioDetails: String,
    val threatActor: String,
    val defenseMitigation: String,
    val cveReferences: String = ""
) {
    fun toEntity(): com.example.data.local.AttackScenarioEntity {
        return com.example.data.local.AttackScenarioEntity(
            id = id,
            organization = organization,
            year = year,
            industry = industry,
            recordsAffected = recordsAffected,
            attackVector = attackVector,
            scenarioDetails = scenarioDetails,
            threatActor = threatActor,
            defenseMitigation = defenseMitigation,
            cveReferences = cveReferences
        )
    }

    companion object {
        fun fromEntity(entity: com.example.data.local.AttackScenarioEntity): AttackScenario {
            return AttackScenario(
                id = entity.id,
                organization = entity.organization,
                year = entity.year,
                industry = entity.industry,
                recordsAffected = entity.recordsAffected,
                attackVector = entity.attackVector,
                scenarioDetails = entity.scenarioDetails,
                threatActor = entity.threatActor,
                defenseMitigation = entity.defenseMitigation,
                cveReferences = entity.cveReferences
            )
        }
    }
}

data class ThreatHistoryItem(
    val id: Long = 0,
    val threatId: String,
    val title: String,
    val source: FeedSource,
    val severity: ThreatSeverity,
    val category: ThreatCategory,
    val actionType: String, // "INVESTIGATED", "AI_ANALYSIS", "BOOKMARKED", "REMEDIATION_COPIED", "PUSH_ALERTED", "STATUS_CHANGED"
    val triageStatus: String = "INVESTIGATING", // "INVESTIGATING", "RESOLVED", "FALSE_POSITIVE", "ESCALATED", "MONITORING"
    val timestampMillis: Long = System.currentTimeMillis(),
    val analystNotes: String = ""
)

data class RoadmapResourceLink(
    val title: String,
    val description: String,
    val url: String,
    val tag: String,
    val domainOrProvider: String
)


