package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreatDao {
    @Query("SELECT * FROM threat_items ORDER BY pubDateMillis DESC")
    fun getAllThreatsFlow(): Flow<List<ThreatItemEntity>>

    @Query("SELECT * FROM threat_items WHERE id = :id LIMIT 1")
    suspend fun getThreatById(id: String): ThreatItemEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(threats: List<ThreatItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(threat: ThreatItemEntity)

    @Query("UPDATE threat_items SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: String, isBookmarked: Boolean)

    @Query("UPDATE threat_items SET isRead = :isRead WHERE id = :id")
    suspend fun updateReadStatus(id: String, isRead: Boolean)

    @Query("UPDATE threat_items SET aiSummary = :summary WHERE id = :id")
    suspend fun updateAiSummary(id: String, summary: String)

    @Query("""
        UPDATE threat_items 
        SET aiSummary = :summary, 
            aiRemediationStepsJson = :stepsJson, 
            aiRiskScore = :riskScore, 
            aiMitigationCode = :code 
        WHERE id = :id
    """)
    suspend fun updateAiAnalysis(
        id: String,
        summary: String,
        stepsJson: String,
        riskScore: Int,
        code: String
    )

    @Query("SELECT * FROM security_alerts ORDER BY timestampMillis DESC")
    fun getAllAlertsFlow(): Flow<List<SecurityAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: SecurityAlertEntity)

    @Query("UPDATE security_alerts SET isAcknowledged = 1 WHERE id = :alertId")
    suspend fun acknowledgeAlert(alertId: String)

    @Query("DELETE FROM security_alerts WHERE id = :alertId")
    suspend fun deleteAlert(alertId: String)

    @Query("SELECT * FROM glossary_terms ORDER BY term ASC")
    fun getAllGlossaryTermsFlow(): Flow<List<GlossaryTermEntity>>

    @Query("SELECT * FROM glossary_terms WHERE LOWER(term) = LOWER(:term) LIMIT 1")
    suspend fun getGlossaryTerm(term: String): GlossaryTermEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGlossaryTerms(terms: List<GlossaryTermEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGlossaryTerm(term: GlossaryTermEntity)

    @Query("SELECT * FROM dynamic_roadmap_topics WHERE trackId = :trackId ORDER BY phaseNumber ASC")
    fun getDynamicTopicsForTrackFlow(trackId: String): Flow<List<DynamicRoadmapTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDynamicTopics(topics: List<DynamicRoadmapTopicEntity>)

    @Query("SELECT * FROM attack_scenarios ORDER BY timestampMillis DESC")
    fun getAllAttackScenariosFlow(): Flow<List<AttackScenarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttackScenarios(scenarios: List<AttackScenarioEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttackScenario(scenario: AttackScenarioEntity)

    @Query("SELECT * FROM threat_history ORDER BY timestampMillis DESC")
    fun getAllHistoryFlow(): Flow<List<ThreatHistoryEntity>>

    @Query("SELECT * FROM threat_history WHERE threatId = :threatId ORDER BY timestampMillis DESC")
    fun getHistoryForThreatFlow(threatId: String): Flow<List<ThreatHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(historyItem: ThreatHistoryEntity): Long

    @Query("UPDATE threat_history SET triageStatus = :status, analystNotes = :notes WHERE threatId = :threatId")
    suspend fun updateTriageStatusForThreat(threatId: String, status: String, notes: String)

    @Query("UPDATE threat_history SET triageStatus = :status, analystNotes = :notes WHERE id = :id")
    suspend fun updateHistoryItem(id: Long, status: String, notes: String)

    @Query("DELETE FROM threat_history WHERE id = :id")
    suspend fun deleteHistoryItem(id: Long)

    @Query("DELETE FROM threat_history")
    suspend fun clearAllHistory()

    // --- Offline Caching and CVE Lookup Extensions ---
    @Query("SELECT COUNT(*) FROM threat_items")
    fun getThreatCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM threat_items")
    suspend fun getThreatCount(): Int

    @Query("SELECT * FROM threat_items WHERE cveIdsJson LIKE '%' || :cveId || '%' ORDER BY pubDateMillis DESC")
    fun getThreatsByCveFlow(cveId: String): Flow<List<ThreatItemEntity>>

    @Query("SELECT * FROM threat_items WHERE cveIdsJson != '' ORDER BY pubDateMillis DESC")
    fun getAllCveThreatsFlow(): Flow<List<ThreatItemEntity>>

    @Query("SELECT * FROM threat_items WHERE cveIdsJson != '' ORDER BY pubDateMillis DESC")
    suspend fun getAllCveThreats(): List<ThreatItemEntity>

    @Query("SELECT * FROM threat_items ORDER BY pubDateMillis DESC")
    suspend fun getAllThreatsList(): List<ThreatItemEntity>

    @Query("SELECT MAX(pubDateMillis) FROM threat_items")
    suspend fun getLatestCachedTimestamp(): Long?

    @Query("DELETE FROM threat_items")
    suspend fun clearAllThreats()
}
