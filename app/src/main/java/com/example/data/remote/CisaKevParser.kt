package com.example.data.remote

import com.example.data.model.FeedSource
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import org.json.JSONObject
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

object CisaKevParser {
    fun parse(inputStream: InputStream): List<ThreatItem> {
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(jsonString)
        val vulnerabilities = json.getJSONArray("vulnerabilities")
        val items = mutableListOf<ThreatItem>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        
        for (i in 0 until vulnerabilities.length()) {
            val v = vulnerabilities.getJSONObject(i)
            val cveId = v.getString("cveID")
            val title = v.getString("vulnerabilityName")
            val description = v.getString("shortDescription")
            val dateAdded = v.getString("dateAdded")
            val pubDateMillis = try { sdf.parse(dateAdded)?.time ?: System.currentTimeMillis() } catch (e: Exception) { System.currentTimeMillis() }
            
            items.add(
                ThreatItem(
                    id = "cisa_${cveId.lowercase()}",
                    title = "$cveId: $title",
                    description = description,
                    fullContent = v.optString("notes", description),
                    source = FeedSource.CISA_KEV,
                    link = "https://www.cisa.gov/known-exploited-vulnerabilities-catalog",
                    pubDateMillis = pubDateMillis,
                    severity = ThreatSeverity.CRITICAL, // KEV items are critical by definition
                    category = ThreatCategory.ZERO_DAY,
                    cveIds = listOf(cveId),
                    cisaKev = true
                )
            )
        }
        return items
    }
}
