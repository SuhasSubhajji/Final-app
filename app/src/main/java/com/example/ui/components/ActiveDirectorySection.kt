package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.learning.ActiveDirectoryData
import com.example.data.learning.AdAttackToolCommand
import com.example.data.learning.AdTopicModule
import com.example.ui.theme.*

@Composable
fun ActiveDirectorySection(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("🏰 Architecture & Auth", "⚔️ Red Teaming", "🛡️ Tier 0 Defense", "💻 CLI Cheat Sheet", "🔗 Official Links")

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF97316).copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFFF97316),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Active Directory & Red Teaming Mastery",
                                color = CyberTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Kerberos, NTLM, AD CS, Mimikatz & BloodHound",
                                color = Color(0xFFF97316),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SeverityCritical.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SeverityCritical.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "TIER 0 CONTROL",
                            color = SeverityCritical,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Comprehensive mastery of Active Directory architecture, Kerberos authentication mechanics, BloodHound graph pathfinding, offensive red teaming (Kerberoasting, DCSync, Golden Tickets, AD CS ESC1-ESC8), and Enterprise Access Tier 0 defensive hardening.",
                    color = CyberTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Sub-tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberNavySurface, shape = RoundedCornerShape(12.dp))
                .border(1.dp, CyberBorder, shape = RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { idx, label ->
                val selected = selectedTab == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (selected) Color(0xFFF97316).copy(alpha = 0.2f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (selected) Color(0xFFF97316).copy(alpha = 0.6f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedTab = idx }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (selected) Color(0xFFF97316) else CyberTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    AdModulesList(
                        modules = ActiveDirectoryData.modules.filter { it.phaseNumber in 1..2 }
                    )
                    RoadmapResourceLinksSection(
                        sectionTitle = "Active Directory Security & Tier 0 Documentation",
                        accentColor = Color(0xFFF97316),
                        resources = ActiveDirectoryData.resourceLinks
                    )
                }
            }
            1 -> {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    AdModulesList(
                        modules = ActiveDirectoryData.modules.filter { it.phaseNumber in 3..6 }
                    )
                    RoadmapResourceLinksSection(
                        sectionTitle = "Red Team Exploitation Tools & Playbooks",
                        accentColor = Color(0xFFEF4444),
                        resources = ActiveDirectoryData.resourceLinks
                    )
                }
            }
            2 -> AdModulesList(
                modules = ActiveDirectoryData.modules.filter { it.phaseNumber == 7 }
            )
            3 -> AdCheatSheetView(
                commands = ActiveDirectoryData.cheatSheetCommands
            )
            4 -> {
                RoadmapResourceLinksSection(
                    sectionTitle = "Active Directory & Red Teaming Official Tool Repositories",
                    accentColor = Color(0xFFF97316),
                    resources = ActiveDirectoryData.resourceLinks
                )
            }
        }
    }
}

@Composable
private fun AdModulesList(modules: List<AdTopicModule>) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        modules.forEach { module ->
            var expanded by remember { mutableStateOf(false) }

            Surface(
                color = CyberNavySurface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (expanded) Color(0xFFF97316) else CyberBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF97316).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFFF97316).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "PHASE ${module.phaseNumber}: ${module.tag.uppercase()}",
                                color = Color(0xFFF97316),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = CyberTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = module.title,
                        color = CyberTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = module.summary,
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "🔍 DEEP-DIVE TECHNICAL BREAKDOWN",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = module.deepDiveDetails,
                                color = CyberTextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "🛠️ KEY TOOLS & LIBRARIES",
                                color = Color(0xFFF97316),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                module.keyTools.forEach { tool ->
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = CyberNavySurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(0.6.dp, CyberBorder)
                                    ) {
                                        Text(
                                            text = tool,
                                            color = CyberTextSecondary,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            // Code Box
                            Surface(
                                color = CyberNavyDark,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "HANDS-ON CLI COMMAND",
                                            color = CyberEmerald,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("AD Command", module.handsOnCommand)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Command copied to clipboard!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy Command",
                                                tint = CyberEmerald,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = module.handsOnCommand,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "🛡️ DEFENSIVE MITIGATION & DETECTION",
                                color = CyberEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = module.defensiveRemediation,
                                color = CyberTextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdCheatSheetView(commands: List<AdAttackToolCommand>) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Active Directory Red Teaming CLI Cheat Sheet",
            color = CyberTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        commands.forEach { item ->
            Surface(
                color = CyberNavySurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            color = CyberTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF97316).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = item.toolName,
                                color = Color(0xFFF97316),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Command Box
                    Surface(
                        color = CyberNavyDark,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.category.uppercase(),
                                    color = Color(0xFFF97316),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("AD Tool Command", item.command)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Command copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Command",
                                        tint = Color(0xFFF97316),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.command,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Detection: ${item.detectionEventId}",
                        color = SeverityCritical,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
