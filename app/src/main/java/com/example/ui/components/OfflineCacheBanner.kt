package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OfflineCacheStatus
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

/**
 * Tactical badge displaying offline Room database cache health, cached items count,
 * and live sync state for threat intelligence.
 */
@Composable
fun OfflineCacheBanner(
    cacheStatus: OfflineCacheStatus,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("offline_cache_status_banner"),
        color = CyberNavySurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (cacheStatus.isOfflineMode) Color(0xFFF59E0B).copy(alpha = 0.6f) else CyberCyan.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (cacheStatus.isOfflineMode) Color(0xFFF59E0B).copy(alpha = 0.2f)
                                else CyberEmerald.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                cacheStatus.isSyncing -> Icons.Default.CloudSync
                                cacheStatus.isOfflineMode -> Icons.Default.CloudOff
                                else -> Icons.Default.CloudDone
                            },
                            contentDescription = "Room Cache Status",
                            tint = if (cacheStatus.isOfflineMode) Color(0xFFF59E0B) else CyberEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (cacheStatus.isOfflineMode) "OFFLINE CACHE ACTIVE" else "ROOM LOCAL CACHE READY",
                                color = if (cacheStatus.isOfflineMode) Color(0xFFF59E0B) else CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CyberCyan.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${cacheStatus.totalCachedThreats} Cached",
                                    color = CyberCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Text(
                            text = if (cacheStatus.isOfflineMode)
                                "Network offline • All intel & CVEs served instantly from local Room DB"
                            else
                                "Synced ${cacheStatus.formattedLastSync} • ${cacheStatus.totalCachedCves} CVE advisories available offline",
                            color = CyberTextSecondary,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }

                IconButton(
                    onClick = onSyncClick,
                    enabled = !cacheStatus.isSyncing,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("sync_cache_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Synchronize cache",
                        tint = if (cacheStatus.isSyncing) CyberTextMuted else CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (cacheStatus.isSyncing) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = CyberCyan,
                    trackColor = CyberNavySurfaceVariant
                )
            }
        }
    }
}
