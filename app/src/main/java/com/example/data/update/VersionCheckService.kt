package com.example.data.update

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request

class VersionCheckService(private val okHttpClient: OkHttpClient, private val moshi: Moshi) {

    companion object {
        private const val TAG = "VersionCheckService"
    }

    private val versionManager = VersionManager()

    suspend fun checkRemoteVersion(repo: String): UpdateCheckResult {
        val candidateUrls = listOf(
            "https://github.com/$repo/releases/latest/download/app-version.json",
            "https://api.github.com/repos/$repo/releases/latest",
            "https://raw.githubusercontent.com/$repo/main/app-version.json"
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

                        Log.d(TAG, "Detected version info: $detectedInfo from $url")

                        if (detectedInfo != null && detectedInfo.downloadUrl.isNotBlank()) {
                            val currentVersionName = BuildConfig.VERSION_NAME
                            val currentVersionCode = BuildConfig.VERSION_CODE

                            val isNewer = versionManager.isNewerVersion(detectedInfo.versionCode, detectedInfo.versionName)

                            Log.d(TAG, "Remote: code=${detectedInfo.versionCode}, name=${detectedInfo.versionName} | Local: code=$currentVersionCode, name=$currentVersionName -> isNewer: $isNewer")

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

    private fun parseGitHubReleaseApiResponse(jsonStr: String): AppUpdateInfo? {
        return try {
            val json = org.json.JSONObject(jsonStr)
            val tagName = json.optString("tag_name", "")
            val name = json.optString("name", tagName)
            val body = json.optString("body", "Bug fixes and performance improvements")
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
            val runNum = parts.lastOrNull()?.toIntOrNull() ?: 1

            AppUpdateInfo(
                versionCode = runNum,
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
