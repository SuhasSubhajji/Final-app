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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.learning.AdTopicModule
import com.example.ui.theme.*

@Composable
fun ActiveDirectoryAuthSection(
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf("🏛️ Domain Foundations", "🔑 Kerberos Protocols", "🎫 Ticket Mechanics", "🛡️ Hardening & Tier 0")

    val authModules = remember {
        ActiveDirectoryData.modules.filter { 
            it.id in listOf("ad_arch_foundations", "ad_kerberos_auth", "ad_ticket_forging", "ad_tiered_hardening")
        }
    }

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
                                text = "Active Directory Architecture & Auth",
                                color = CyberTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Kerberos Handshake, NTLMv2, PAC & Domain Tier 0",
                                color = Color(0xFFF97316),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF97316).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "TIER 0 IDENTITY",
                            color = Color(0xFFF97316),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Core directory architecture, NTDS.dit mechanics, Kerberos 3-way handshake (AS-REQ/AS-REP, TGS-REQ/TGS-REP, AP-REQ), Ticket Granting Tickets (TGT), Privilege Attribute Certificate (PAC) signing, and Tiered Administration.",
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        // Sub-tabs
        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = CyberNavySurfaceVariant,
            contentColor = Color(0xFFF97316),
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = Color(0xFFF97316)
                )
            }
        ) {
            subTabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedSubTab == idx,
                    onClick = { selectedSubTab = idx },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedSubTab == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSubTab == idx) Color(0xFFF97316) else CyberTextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_ad_auth_$idx")
                )
            }
        }

        // Content
        when (selectedSubTab) {
            0 -> AdModuleCard(module = authModules.firstOrNull { it.id == "ad_arch_foundations" } ?: authModules[0])
            1 -> AdModuleCard(module = authModules.firstOrNull { it.id == "ad_kerberos_auth" } ?: authModules[0])
            2 -> AdModuleCard(module = authModules.firstOrNull { it.id == "ad_ticket_forging" } ?: authModules[0])
            3 -> AdModuleCard(module = authModules.firstOrNull { it.id == "ad_tiered_hardening" } ?: authModules[0])
        }

        RoadmapResourceLinksSection(
            sectionTitle = "Active Directory Official Architecture & Hardening Links",
            accentColor = Color(0xFFF97316),
            resources = ActiveDirectoryData.resourceLinks
        )
    }
}

@Composable
fun AdModuleCard(module: AdTopicModule) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(true) }

    Surface(
        color = CyberNavySurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
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

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = CyberTextMuted
                    )
                }
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
                        text = "🔍 ARCHITECTURE & PROTOCOL MECHANICS",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = module.deepDiveDetails,
                        color = CyberTextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "⚙️ KEY ADMINISTRATIVE UTILITIES",
                        color = CyberEmerald,
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
                                    color = CyberEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💻 POWERSHELL / COMMAND EXECUTION",
                            color = Color(0xFFF97316),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Command", module.handsOnCommand)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Command copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Command",
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = CyberNavyDark,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = module.handsOnCommand,
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DEFENSIVE RECOMMENDATION",
                                    color = CyberEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = module.defensiveRemediation,
                                color = CyberTextPrimary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
