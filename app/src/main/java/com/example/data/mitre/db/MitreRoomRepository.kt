package com.example.data.mitre.db

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MitreRoomRepository(context: Context) {
    private val db = MitreDatabase.getInstance(context)
    private val dao = db.mitreDao()
    private val appContext = context.applicationContext

    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        if (dao.getTechniqueCount() == 0) {
            populateFromJsonAsset()
        }
    }

    private suspend fun populateFromJsonAsset() {
        try {
            val jsonString = appContext.assets.open("mitre_attack_d3fend.json")
                .bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            // 1. Tactics
            val tacticsJson = root.optJSONArray("tactics")
            val tacticEntities = mutableListOf<TacticEntity>()
            if (tacticsJson != null) {
                for (i in 0 until tacticsJson.length()) {
                    val obj = tacticsJson.getJSONObject(i)
                    tacticEntities.add(
                        TacticEntity(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            shortname = obj.getString("shortname"),
                            domain = obj.optString("domain", "enterprise"),
                            description = obj.optString("description", "")
                        )
                    )
                }
            }
            dao.insertTactics(tacticEntities)

            // 2. Redirects
            val redirectsJson = root.optJSONObject("redirects")
            val redirectEntities = mutableListOf<RedirectEntity>()
            if (redirectsJson != null) {
                val keys = redirectsJson.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    redirectEntities.add(RedirectEntity(oldId = k, targetId = redirectsJson.getString(k)))
                }
            }
            dao.insertRedirects(redirectEntities)

            // 3. Techniques & CrossRefs & FTS
            val techniquesJson = root.optJSONArray("techniques")
            val techniqueEntities = mutableListOf<TechniqueEntity>()
            val crossRefs = mutableListOf<TechniqueTacticCrossRef>()
            val ftsList = mutableListOf<TechniqueFtsEntity>()

            if (techniquesJson != null) {
                for (i in 0 until techniquesJson.length()) {
                    val obj = techniquesJson.getJSONObject(i)
                    val id = obj.getString("id")
                    val name = obj.getString("name")
                    val domain = obj.optString("domain", "enterprise")
                    val isSub = obj.optBoolean("isSubtechnique", false)
                    val parentId = obj.optString("parentTechniqueId", null).let { if (it.isNullOrBlank()) null else it }
                    val desc = obj.optString("description", "")
                    val url = obj.optString("url", "https://attack.mitre.org/techniques/$id")

                    val platformsArr = obj.optJSONArray("platforms")
                    val platformsList = mutableListOf<String>()
                    if (platformsArr != null) {
                        for (p in 0 until platformsArr.length()) {
                            platformsList.add(platformsArr.getString(p))
                        }
                    }

                    val dataSourcesArr = obj.optJSONArray("dataSources")
                    val dataSourcesList = mutableListOf<String>()
                    if (dataSourcesArr != null) {
                        for (d in 0 until dataSourcesArr.length()) {
                            dataSourcesList.add(dataSourcesArr.getString(d))
                        }
                    }

                    val platformsStr = platformsList.joinToString(", ")
                    val dataSourcesStr = dataSourcesList.joinToString(", ")

                    techniqueEntities.add(
                        TechniqueEntity(
                            id = id,
                            name = name,
                            domain = domain,
                            isSubtechnique = isSub,
                            parentTechniqueId = parentId,
                            platforms = platformsStr,
                            dataSources = dataSourcesStr,
                            description = desc,
                            url = url
                        )
                    )

                    ftsList.add(
                        TechniqueFtsEntity(
                            id = id,
                            name = name,
                            description = desc,
                            platforms = platformsStr
                        )
                    )

                    val tacticsArr = obj.optJSONArray("tactics")
                    if (tacticsArr != null) {
                        for (t in 0 until tacticsArr.length()) {
                            val tacticShort = tacticsArr.getString(t)
                            crossRefs.add(TechniqueTacticCrossRef(techniqueId = id, tacticShortname = tacticShort))
                        }
                    }
                }
            }

            dao.insertTechniques(techniqueEntities)
            dao.insertCrossRefs(crossRefs)
            dao.insertTechniqueFts(ftsList)

            // 4. Procedures
            val procsJson = root.optJSONArray("procedures")
            val procEntities = mutableListOf<ProcedureEntity>()
            if (procsJson != null) {
                for (i in 0 until procsJson.length()) {
                    val obj = procsJson.getJSONObject(i)
                    procEntities.add(
                        ProcedureEntity(
                            id = obj.getString("id"),
                            techniqueId = obj.getString("techniqueId"),
                            sourceType = obj.optString("sourceType", "group"),
                            sourceId = obj.optString("sourceId", ""),
                            sourceName = obj.optString("sourceName", ""),
                            description = obj.optString("description", "")
                        )
                    )
                }
            }
            dao.insertProcedures(procEntities)

            // 5. Detections
            val detsJson = root.optJSONArray("detections")
            val detEntities = mutableListOf<DetectionEntity>()
            if (detsJson != null) {
                for (i in 0 until detsJson.length()) {
                    val obj = detsJson.getJSONObject(i)
                    val analyticsArr = obj.optJSONArray("analytics")
                    val analyticsStr = mutableListOf<String>().apply {
                        if (analyticsArr != null) {
                            for (a in 0 until analyticsArr.length()) add(analyticsArr.getString(a))
                        }
                    }.joinToString(" | ")

                    val dcArr = obj.optJSONArray("dataComponents")
                    val dcStr = mutableListOf<String>().apply {
                        if (dcArr != null) {
                            for (d in 0 until dcArr.length()) add(dcArr.getString(d))
                        }
                    }.joinToString(", ")

                    detEntities.add(
                        DetectionEntity(
                            id = obj.getString("id"),
                            techniqueId = obj.getString("techniqueId"),
                            strategy = obj.optString("strategy", ""),
                            analytics = analyticsStr,
                            dataComponents = dcStr
                        )
                    )
                }
            }
            dao.insertDetections(detEntities)

            // 6. D3FEND Tactics & Techniques
            val d3fendTacticsJson = root.optJSONArray("d3fendTactics")
            val d3fendTacticEntities = mutableListOf<D3fendTacticEntity>()
            if (d3fendTacticsJson != null) {
                for (i in 0 until d3fendTacticsJson.length()) {
                    val obj = d3fendTacticsJson.getJSONObject(i)
                    d3fendTacticEntities.add(
                        D3fendTacticEntity(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            description = obj.optString("description", "")
                        )
                    )
                }
            }
            dao.insertD3fendTactics(d3fendTacticEntities)

            val d3fendTechsJson = root.optJSONArray("d3fendTechniques")
            val d3fendTechEntities = mutableListOf<D3fendTechniqueEntity>()
            if (d3fendTechsJson != null) {
                for (i in 0 until d3fendTechsJson.length()) {
                    val obj = d3fendTechsJson.getJSONObject(i)
                    val artsArr = obj.optJSONArray("artifacts")
                    val artsStr = mutableListOf<String>().apply {
                        if (artsArr != null) {
                            for (a in 0 until artsArr.length()) add(artsArr.getString(a))
                        }
                    }.joinToString(", ")

                    val attackArr = obj.optJSONArray("counteredAttackIds")
                    val attackStr = mutableListOf<String>().apply {
                        if (attackArr != null) {
                            for (a in 0 until attackArr.length()) add(attackArr.getString(a))
                        }
                    }.joinToString(", ")

                    d3fendTechEntities.add(
                        D3fendTechniqueEntity(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            tacticId = obj.optString("tacticId", ""),
                            tacticName = obj.optString("tacticName", ""),
                            description = obj.optString("description", ""),
                            artifacts = artsStr,
                            counteredAttackIds = attackStr,
                            ruleSnippet = obj.optString("ruleSnippet", "")
                        )
                    )
                }
            }
            dao.insertD3fendTechniques(d3fendTechEntities)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTacticsForDomain(domain: String = "enterprise"): Flow<List<TacticEntity>> = dao.getTacticsByDomain(domain)
    fun getTechniquesForTactic(tacticShortname: String): Flow<List<TechniqueEntity>> = dao.getTechniquesForTactic(tacticShortname)
    fun getSubtechniques(parentId: String): Flow<List<TechniqueEntity>> = dao.getSubtechniques(parentId)
    suspend fun getTechniqueById(id: String): TechniqueEntity? {
        val redirect = dao.getRedirectTarget(id)
        val targetId = redirect ?: id
        return dao.getTechniqueById(targetId)
    }
    fun getProceduresForTechnique(techniqueId: String): Flow<List<ProcedureEntity>> = dao.getProceduresForTechnique(techniqueId)
    fun getDetectionsForTechnique(techniqueId: String): Flow<List<DetectionEntity>> = dao.getDetectionsForTechnique(techniqueId)
    fun getMitigationsForTechnique(techniqueId: String): Flow<List<MitigationEntity>> = dao.getMitigationsForTechnique(techniqueId)
    fun getTacticsForTechnique(techniqueId: String): Flow<List<TacticEntity>> = dao.getTacticsForTechnique(techniqueId)
    fun getD3fendTactics(): Flow<List<D3fendTacticEntity>> = dao.getD3fendTactics()
    fun getD3fendTechniquesForTactic(tacticId: String): Flow<List<D3fendTechniqueEntity>> = dao.getD3fendTechniquesForTactic(tacticId)
    fun getD3fendCountermeasuresForAttack(attackId: String): Flow<List<D3fendTechniqueEntity>> = dao.getD3fendCountermeasuresForAttack(attackId)
    fun getAllD3fendTechniques(): Flow<List<D3fendTechniqueEntity>> = dao.getAllD3fendTechniques()
    fun getAllTechniques(): Flow<List<TechniqueEntity>> = dao.getAllTechniques()
    fun searchTechniques(query: String): Flow<List<TechniqueEntity>> = dao.searchTechniquesFts("*$query*")
}
