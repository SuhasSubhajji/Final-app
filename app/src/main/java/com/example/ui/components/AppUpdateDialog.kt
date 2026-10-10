package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.update.AppUpdateInfo
import com.example.data.update.UpdateDownloadState
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.SeverityCritical

/**
 * High-priority interactive prompt banner or dialog that appears when a new APK
 * build is detected from GitHub. Allows users to tap "Update Now" which downloads
 * the APK and directly triggers the system package installer to finish the update.
 */
@Composable
fun AppUpdatePromptDialog(
    updateInfo: AppUpdateInfo,
    downloadState: UpdateDownloadState,
    onConfirmUpdate: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("app_update_dialog"),
        shape = RoundedCornerShape(16.dp),
        containerColor = CyberNavySurface,
        icon = {
            Surface(
                shape = CircleShape,
                color = CyberCyan.copy(alpha = 0.2f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = "App Update Available",
                        tint = CyberCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "New Update Available",
                    color = CyberTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberEmerald.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Version ${updateInfo.versionName} (Build ${updateInfo.versionCode})",
                        color = CyberEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "A new build has been pushed to GitHub. Update your CyberShield installation to access the latest cybersecurity features and enhancements directly.",
                    color = CyberTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                if (updateInfo.releaseNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Release Notes:",
                        color = CyberTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyberNavyDark,
                        border = BorderStroke(1.dp, CyberTheme.colors.border),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = updateInfo.releaseNotes.trim(),
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Download / Install Progress State
                when (downloadState) {
                    is UpdateDownloadState.Downloading -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Downloading update...",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${downloadState.progressPercent}%",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { downloadState.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = CyberCyan,
                            trackColor = CyberNavySurfaceVariant
                        )
                    }

                    is UpdateDownloadState.ReadyToInstall -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "✅ Download complete! Prompting package installer to finish update...",
                            color = CyberEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    is UpdateDownloadState.Error -> {
                        val context = LocalContext.current
                        var copiedPkg by remember { mutableStateOf(false) }

                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberNavyDark),
                            border = BorderStroke(1.dp, SeverityCritical.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth().testTag("package_conflict_diagnostic_card")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Package Conflict Warning",
                                        tint = SeverityCritical,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Package Signature Conflict",
                                        color = SeverityCritical,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = downloadState.message,
                                    color = CyberTextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "If you see 'App not installed' or 'Package conflict', the existing installed app was signed with a different key certificate. Uninstall the existing version once to enable seamless automated GitHub updates.",
                                    color = CyberTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = {
                                        val pkgName = context.packageName
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("App Package Name", pkgName))
                                        copiedPkg = true
                                        Toast.makeText(context, "Copied package name: $pkgName", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth().testTag("copy_package_name_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (copiedPkg) "Copied Package Name!" else "Copy Package Name (${context.packageName})",
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    is UpdateDownloadState.Idle -> {
                        // No extra indicator
                    }
                }
            }
        },
        confirmButton = {
            val isDownloading = downloadState is UpdateDownloadState.Downloading
            Button(
                onClick = onConfirmUpdate,
                enabled = !isDownloading,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = CyberNavyDark
                ),
                modifier = Modifier.testTag("confirm_update_button")
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = CyberNavyDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Downloading...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Update Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_update_button")
            ) {
                Text("Later", color = CyberTextMuted, fontSize = 12.sp)
            }
        }
    )
}

/**
 * Inline banner displayed at the top of the Dashboard when an update is available,
 * allowing manual click to trigger update or dismiss.
 */
@Composable
fun AppUpdateBanner(
    updateInfo: AppUpdateInfo,
    onUpdateClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_update_banner_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = CyberCyan.copy(alpha = 0.2f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.NewReleases,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Update Available: v${updateInfo.versionName}",
                    color = CyberCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "A new build from GitHub is ready. Tap to finish update.",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = onUpdateClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = CyberNavyDark
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("banner_update_now_button")
            ) {
                Text("Update", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = CyberTextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Dialog to view or configure the GitHub repository (owner/repo) used for CI/CD update monitoring.
 */
@Composable
fun GitHubUpdateSettingsDialog(
    currentRepo: String,
    currentVersionName: String,
    currentVersionCode: Int,
    isChecking: Boolean,
    onCheckNow: () -> Unit,
    onSaveRepo: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var repoInput by remember { mutableStateOf(currentRepo) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("GitHub CI/CD Updates", color = CyberTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Current App Version: $currentVersionName (Build $currentVersionCode)",
                    color = CyberEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "GitHub Repository (owner/repo):",
                    color = CyberTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = repoInput,
                    onValueChange = { repoInput = it },
                    placeholder = { Text("e.g. suhas4341/CyberShield", color = CyberTextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberNavySurfaceVariant,
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary,
                        focusedContainerColor = CyberNavyDark,
                        unfocusedContainerColor = CyberNavyDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("github_repo_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Whenever a new commit is pushed, GitHub Actions builds the APK and releases it. CyberShield checks this repository and prompts installed devices to finish the update directly.",
                    color = CyberTextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onCheckNow,
                    enabled = !isChecking,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("manual_check_update_button")
                ) {
                    if (isChecking) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberCyan, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Checking GitHub...", fontSize = 12.sp)
                    } else {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Check for Updates Now", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveRepo(repoInput)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = CyberTextMuted)
            }
        },
        containerColor = CyberNavySurface,
        shape = RoundedCornerShape(16.dp)
    )
}
