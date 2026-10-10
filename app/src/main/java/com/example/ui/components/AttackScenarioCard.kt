package com.example.ui.components

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttackScenario
import com.example.ui.theme.*

@Composable
fun AttackScenarioCard(
    scenario: AttackScenario,
    modifier: Modifier = Modifier,
    onExplainWithGemini: ((AttackScenario) -> Unit)? = null,
    onCveClick: ((String) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        color = CyberNavySurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (expanded) CyberCyan.copy(alpha = 0.8f) else CyberBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("scenario_card_${scenario.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SeverityCritical.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, SeverityCritical.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = scenario.year.toString(),
                                color = SeverityCritical,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = scenario.industry,
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = scenario.organization,
                        color = CyberTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CyberCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = scenario.attackVector.take(28) + if (scenario.attackVector.length > 28) "…" else "",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = CyberEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Impact: ",
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = scenario.recordsAffected,
                    color = CyberTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = CyberBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Step-by-Step Kill Chain Breakdown
                    Text(
                        text = "💥 EXACT ATTACK EXECUTION (WHAT HAPPENED)",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Format bullet points cleanly
                    scenario.scenarioDetails.lines().filter { it.isNotBlank() }.forEach { line ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            val cleanLine = line.trim()
                            val isBullet = cleanLine.startsWith("•") || cleanLine.startsWith("-")
                            val displayText = if (isBullet) cleanLine.drop(1).trim() else cleanLine

                            Surface(
                                shape = CircleShape,
                                color = CyberCyan.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(6.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = displayText,
                                color = CyberTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Threat Actor
                    Text(
                        text = "🕵️ THREAT ACTOR / APT ATTRIBUTION",
                        color = SeverityCritical,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scenario.threatActor,
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Defensive Mitigation
                    Text(
                        text = "🛡️ DEFENSIVE MITIGATION & HARDENING BLUEPRINT",
                        color = CyberEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scenario.defenseMitigation,
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    if (scenario.cveReferences.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "🔗 VULNERABILITY / CVE REFERENCES",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            scenario.cveReferences.split(",").map { it.trim() }.forEach { ref ->
                                val isCve = ref.startsWith("CVE-", ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CyberNavySurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isCve) CyberCyan.copy(alpha = 0.5f) else CyberBorder),
                                    modifier = Modifier
                                        .clickable(enabled = isCve && onCveClick != null) {
                                            val cveToken = ref.split(" ").firstOrNull { it.startsWith("CVE-", ignoreCase = true) } ?: ref
                                            onCveClick?.invoke(cveToken)
                                        }
                                        .testTag("scenario_cve_chip_${ref.replace(" ", "_")}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = ref,
                                            color = if (isCve) CyberCyan else CyberTextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (isCve && onCveClick != null) {
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = "View CVE details",
                                                tint = CyberCyan.copy(alpha = 0.8f),
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (onExplainWithGemini != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { onExplainWithGemini(scenario) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCyan.copy(alpha = 0.15f),
                                contentColor = CyberCyan
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("explain_scenario_${scenario.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Analyze Breach Architecture with Gemini AI",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (!expanded) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tap to view step-by-step kill chain & defense…",
                        color = CyberTextMuted,
                        fontSize = 11.sp
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = CyberTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Collapse",
                        tint = CyberTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
