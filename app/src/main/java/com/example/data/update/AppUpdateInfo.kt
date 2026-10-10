package com.example.data.update

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppUpdateInfo(
    @Json(name = "versionCode") val versionCode: Int = 0,
    @Json(name = "versionName") val versionName: String = "1.0.0",
    @Json(name = "releaseNotes") val releaseNotes: String = "",
    @Json(name = "downloadUrl") val downloadUrl: String = "",
    @Json(name = "publishedAt") val publishedAt: String = ""
)

sealed class UpdateDownloadState {
    data object Idle : UpdateDownloadState()
    data class Downloading(val progressPercent: Int, val downloadedBytes: Long, val totalBytes: Long) : UpdateDownloadState()
    data class ReadyToInstall(val apkFile: java.io.File) : UpdateDownloadState()
    data class Error(val message: String) : UpdateDownloadState()
}

sealed class UpdateCheckResult {
    data class UpdateAvailable(val updateInfo: AppUpdateInfo) : UpdateCheckResult()
    data class UpToDate(val versionName: String, val versionCode: Int) : UpdateCheckResult()
    data class Error(val message: String) : UpdateCheckResult()
}

