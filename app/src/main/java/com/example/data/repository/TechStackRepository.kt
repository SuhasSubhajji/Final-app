package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tech_stack")

class TechStackRepository(private val context: Context) {
    companion object {
        val TECH_STACK_KEY = stringSetPreferencesKey("selected_tech_stack")
    }

    val techStackFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[TECH_STACK_KEY] ?: emptySet()
    }

    suspend fun updateTechStack(newStack: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[TECH_STACK_KEY] = newStack
        }
    }
}
