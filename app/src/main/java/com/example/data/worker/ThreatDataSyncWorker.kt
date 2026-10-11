package com.example.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@JsonClass(generateAdapter = true)
data class CisaKevFeed(val vulnerabilities: List<CisaKevVulnerability>)

@JsonClass(generateAdapter = true)
data class CisaKevVulnerability(@Json(name = "cveID") val cveId: String)

class ThreatDataSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 1. Fetch CISA KEV JSON
            val cisaKevJson = URL("https://www.cisa.gov/sites/default/files/feeds/known_exploited_vulnerabilities.json").readText()
            
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(CisaKevFeed::class.java)
            val feed = adapter.fromJson(cisaKevJson)
            
            // 3. Update database
            val db = AppDatabase.getInstance(applicationContext)
            val threatDao = db.threatDao()
            
            feed?.vulnerabilities?.forEach { vuln ->
                threatDao.markThreatsAsCisaKevByCveId(vuln.cveId)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
