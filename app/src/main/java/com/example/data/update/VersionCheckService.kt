package com.example.data.update

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class VersionCheckService(private val okHttpClient: OkHttpClient, private val moshi: Moshi) {

    companion object {
        private const val TAG = "VersionCheckService"
    }

    suspend fun checkRemoteVersion(repo: String): UpdateCheckResult {
        val candidateUrls = listOf(
            "https://raw.githubusercontent.com/$repo/main/app-version.json",
            "https://github.com/$repo/releases/latest/download/app-version.json",
            "https://api.github.com/repos/$repo/releases/latest"
        )

        for (url in candidateUrls) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "CyberShield-Android")
                    .build()

                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    if (bodyString.isNotBlank()) {
                        val detectedInfo = if (url.contains("api.github.com")) {
                            parseGitHubReleaseApiResponse(bodyString)
                        } else {
                            moshi.adapter(AppUpdateInfo::class.java).fromJson(bodyString)
                        }

                        Log.d(TAG, "Detected version: $detectedInfo from $url")

                        if (detectedInfo != null) {
                            val currentVersionName = BuildConfig.VERSION_NAME
                            val currentVersionCode = BuildConfig.VERSION_CODE
                            
                            val isNewer = compareVersionNames(detectedInfo.versionName, currentVersionName) > 0
                            
                            Log.d(TAG, "Comparing $detectedInfo.versionName > $currentVersionName, isNewer: $isNewer")
                            
                            if (isNewer) {
                                return UpdateCheckResult.UpdateAvailable(detectedInfo)
                            } else {
                                return UpdateCheckResult.UpToDate(currentVersionName, currentVersionCode)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error checking candidate URL $url: ${e.message}")
            }
        }
        return UpdateCheckResult.Error("Failed to check update server")
    }

    private fun compareVersionNames(v1: String, v2: String): Int {
        val cleanV1 = v1.removePrefix("v").trim()
        val cleanV2 = v2.removePrefix("v").trim()
        val parts1 = cleanV1.split(".").mapNotNull { it.toIntOrNull() }
        val parts2 = cleanV2.split(".").mapNotNull { it.toIntOrNull() }
        val maxLength = maxOf(parts1.size, parts2.size)
        for (i in 0 until maxLength) {
            val p1 = parts1.getOrElse(i) { 0 }
            val p2 = parts2.getOrElse(i) { 0 }
            if (p1 != p2) return p1.compareTo(p2)
        }
        return 0
    }

    private fun parseGitHubReleaseApiResponse(jsonStr: String): AppUpdateInfo? {
        return try {
            val json = org.json.JSONObject(jsonStr)
            val tagName = json.optString("tag_name", "")
            val name = json.optString("name", tagName)
            val body = json.optString("body", "Bug fixes and improvements")
            val publishedAt = json.optString("published_at", "")

            var apkUrl = ""
            val assets = json.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val assetName = asset.optString("name", "")
                    if (assetName.endsWith(".apk", ignoreCase = true)) {
                        apkUrl = asset.optString("browser_download_url", "")
                        break
                    }
                }
            }

            if (apkUrl.isBlank()) apkUrl = json.optString("html_url", "")

            val cleanVersion = tagName.removePrefix("v").trim()
            val parts = cleanVersion.split(".")
            val code = parts.lastOrNull()?.toIntOrNull() ?: 1

            AppUpdateInfo(
                versionCode = code,
                versionName = cleanVersion.ifBlank { name },
                releaseNotes = body,
                downloadUrl = apkUrl,
                publishedAt = publishedAt
            )
        } catch (e: Exception) {
            null
        }
    }
}
