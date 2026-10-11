package com.example.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class AppUpdateManager(private val context: Context) {

    companion object {
        private const val TAG = "AppUpdateManager"
        private const val PREFS_NAME = "cybershield_update_prefs"
        private const val KEY_DISMISSED_VERSION_CODE = "dismissed_version_code"
        private const val KEY_DISMISSED_VERSION_NAME = "dismissed_version_name"
        private const val KEY_GITHUB_REPO = "github_repo_owner_name"
        private const val KEY_CUSTOM_UPDATE_URL = "custom_update_json_url"
        
        // Default target repository if user pushes to GitHub
        const val DEFAULT_FALLBACK_REPO = "SuhasSubhajji/Final-app"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            
            // Add GitHub Token if available and not a placeholder
            val token = BuildConfig.GITHUB_TOKEN
            if (token.isNotBlank() && token != "MY_GITHUB_TOKEN" && original.url.host.contains("github")) {
                requestBuilder.header("Authorization", "token $token")
            }
            
            chain.proceed(requestBuilder.build())
        }
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val _updateInfo = MutableStateFlow<AppUpdateInfo?>(null)
    val updateInfo: StateFlow<AppUpdateInfo?> = _updateInfo.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

    private val _downloadState = MutableStateFlow<UpdateDownloadState>(UpdateDownloadState.Idle)
    val downloadState: StateFlow<UpdateDownloadState> = _downloadState.asStateFlow()

    private val _configuredRepo = MutableStateFlow(
        prefs.getString(KEY_GITHUB_REPO, DEFAULT_FALLBACK_REPO) ?: DEFAULT_FALLBACK_REPO
    )
    val configuredRepo: StateFlow<String> = _configuredRepo.asStateFlow()

    fun setConfiguredRepo(repo: String) {
        val cleanRepo = repo.trim().removePrefix("https://github.com/").removeSuffix("/")
        prefs.edit().putString(KEY_GITHUB_REPO, cleanRepo).apply()
        _configuredRepo.value = cleanRepo
    }

    internal fun compareVersionNames(v1: String, v2: String): Int {
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

    /**
     * Checks for new APK updates from GitHub release artifacts or custom JSON endpoint.
     * Strictly verifies current versionCode and versionName. Only prompts for update
     * if a genuinely newer build or version is available.
     */
    suspend fun checkForUpdates(isUserInitiated: Boolean = false): UpdateCheckResult = withContext(Dispatchers.IO) {
        _isCheckingUpdate.value = true
        try {
            val repo = _configuredRepo.value.ifBlank { DEFAULT_FALLBACK_REPO }
            
            // Priority 1: GitHub Raw main branch or release metadata app-version.json
            val candidateUrls = listOf(
                "https://raw.githubusercontent.com/$repo/main/app-version.json",
                "https://github.com/$repo/releases/latest/download/app-version.json",
                "https://api.github.com/repos/$repo/releases/latest"
            )

            var detectedInfo: AppUpdateInfo? = null

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
                            if (url.contains("api.github.com")) {
                                // Parse GitHub Releases API response
                                detectedInfo = parseGitHubReleaseApiResponse(bodyString)
                            } else {
                                // Parse custom app-version.json
                                val adapter = moshi.adapter(AppUpdateInfo::class.java)
                                detectedInfo = adapter.fromJson(bodyString)
                            }
                            if (detectedInfo != null) break
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Error checking candidate URL $url: ${e.message}")
                }
            }

            val currentVersionCode = BuildConfig.VERSION_CODE
            val currentVersionName = BuildConfig.VERSION_NAME
            val dismissedVersionCode = prefs.getInt(KEY_DISMISSED_VERSION_CODE, 0)
            val dismissedVersionName = prefs.getString(KEY_DISMISSED_VERSION_NAME, "") ?: ""

            if (detectedInfo != null) {
                val isNewerCode = detectedInfo.versionCode > currentVersionCode
                val isNewerName = compareVersionNames(detectedInfo.versionName, currentVersionName) > 0
                val isNewer = isNewerCode || isNewerName

                val isDismissed = (detectedInfo.versionCode <= dismissedVersionCode || detectedInfo.versionName == dismissedVersionName) && !isUserInitiated

                // Strictly trigger update ONLY if detected version/build is genuinely newer than installed build AND not dismissed
                if (isNewer && !isDismissed) {
                    _updateInfo.value = detectedInfo
                    return@withContext UpdateCheckResult.UpdateAvailable(detectedInfo)
                }
            }
            
            _updateInfo.value = null
            return@withContext UpdateCheckResult.UpToDate(currentVersionName, currentVersionCode)
        } catch (e: Exception) {
            Log.e(TAG, "Failed checking for update: ${e.message}", e)
            _updateInfo.value = null
            return@withContext UpdateCheckResult.Error(e.message ?: "Failed to check update server")
        } finally {
            _isCheckingUpdate.value = false
        }
    }

    /**
     * Downloads the APK file and emits progress updates.
     */
    suspend fun downloadAndInstallUpdate(update: AppUpdateInfo) = withContext(Dispatchers.IO) {
        if (update.downloadUrl.isBlank() || !update.downloadUrl.startsWith("https://", ignoreCase = true)) {
            _downloadState.value = UpdateDownloadState.Error("Security policy violation: Download URL must start with https://")
            return@withContext
        }

        _downloadState.value = UpdateDownloadState.Downloading(0, 0, 0)

        try {
            val request = Request.Builder()
                .url(update.downloadUrl)
                .header("User-Agent", "CyberShield-Android")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful || response.body == null) {
                _downloadState.value = UpdateDownloadState.Error("Failed to connect to download server (HTTP ${response.code})")
                return@withContext
            }

            val body = response.body!!
            val totalBytes = body.contentLength()
            val downloadsDir = File(context.cacheDir, "updates")
            if (!downloadsDir.exists()) downloadsDir.mkdirs()

            val apkFile = File(downloadsDir, "CyberShield-${update.versionName}.apk")
            if (apkFile.exists()) apkFile.delete()

            body.byteStream().use { input ->
                FileOutputStream(apkFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    var totalRead = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        val progressPercent = if (totalBytes > 0) {
                            ((totalRead * 100) / totalBytes).toInt()
                        } else {
                            50
                        }
                        _downloadState.value = UpdateDownloadState.Downloading(
                            progressPercent = progressPercent,
                            downloadedBytes = totalRead,
                            totalBytes = totalBytes
                        )
                    }
                    output.flush()
                }
            }

            _downloadState.value = UpdateDownloadState.ReadyToInstall(apkFile)

            // Trigger prompt to finish installation directly
            withContext(Dispatchers.Main) {
                installApk(apkFile)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Download error: ${e.message}", e)
            _downloadState.value = UpdateDownloadState.Error("Download failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    /**
     * Prompts the system package installer to update/finish installing the APK directly.
     */
    fun installApk(apkFile: File) {
        try {
            if (!apkFile.exists()) {
                Log.e(TAG, "APK file does not exist: ${apkFile.absolutePath}")
                return
            }

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            context.startActivity(installIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed launching system APK installer: ${e.message}", e)
            _downloadState.value = UpdateDownloadState.Error("Installation launch failed: ${e.message}")
        }
    }

    fun dismissUpdate(versionCodeToDismiss: Int? = _updateInfo.value?.versionCode, versionName: String? = _updateInfo.value?.versionName) {
        val code = versionCodeToDismiss ?: _updateInfo.value?.versionCode ?: 0
        val name = versionName ?: _updateInfo.value?.versionName ?: ""
        val editor = prefs.edit()
        if (code > 0) editor.putInt(KEY_DISMISSED_VERSION_CODE, code)
        if (name.isNotBlank()) editor.putString(KEY_DISMISSED_VERSION_NAME, name)
        editor.apply()
        _updateInfo.value = null
        _downloadState.value = UpdateDownloadState.Idle
    }

    private fun parseGitHubReleaseApiResponse(jsonStr: String): AppUpdateInfo? {
        return try {
            val json = org.json.JSONObject(jsonStr)
            val tagName = json.optString("tag_name", "")
            val name = json.optString("name", tagName)
            val body = json.optString("body", "Bug fixes and improvements")
            val publishedAt = json.optString("published_at", "")

            // Look for apk asset
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

            if (apkUrl.isBlank()) {
                apkUrl = json.optString("html_url", "")
            }

            // Extract version code and name
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
