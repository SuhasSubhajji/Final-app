package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.AttackScenarioEntity
import com.example.data.model.AttackScenario
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BreachRepository(
    private val database: AppDatabase,
    private val geminiService: GeminiService = GeminiService()
) {
    private val threatDao = database.threatDao()

    /**
     * Exposes the flow of cached/stored top security breaches and attack scenarios.
     */
    val allBreachesFlow: Flow<List<AttackScenario>> = threatDao.getAllAttackScenariosFlow().map { entities ->
        entities.map { AttackScenario.fromEntity(it) }
    }

    /**
     * Pre-seeds the database with top security breaches if it is currently empty.
     */
    suspend fun initializeBreachDatabase() {
        val currentScenarios = geminiService.getLocalSeedAttackScenarios()
        threatDao.insertAttackScenarios(currentScenarios.map { it.toEntity() })
    }

    /**
     * Contacts the Gemini Retrofit client to check for, generate, and cache new security breaches.
     */
    suspend fun fetchAndCacheNewBreaches(existingCount: Int): Result<Int> {
        return try {
            val newScenarios = geminiService.fetchUpdatedAttackScenarios(existingCount)
            threatDao.insertAttackScenarios(newScenarios.map { it.toEntity() })
            Result.success(newScenarios.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
