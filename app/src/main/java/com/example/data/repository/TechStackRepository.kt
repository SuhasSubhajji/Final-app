package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.techStackDataStore: DataStore<Preferences> by preferencesDataStore(name = "tech_stack_prefs")

class TechStackRepository(private val context: Context) {
    private val TECH_STACK_KEY = stringSetPreferencesKey("selected_tech_stack")

    val selectedTechStack: Flow<Set<String>> = context.techStackDataStore.data
        .map { prefs: Preferences ->
            prefs[TECH_STACK_KEY] ?: emptySet()
        }

    suspend fun saveTechStack(stack: Set<String>) {
        context.techStackDataStore.edit { prefs ->
            prefs[TECH_STACK_KEY] = stack
        }
    }

    companion object {
        val ALL_TECH_OPTIONS = mapOf(
            "Operating Systems" to listOf("Windows", "Linux", "macOS", "Android", "iOS"),
            "Languages" to listOf("Kotlin", "Java", "Python", "JavaScript", "Go", "Rust", "C++"),
            "Cloud Providers" to listOf("AWS", "Google Cloud", "Azure"),
            "Web Servers" to listOf("Nginx", "Apache", "IIS"),
            "Databases" to listOf("PostgreSQL", "MySQL", "MongoDB", "Redis", "SQLite")
        )
    }
}
