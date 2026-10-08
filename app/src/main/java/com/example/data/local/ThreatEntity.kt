package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.FeedSource
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity

@Entity(tableName = "threat_items")
data class ThreatItemEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val fullContent: String,
    val source: String,
    val link: String,
    val pubDateMillis: Long,
    val severity: String,
    val category: String,
    val cveIdsJson: String,
    val tagsJson: String,
    val isBookmarked: Boolean,
    val isRead: Boolean,
    val aiSummary: String?,
    val aiRemediationStepsJson: String?,
    val aiRiskScore: Int?,
    val aiMitigationCode: String?
) {
    fun toDomain(): ThreatItem {
        val cves = if (cveIdsJson.isBlank()) emptyList() else cveIdsJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val tagList = if (tagsJson.isBlank()) emptyList() else tagsJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val steps = if (aiRemediationStepsJson.isNullOrBlank()) null else aiRemediationStepsJson.split(";;;").map { it.trim() }.filter { it.isNotEmpty() }

        return ThreatItem(
            id = id,
            title = title,
            description = description,
            fullContent = fullContent,
            source = FeedSource.fromString(source),
            link = link,
            pubDateMillis = pubDateMillis,
            severity = ThreatSeverity.fromString(severity),
            category = ThreatCategory.fromString(category),
            cveIds = cves,
            tags = tagList,
            isBookmarked = isBookmarked,
            isRead = isRead,
            aiSummary = aiSummary,
            aiRemediationSteps = steps,
            aiRiskScore = aiRiskScore,
            aiMitigationCode = aiMitigationCode
        )
    }

    companion object {
        fun fromDomain(item: ThreatItem): ThreatItemEntity {
            return ThreatItemEntity(
                id = item.id,
                title = item.title,
                description = item.description,
                fullContent = item.fullContent,
                source = item.source.name,
                link = item.link,
                pubDateMillis = item.pubDateMillis,
                severity = item.severity.name,
                category = item.category.name,
                cveIdsJson = item.cveIds.joinToString(","),
                tagsJson = item.tags.joinToString(","),
                isBookmarked = item.isBookmarked,
                isRead = item.isRead,
                aiSummary = item.aiSummary,
                aiRemediationStepsJson = item.aiRemediationSteps?.joinToString(";;;"),
                aiRiskScore = item.aiRiskScore,
                aiMitigationCode = item.aiMitigationCode
            )
        }
    }
}

@Entity(tableName = "security_alerts")
data class SecurityAlertEntity(
    @PrimaryKey
    val id: String,
    val threatItemId: String,
    val title: String,
    val snippet: String,
    val severity: String,
    val timestampMillis: Long,
    val isAcknowledged: Boolean,
    val remediationQuickFix: String
)

@Entity(tableName = "glossary_terms")
data class GlossaryTermEntity(
    @PrimaryKey
    val term: String,
    val definition: String,
    val industryStandard: String,
    val practicalExample: String,
    val category: String,
    val isAiGenerated: Boolean = false,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "dynamic_roadmap_topics")
data class DynamicRoadmapTopicEntity(
    @PrimaryKey
    val id: String,
    val trackId: String,
    val phaseNumber: Int,
    val title: String,
    val description: String,
    val toolsAndSkillsJson: String,
    val codeSnippet: String,
    val isAiGenerated: Boolean = true,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "attack_scenarios")
data class AttackScenarioEntity(
    @PrimaryKey
    val id: String,
    val organization: String,
    val year: Int,
    val industry: String,
    val recordsAffected: String,
    val attackVector: String,
    val scenarioDetails: String,
    val threatActor: String,
    val defenseMitigation: String,
    val cveReferences: String = "",
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "threat_history")
data class ThreatHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val threatId: String,
    val title: String,
    val source: String,
    val severity: String,
    val category: String,
    val actionType: String,
    val triageStatus: String = "INVESTIGATING",
    val timestampMillis: Long = System.currentTimeMillis(),
    val analystNotes: String = ""
) {
    fun toDomain(): com.example.data.model.ThreatHistoryItem {
        return com.example.data.model.ThreatHistoryItem(
            id = id,
            threatId = threatId,
            title = title,
            source = FeedSource.fromString(source),
            severity = ThreatSeverity.fromString(severity),
            category = ThreatCategory.fromString(category),
            actionType = actionType,
            triageStatus = triageStatus,
            timestampMillis = timestampMillis,
            analystNotes = analystNotes
        )
    }

    companion object {
        fun fromDomain(item: com.example.data.model.ThreatHistoryItem): ThreatHistoryEntity {
            return ThreatHistoryEntity(
                id = item.id,
                threatId = item.threatId,
                title = item.title,
                source = item.source.name,
                severity = item.severity.name,
                category = item.category.name,
                actionType = item.actionType,
                triageStatus = item.triageStatus,
                timestampMillis = item.timestampMillis,
                analystNotes = item.analystNotes
            )
        }
    }
}


