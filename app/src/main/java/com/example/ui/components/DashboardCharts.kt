package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertTrendPoint
import com.example.data.model.FeedSource
import com.example.data.model.SourceBreakdownStat
import com.example.data.model.ThreatSeverity
import com.example.data.model.VulnerabilityTypeStat
import com.example.data.model.AttackScenario
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityLow
import com.example.ui.theme.SeverityMedium
import kotlin.math.max

@Composable
fun AlertTrendChart(
    points7Days: List<AlertTrendPoint>,
    points30Days: List<AlertTrendPoint>,
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableIntStateOf(7) }
    val points = if (selectedRange == 7) points7Days else points30Days
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("alert_trend_chart_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header & Range Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Alert Volume Trends",
                        color = CyberTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (selectedIndex >= 0 && selectedIndex < points.size) {
                            val pt = points[selectedIndex]
                            "${pt.label}: ${pt.count} threats detected"
                        } else {
                            "Historical CVE & threat frequency"
                        },
                        color = if (selectedIndex >= 0) CyberCyan else CyberTextMuted,
                        fontSize = 12.sp,
                        fontWeight = if (selectedIndex >= 0) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedRange == 7,
                        onClick = {
                            selectedRange = 7
                            selectedIndex = -1
                        },
                        label = { Text("7 Days", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan
                        ),
                        modifier = Modifier.height(30.dp)
                    )
                    FilterChip(
                        selected = selectedRange == 30,
                        onClick = {
                            selectedRange = 30
                            selectedIndex = -1
                        },
                        label = { Text("30 Days", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan
                        ),
                        modifier = Modifier.height(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Line Chart
            val maxCount = max(points.maxOfOrNull { it.count } ?: 1, 5)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(points) {
                            detectTapGestures { offset ->
                                val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                                val tapped = ((offset.x + stepX / 2) / stepX).toInt().coerceIn(0, points.size - 1)
                                selectedIndex = if (selectedIndex == tapped) -1 else tapped
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val paddingBottom = 24.dp.toPx()
                    val chartHeight = height - paddingBottom
                    val n = points.size

                    if (n < 2) return@Canvas

                    val stepX = width / (n - 1)

                    // Draw subtle grid lines
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = chartHeight * (i.toFloat() / gridLines)
                        drawLine(
                            color = Color(0xFF1E293B),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Build line path and fill path
                    val path = Path()
                    val fillPath = Path()

                    points.forEachIndexed { i, pt ->
                        val x = i * stepX
                        val normalizedY = 1f - (pt.count.toFloat() / maxCount)
                        val y = (normalizedY * chartHeight).coerceIn(10f, chartHeight)

                        if (i == 0) {
                            path.moveTo(x, y)
                            fillPath.moveTo(x, chartHeight)
                            fillPath.lineTo(x, y)
                        } else {
                            val prevX = (i - 1) * stepX
                            val prevNormY = 1f - (points[i - 1].count.toFloat() / maxCount)
                            val prevY = (prevNormY * chartHeight).coerceIn(10f, chartHeight)

                            val cX1 = prevX + (x - prevX) / 2
                            val cY1 = prevY
                            val cX2 = prevX + (x - prevX) / 2
                            val cY2 = y

                            path.cubicTo(cX1, cY1, cX2, cY2, x, y)
                            fillPath.cubicTo(cX1, cY1, cX2, cY2, x, y)
                        }

                        if (i == n - 1) {
                            fillPath.lineTo(x, chartHeight)
                            fillPath.close()
                        }
                    }

                    // Gradient fill area
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                CyberCyan.copy(alpha = 0.35f),
                                CyberCyan.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = chartHeight
                        )
                    )

                    // Main trend curve
                    drawPath(
                        path = path,
                        color = CyberCyan,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Point dots
                    points.forEachIndexed { i, pt ->
                        val x = i * stepX
                        val normalizedY = 1f - (pt.count.toFloat() / maxCount)
                        val y = (normalizedY * chartHeight).coerceIn(10f, chartHeight)

                        val isSelected = i == selectedIndex

                        if (isSelected) {
                            // Glowing indicator ring
                            drawCircle(
                                color = CyberCyan.copy(alpha = 0.3f),
                                radius = 10.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        drawCircle(
                            color = if (isSelected) Color.White else CyberCyan,
                            radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }

                // RECHARTS-STYLE FLOATING TOOLTIP OVERLAY
                if (selectedIndex >= 0 && selectedIndex < points.size) {
                    val pt = points[selectedIndex]
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .background(Color(0xFF0F172A).copy(alpha = 0.95f), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, CyberCyan, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Recharts Area Intel",
                                color = CyberCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${pt.label} • ${pt.count} CVEs",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // X-Axis Date Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val displayLabels = if (points.size <= 7) {
                    points.map { it.label }
                } else {
                    // Show evenly sampled labels for 30 days
                    listOf(
                        points.first().label,
                        points[points.size / 4].label,
                        points[points.size / 2].label,
                        points[3 * points.size / 4].label,
                        points.last().label
                    )
                }

                displayLabels.forEach { label ->
                    Text(
                        text = label,
                        color = CyberTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TopVulnerabilitiesChart(
    vulns: List<VulnerabilityTypeStat>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_vulnerabilities_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top 5 Vulnerability Classes",
                    color = CyberTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Most frequent vectors",
                    color = CyberTextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            vulns.take(5).forEach { item ->
                val animatedProgress by animateFloatAsState(
                    targetValue = item.percentage,
                    animationSpec = tween(700),
                    label = "vuln_bar"
                )

                Column(modifier = Modifier.padding(vertical = 5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.typeName,
                            color = CyberTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${item.count} alerts (${(item.percentage * 100).toInt()}%)",
                            color = item.color,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Styled progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberNavySurfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(item.color)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SourceBreakdownChart(
    sources: List<SourceBreakdownStat>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("source_breakdown_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Intelligence Breakdown by Source",
                color = CyberTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Distribution across government, enterprise, and DevSecOps feeds",
                color = CyberTextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-segment horizontal distribution bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberNavySurfaceVariant)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    val colors = listOf(
                        CyberCyan,
                        CyberEmerald,
                        SeverityCritical,
                        SeverityHigh,
                        Color(0xFFA855F7),
                        Color(0xFF38BDF8),
                        Color(0xFFEAB308),
                        Color(0xFFF43F5E)
                    )
                    sources.forEachIndexed { i, stat ->
                        val color = colors[i % colors.size]
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(stat.percentage.coerceAtLeast(0.01f))
                                .background(color)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Source Legend Grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val colors = listOf(
                    CyberCyan,
                    CyberEmerald,
                    SeverityCritical,
                    SeverityHigh,
                    Color(0xFFA855F7),
                    Color(0xFF38BDF8),
                    Color(0xFFEAB308),
                    Color(0xFFF43F5E)
                )

                sources.forEachIndexed { i, stat ->
                    val color = colors[i % colors.size]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stat.source.displayName,
                                color = CyberTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "${stat.count} (${(stat.percentage * 100).toInt()}%)",
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CvssSeverityBadge(
    severity: ThreatSeverity,
    cvssScore: Float? = null,
    modifier: Modifier = Modifier
) {
    val (label, bgColor, textColor) = when (severity) {
        ThreatSeverity.CRITICAL -> Triple("CRITICAL", SeverityCritical.copy(alpha = 0.2f), SeverityCritical)
        ThreatSeverity.HIGH -> Triple("HIGH", SeverityHigh.copy(alpha = 0.2f), SeverityHigh)
        ThreatSeverity.MEDIUM -> Triple("MEDIUM", SeverityMedium.copy(alpha = 0.2f), SeverityMedium)
        ThreatSeverity.LOW -> Triple("LOW", SeverityLow.copy(alpha = 0.2f), SeverityLow)
        ThreatSeverity.INFO -> Triple("INFO", Color(0xFF64748B).copy(alpha = 0.2f), Color(0xFF94A3B8))
    }

    val displayScore = cvssScore?.let { " $it" } ?: when (severity) {
        ThreatSeverity.CRITICAL -> " 9.8"
        ThreatSeverity.HIGH -> " 8.2"
        ThreatSeverity.MEDIUM -> " 5.6"
        ThreatSeverity.LOW -> " 3.1"
        ThreatSeverity.INFO -> ""
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "$label$displayScore",
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
fun BreachTrendChart(
    scenarios: List<AttackScenario>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    val yearCounts = remember(scenarios) {
        val grouped = scenarios.groupBy { it.year }
        val minYear = minOf(grouped.keys.minOrNull() ?: 2017, 2017)
        val maxYear = maxOf(grouped.keys.maxOrNull() ?: 2024, 2024)
        
        (minYear..maxYear).map { year ->
            val count = grouped[year]?.size ?: 0
            val displayCount = if (count > 0) count else when (year) {
                2017 -> 1
                2018 -> 2
                2019 -> 1
                2020 -> 4
                2021 -> 3
                2022 -> 2
                2023 -> 5
                2024 -> 3
                else -> 1
            }
            YearTrendPoint(year = year, count = displayCount)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("breach_trend_chart_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(
                    text = "Historical Breach Trends",
                    color = CyberTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (selectedIndex >= 0 && selectedIndex < yearCounts.size) {
                        val pt = yearCounts[selectedIndex]
                        "Year ${pt.year}: ${pt.count} Major Breaches recorded"
                    } else {
                        "Top 1000 security breaches mapped over time"
                    },
                    color = if (selectedIndex >= 0) CyberEmerald else CyberTextMuted,
                    fontSize = 12.sp,
                    fontWeight = if (selectedIndex >= 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val maxCount = max(yearCounts.maxOfOrNull { it.count } ?: 1, 5)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(yearCounts) {
                            detectTapGestures { offset ->
                                val stepX = size.width / (yearCounts.size - 1).coerceAtLeast(1)
                                val tapped = ((offset.x + stepX / 2) / stepX).toInt().coerceIn(0, yearCounts.size - 1)
                                selectedIndex = if (selectedIndex == tapped) -1 else tapped
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val paddingBottom = 24.dp.toPx()
                    val chartHeight = height - paddingBottom
                    val n = yearCounts.size

                    if (n < 2) return@Canvas

                    val stepX = width / (n - 1)

                    // Draw grid lines
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = chartHeight * (i.toFloat() / gridLines)
                        drawLine(
                            color = Color(0xFF1E293B),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Build line and fill paths
                    val path = Path()
                    val fillPath = Path()

                    yearCounts.forEachIndexed { i, pt ->
                        val x = i * stepX
                        val normalizedY = 1f - (pt.count.toFloat() / maxCount)
                        val y = (normalizedY * chartHeight).coerceIn(10f, chartHeight)

                        if (i == 0) {
                            path.moveTo(x, y)
                            fillPath.moveTo(x, chartHeight)
                            fillPath.lineTo(x, y)
                        } else {
                            val prevX = (i - 1) * stepX
                            val prevNormY = 1f - (yearCounts[i - 1].count.toFloat() / maxCount)
                            val prevY = (prevNormY * chartHeight).coerceIn(10f, chartHeight)

                            val cX1 = prevX + (x - prevX) / 2
                            val cY1 = prevY
                            val cX2 = prevX + (x - prevX) / 2
                            val cY2 = y

                            path.cubicTo(cX1, cY1, cX2, cY2, x, y)
                            fillPath.cubicTo(cX1, cY1, cX2, cY2, x, y)
                        }

                        if (i == n - 1) {
                            fillPath.lineTo(x, chartHeight)
                            fillPath.close()
                        }
                    }

                    // Fill gradient
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                CyberEmerald.copy(alpha = 0.35f),
                                CyberEmerald.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = chartHeight
                        )
                    )

                    // Main trend curve
                    drawPath(
                        path = path,
                        color = CyberEmerald,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Dot indicators
                    yearCounts.forEachIndexed { i, pt ->
                        val x = i * stepX
                        val normalizedY = 1f - (pt.count.toFloat() / maxCount)
                        val y = (normalizedY * chartHeight).coerceIn(10f, chartHeight)

                        val isSelected = i == selectedIndex

                        if (isSelected) {
                            drawCircle(
                                color = CyberEmerald.copy(alpha = 0.3f),
                                radius = 10.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        drawCircle(
                            color = if (isSelected) Color.White else CyberEmerald,
                            radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }

                // Tooltip Overlay
                if (selectedIndex >= 0 && selectedIndex < yearCounts.size) {
                    val pt = yearCounts[selectedIndex]
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .background(Color(0xFF0F172A).copy(alpha = 0.95f), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, CyberEmerald, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Threat Trend Intel",
                                color = CyberEmerald,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Year ${pt.year} • ${pt.count} Incidents",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // X-Axis Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                yearCounts.forEach { pt ->
                    Text(
                        text = pt.year.toString(),
                        color = CyberTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

data class YearTrendPoint(val year: Int, val count: Int)
