package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.SecurityAlertEntity
import com.example.data.local.ThreatItemEntity
import com.example.data.model.FeedFilterState
import com.example.data.model.SecurityAlert
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import com.example.data.model.ThreatStats
import com.example.data.remote.FeedFetcherService
import com.example.data.remote.GeminiAnalysisResult
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

class ThreatRepository(
    private val database: AppDatabase,
    private val fetcherService: FeedFetcherService = FeedFetcherService(),
    private val geminiService: GeminiService = GeminiService()
) {
    private val threatDao = database.threatDao()

    val allThreatsFlow: Flow<List<ThreatItem>> = threatDao.getAllThreatsFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    val allAlertsFlow: Flow<List<SecurityAlert>> = threatDao.getAllAlertsFlow().map { entities ->
        entities.map {
            SecurityAlert(
                id = it.id,
                threatItemId = it.threatItemId,
                title = it.title,
                snippet = it.snippet,
                severity = ThreatSeverity.fromString(it.severity),
                timestampMillis = it.timestampMillis,
                isAcknowledged = it.isAcknowledged,
                remediationQuickFix = it.remediationQuickFix
            )
        }
    }

    val allHistoryFlow: Flow<List<com.example.data.model.ThreatHistoryItem>> = threatDao.getAllHistoryFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getHistoryForThreatFlow(threatId: String): Flow<List<com.example.data.model.ThreatHistoryItem>> {
        return threatDao.getHistoryForThreatFlow(threatId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun recordThreatHistory(
        threat: ThreatItem,
        actionType: String,
        triageStatus: String = "INVESTIGATING",
        analystNotes: String = ""
    ): Long {
        val entity = com.example.data.local.ThreatHistoryEntity(
            threatId = threat.id,
            title = threat.title,
            source = threat.source.name,
            severity = threat.severity.name,
            category = threat.category.name,
            actionType = actionType,
            triageStatus = triageStatus,
            timestampMillis = System.currentTimeMillis(),
            analystNotes = analystNotes
        )
        return threatDao.insertHistory(entity)
    }

    suspend fun updateTriageStatusForThreat(threatId: String, status: String, notes: String) {
        threatDao.updateTriageStatusForThreat(threatId, status, notes)
    }

    suspend fun updateHistoryItem(id: Long, status: String, notes: String) {
        threatDao.updateHistoryItem(id, status, notes)
    }

    suspend fun deleteHistoryItem(id: Long) {
        threatDao.deleteHistoryItem(id)
    }

    suspend fun clearAllHistory() {
        threatDao.clearAllHistory()
    }

    suspend fun initializeWithSeedsIfNeeded() {
        val seeds = fetcherService.getCuratedSeedThreats()
        threatDao.insertAll(seeds.map { ThreatItemEntity.fromDomain(it) })

        // Prepopulate baseline investigation history if empty
        val sampleThreat = seeds.firstOrNull()
        if (sampleThreat != null) {
            recordThreatHistory(
                threat = sampleThreat,
                actionType = "INVESTIGATED",
                triageStatus = "INVESTIGATING",
                analystNotes = "Initial SOC telemetry triage completed. High priority alert flagged for remediation."
            )
            if (seeds.size > 1) {
                recordThreatHistory(
                    threat = seeds[1],
                    actionType = "AI_ANALYSIS",
                    triageStatus = "RESOLVED",
                    analystNotes = "Remediation verified: ingress rules restricted and credentials rotated."
                )
            }
        }
    }

    suspend fun refreshFeeds(): Result<Int> {
        return try {
            val remoteItems = fetcherService.fetchAllFeeds()
            val entities = remoteItems.map { ThreatItemEntity.fromDomain(it) }
            threatDao.insertAll(entities)
            Result.success(remoteItems.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getThreatById(id: String): ThreatItem? {
        return threatDao.getThreatById(id)?.toDomain()
    }

    suspend fun toggleBookmark(id: String, currentStatus: Boolean) {
        threatDao.updateBookmark(id, !currentStatus)
    }

    suspend fun markAsRead(id: String) {
        threatDao.updateReadStatus(id, true)
    }

    suspend fun requestAiAnalysis(threat: ThreatItem): GeminiAnalysisResult {
        val result = geminiService.analyzeThreat(threat)
        threatDao.updateAiAnalysis(
            id = threat.id,
            summary = result.summary,
            stepsJson = result.remediationSteps.joinToString(";;;"),
            riskScore = result.riskScore,
            code = result.mitigationCode
        )
        return result
    }

    suspend fun askGeminiQuestion(threat: ThreatItem, question: String): String {
        return geminiService.askCustomQuestion(threat, question)
    }

    suspend fun recordAlert(alert: SecurityAlert) {
        threatDao.insertAlert(
            SecurityAlertEntity(
                id = alert.id,
                threatItemId = alert.threatItemId,
                title = alert.title,
                snippet = alert.snippet,
                severity = alert.severity.name,
                timestampMillis = alert.timestampMillis,
                isAcknowledged = alert.isAcknowledged,
                remediationQuickFix = alert.remediationQuickFix
            )
        )
    }

    suspend fun acknowledgeAlert(alertId: String) {
        threatDao.acknowledgeAlert(alertId)
    }

    suspend fun deleteAlert(alertId: String) {
        threatDao.deleteAlert(alertId)
    }

    fun applyFilter(threats: List<ThreatItem>, filter: FeedFilterState): List<ThreatItem> {
        val now = System.currentTimeMillis()
        val query = filter.searchQuery.trim().lowercase(Locale.ROOT)

        return threats.filter { item ->
            // Search query matches title, description, CVEs, tags, or source name
            val matchesQuery = if (query.isEmpty()) {
                true
            } else {
                item.title.lowercase(Locale.ROOT).contains(query) ||
                item.description.lowercase(Locale.ROOT).contains(query) ||
                item.fullContent.lowercase(Locale.ROOT).contains(query) ||
                item.cveIds.any { it.lowercase(Locale.ROOT).contains(query) } ||
                item.tags.any { it.lowercase(Locale.ROOT).contains(query) } ||
                item.source.displayName.lowercase(Locale.ROOT).contains(query) ||
                item.category.displayName.lowercase(Locale.ROOT).contains(query)
            }

            if (!matchesQuery) return@filter false

            // Source filter
            if (filter.selectedSources.isNotEmpty() && item.source !in filter.selectedSources) {
                return@filter false
            }

            // Severity filter
            if (filter.selectedSeverities.isNotEmpty() && item.severity !in filter.selectedSeverities) {
                return@filter false
            }

            // Category filter
            if (filter.selectedCategories.isNotEmpty() && item.category !in filter.selectedCategories) {
                return@filter false
            }

            // Date range filter
            if (filter.dateRange.durationMillis != null) {
                val cutoff = now - filter.dateRange.durationMillis
                if (item.pubDateMillis < cutoff) {
                    return@filter false
                }
            }

            // Bookmarks only
            if (filter.onlyBookmarks && !item.isBookmarked) {
                return@filter false
            }

            // Unread only
            if (filter.onlyUnread && item.isRead) {
                return@filter false
            }

            true
        }
    }

    val allAttackScenariosFlow: Flow<List<com.example.data.model.AttackScenario>> = threatDao.getAllAttackScenariosFlow().map { entities ->
        entities.map { com.example.data.model.AttackScenario.fromEntity(it) }
    }

    suspend fun loadInitialAttackScenariosIfNeeded() {
        val currentScenarios = geminiService.getLocalSeedAttackScenarios()
        threatDao.insertAttackScenarios(currentScenarios.map { it.toEntity() })
    }

    suspend fun updateAttackScenariosFromGemini(existingCount: Int): Result<Int> {
        return try {
            val newScenarios = geminiService.fetchUpdatedAttackScenarios(existingCount)
            threatDao.insertAttackScenarios(newScenarios.map { it.toEntity() })
            Result.success(newScenarios.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
