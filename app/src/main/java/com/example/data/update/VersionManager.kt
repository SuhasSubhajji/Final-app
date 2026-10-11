package com.example.data.update

import com.example.BuildConfig

/**
 * Manages version comparison logic, ensuring updates are only prompted when
 * the remote version is genuinely newer based on both code and name.
 */
class VersionManager {

    /**
     * Returns true if the remote version is strictly newer than the current app version.
     */
    fun isNewerVersion(remoteVersionCode: Int, remoteVersionName: String): Boolean {
        val currentCode = BuildConfig.VERSION_CODE
        val currentName = BuildConfig.VERSION_NAME

        // If code is genuinely higher, it is newer
        if (remoteVersionCode > currentCode) return true
        
        // If code is same, check version name as secondary check
        if (remoteVersionCode == currentCode) {
            return compareVersionNames(remoteVersionName, currentName) > 0
        }

        return false
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
}
