package com.example.data.mitre.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MitreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTactics(tactics: List<TacticEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechniques(techniques: List<TechniqueEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<TechniqueTacticCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcedures(procedures: List<ProcedureEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMitigations(mitigations: List<MitigationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetections(detections: List<DetectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertD3fendTactics(tactics: List<D3fendTacticEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertD3fendTechniques(techniques: List<D3fendTechniqueEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRedirects(redirects: List<RedirectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechniqueFts(ftsItems: List<TechniqueFtsEntity>)

    @Query("SELECT COUNT(*) FROM mitre_techniques")
    suspend fun getTechniqueCount(): Int

    @Query("SELECT * FROM mitre_tactics WHERE domain = :domain OR :domain = 'all'")
    fun getTacticsByDomain(domain: String = "enterprise"): Flow<List<TacticEntity>>

    @Query("SELECT * FROM mitre_tactics")
    suspend fun getAllTactics(): List<TacticEntity>

    @Query("SELECT * FROM mitre_techniques")
    fun getAllTechniques(): Flow<List<TechniqueEntity>>

    @Query("""
        SELECT t.* FROM mitre_techniques t
        INNER JOIN mitre_technique_tactic_cross_ref ref ON t.id = ref.techniqueId
        WHERE ref.tacticShortname = :tacticShortname
    """)
    fun getTechniquesForTactic(tacticShortname: String): Flow<List<TechniqueEntity>>

    @Query("SELECT * FROM mitre_techniques WHERE parentTechniqueId = :parentId")
    fun getSubtechniques(parentId: String): Flow<List<TechniqueEntity>>

    @Query("SELECT * FROM mitre_techniques WHERE id = :id LIMIT 1")
    suspend fun getTechniqueById(id: String): TechniqueEntity?

    @Query("SELECT * FROM mitre_procedures WHERE techniqueId = :techniqueId")
    fun getProceduresForTechnique(techniqueId: String): Flow<List<ProcedureEntity>>

    @Query("SELECT * FROM mitre_detections WHERE techniqueId = :techniqueId")
    fun getDetectionsForTechnique(techniqueId: String): Flow<List<DetectionEntity>>

    @Query("SELECT * FROM mitre_d3fend_tactics")
    fun getD3fendTactics(): Flow<List<D3fendTacticEntity>>

    @Query("SELECT * FROM mitre_d3fend_techniques")
    fun getAllD3fendTechniques(): Flow<List<D3fendTechniqueEntity>>

    @Query("SELECT * FROM mitre_d3fend_techniques WHERE tacticId = :tacticId")
    fun getD3fendTechniquesForTactic(tacticId: String): Flow<List<D3fendTechniqueEntity>>

    @Query("SELECT * FROM mitre_d3fend_techniques WHERE counteredAttackIds LIKE '%' || :attackId || '%'")
    fun getD3fendCountermeasuresForAttack(attackId: String): Flow<List<D3fendTechniqueEntity>>

    @Query("SELECT targetId FROM mitre_redirects WHERE oldId = :oldId LIMIT 1")
    suspend fun getRedirectTarget(oldId: String): String?

    @Query("SELECT * FROM mitre_mitigations WHERE techniqueIds LIKE '%' || :techniqueId || '%'")
    fun getMitigationsForTechnique(techniqueId: String): Flow<List<MitigationEntity>>

    @Query("""
        SELECT t.* FROM mitre_tactics t
        INNER JOIN mitre_technique_tactic_cross_ref ref ON t.shortname = ref.tacticShortname
        WHERE ref.techniqueId = :techniqueId
    """)
    fun getTacticsForTechnique(techniqueId: String): Flow<List<TacticEntity>>

    // Full-Text Search
    @Query("""
        SELECT t.* FROM mitre_techniques t
        JOIN mitre_techniques_fts fts ON t.id = fts.id
        WHERE mitre_techniques_fts MATCH :query
    """)
    fun searchTechniquesFts(query: String): Flow<List<TechniqueEntity>>
}
