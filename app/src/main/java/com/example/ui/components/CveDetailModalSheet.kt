package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AffectedSoftware
import com.example.data.model.CveDetail
import com.example.data.model.CveExploitationStatus
import com.example.data.model.CveExternalReference
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import com.example.ui.theme.CyberBorder
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
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityLow
import com.example.ui.theme.SeverityMedium

/**
 * High-fidelity Modal Bottom Sheet displaying comprehensive, authoritative
 * details for a specific CVE vulnerability:
 * - CVSS v3.1 score and interactive severity meter
 * - Vector string & CWE taxonomy
 * - Active exploitation status (CISA KEV in-the-wild tracking)
 * - Affected software packages, versions, and vendor fixed release
 * - Remediation guidance and patch recommendations
 * - Direct external references with system browser launch (NVD, KEV, Vendor Advisory, Exploit-DB)
 * - One-touch clipboard copy for SOC tickets and Jira issues
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CveDetailModalSheet(
    cveDetail: CveDetail,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    matchingThreat: ThreatItem? = null,
    onViewThreatDetail: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val theme = CyberTheme.colors

    var copiedVector by remember { mutableStateOf(false) }
    var copiedCve by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = CyberNavyDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(CyberCyan.copy(alpha = 0.5f))
            )
        },
        modifier = modifier.testTag("cve_detail_bottom_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar: CVE ID, Severity Badge & Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CyberCyan.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = cveDetail.cveId,
                                        color = CyberTextPrimary,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.testTag("cve_detail_identifier_title")
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("CVE ID", cveDetail.cveId))
                                            copiedCve = true
                                            Toast.makeText(context, "Copied ${cveDetail.cveId} to clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp).testTag("copy_cve_id_button")
                                    ) {
                                        Icon(
                                            imageVector = if (copiedCve) Icons.Default.Check else Icons.Default.ContentCopy,
                                            contentDescription = "Copy CVE",
                                            tint = if (copiedCve) CyberEmerald else CyberTextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Published: ${cveDetail.publishedDate} • Updated: ${cveDetail.lastModifiedDate}",
                                    color = CyberTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(32.dp).testTag("close_cve_sheet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Sheet",
                            tint = CyberTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // CVSS Score & Threat Meter Card
            item {
                CvssScoreHeroCard(
                    cve = cveDetail,
                    onCopyVector = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("CVSS Vector", cveDetail.cvssVector))
                        copiedVector = true
                        Toast.makeText(context, "Copied CVSS Vector to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    copiedVector = copiedVector
                )
            }

            // Active Exploitation & Threat Intelligence Status Pill
            item {
                ExploitationStatusBanner(status = cveDetail.exploitationStatus)
            }

            // Advisory Title & Technical Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("cve_summary_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                    border = BorderStroke(1.dp, CyberBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = cveDetail.title,
                            color = CyberTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = cveDetail.description,
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CyberCyan.copy(alpha = 0.12f),
                                border = BorderStroke(0.6.dp, CyberCyan.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "${cveDetail.cweId}: ${cveDetail.cweName}",
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Affected Software & Version Matrices
            item {
                Text(
                    text = "AFFECTED SOFTWARE VERSIONS & FIXES",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
            }

            items(cveDetail.affectedSoftware) { item ->
                AffectedSoftwareCard(software = item)
            }

            // Remediation & Vendor Patch Recommendations
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("cve_remediation_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Remediation & Patch Guidance",
                                color = CyberEmerald,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = cveDetail.remediationGuidance,
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberEmerald.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cveDetail.patchAvailability,
                                    color = CyberEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Matching Threat Item Navigation (if advisory exists in app feed)
            if (matchingThreat != null && onViewThreatDetail != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CyberCyan.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDismissRequest()
                                onViewThreatDetail(matchingThreat.id)
                            }
                            .testTag("view_matching_threat_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Investigate Linked Advisory in CyberShield",
                                    color = CyberCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = matchingThreat.title,
                                    color = CyberTextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open Advisory",
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Authoritative External References & Links
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AUTHORITATIVE EXTERNAL REFERENCES",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = "${cveDetail.externalReferences.size} sources",
                        color = CyberTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            items(cveDetail.externalReferences) { ref ->
                ExternalReferenceItem(
                    reference = ref,
                    onOpen = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ref.url)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable to launch browser: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Footer Share / Export Button
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareText = buildString {
                                appendLine("🚨 ${cveDetail.cveId}: ${cveDetail.title}")
                                appendLine("CVSS Score: ${cveDetail.cvssScore} (${cveDetail.cvssSeverity.name})")
                                appendLine("Vector: ${cveDetail.cvssVector}")
                                appendLine("Status: ${cveDetail.exploitationStatus.label}")
                                appendLine()
                                appendLine("Summary: ${cveDetail.description}")
                                appendLine()
                                appendLine("Affected: ${cveDetail.affectedSoftware.joinToString { "${it.vendor} ${it.product} (${it.affectedVersions})" }}")
                                appendLine("Remediation: ${cveDetail.remediationGuidance}")
                                appendLine()
                                appendLine("Authoritative Reference: ${cveDetail.externalReferences.firstOrNull()?.url ?: ""}")
                                appendLine()
                                appendLine("Sent via CyberShield SOC Intelligence")
                            }

                            try {
                                val intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_SUBJECT, "Security Advisory: ${cveDetail.cveId}")
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(intent, "Share CVE Dossier"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "Share unavailable: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("share_cve_dossier_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyberCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Dossier", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("done_cve_button")
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * Hero Card displaying the large CVSS v3.1 score, severity classification,
 * progress bar gauge, and vector string.
 */
@Composable
private fun CvssScoreHeroCard(
    cve: CveDetail,
    onCopyVector: () -> Unit,
    copiedVector: Boolean,
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors
    val scoreRatio = (cve.cvssScore / 10f).coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(targetValue = scoreRatio, animationSpec = tween(700), label = "cvss_ratio")

    val (badgeBg, badgeText, gradient) = when (cve.cvssSeverity) {
        ThreatSeverity.CRITICAL -> Triple(SeverityCritical.copy(alpha = 0.2f), SeverityCritical, Brush.horizontalGradient(listOf(SeverityHigh, SeverityCritical)))
        ThreatSeverity.HIGH -> Triple(SeverityHigh.copy(alpha = 0.2f), SeverityHigh, Brush.horizontalGradient(listOf(SeverityMedium, SeverityHigh)))
        ThreatSeverity.MEDIUM -> Triple(SeverityMedium.copy(alpha = 0.2f), SeverityMedium, Brush.horizontalGradient(listOf(CyberCyan, SeverityMedium)))
        ThreatSeverity.LOW -> Triple(SeverityLow.copy(alpha = 0.2f), SeverityLow, Brush.horizontalGradient(listOf(CyberEmerald, SeverityLow)))
        ThreatSeverity.INFO -> Triple(CyberTextMuted.copy(alpha = 0.2f), CyberTextMuted, Brush.horizontalGradient(listOf(CyberTextMuted, CyberTextSecondary)))
    }

    Card(
        modifier = modifier.fillMaxWidth().testTag("cvss_hero_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = BorderStroke(1.dp, badgeText.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CVSS v3.1 BASE SCORE",
                        color = CyberTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", cve.cvssScore),
                            color = badgeText,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.testTag("cvss_score_text")
                        )
                        Text(
                            text = " / 10.0",
                            color = CyberTextMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg,
                    border = BorderStroke(1.dp, badgeText.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(badgeText)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cve.cvssSeverity.name,
                            color = badgeText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.testTag("cvss_severity_badge_label")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Progress Meter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CyberNavySurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedRatio)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(gradient)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vector String with one-click copy
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberNavyDark,
                border = BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth().clickable { onCopyVector() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Vector String:",
                            color = CyberTextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = cve.cvssVector,
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (copiedVector) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy Vector",
                        tint = if (copiedVector) CyberEmerald else CyberTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Exploitation status indicator banner (Active in KEV, Public PoC, Patched).
 */
@Composable
private fun ExploitationStatusBanner(
    status: CveExploitationStatus,
    modifier: Modifier = Modifier
) {
    val containerBg = status.badgeColor.copy(alpha = 0.12f)
    val borderCol = status.badgeColor.copy(alpha = 0.5f)

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerBg,
        border = BorderStroke(1.dp, borderCol),
        modifier = modifier.fillMaxWidth().testTag("exploitation_status_banner")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(status.badgeColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "EXPLOIT INTELLIGENCE STATUS",
                    color = status.badgeColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = status.label,
                    color = CyberTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Card detailing a vendor product, affected versions range, and fixed release.
 */
@Composable
private fun AffectedSoftwareCard(
    software: AffectedSoftware,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${software.vendor} • ${software.product}",
                    color = CyberTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CyberNavySurfaceVariant
                ) {
                    Text(
                        text = software.packageEcosystem,
                        color = CyberTextMuted,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Affected Versions:", color = CyberTextMuted, fontSize = 11.sp)
                    Text(
                        text = software.affectedVersions,
                        color = SeverityCritical,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Fixed Version:", color = CyberTextMuted, fontSize = 11.sp)
                    Text(
                        text = software.fixedVersion,
                        color = CyberEmerald,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Interactive card representing an external authoritative reference link.
 */
@Composable
private fun ExternalReferenceItem(
    reference: CveExternalReference,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = CyberNavySurface,
        border = BorderStroke(1.dp, CyberBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("external_ref_${reference.tag.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberCyan.copy(alpha = 0.12f),
                    border = BorderStroke(0.6.dp, CyberCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = reference.tag,
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = reference.title,
                        color = CyberTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = reference.url,
                        color = CyberTextMuted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Open Link",
                tint = CyberCyan,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
