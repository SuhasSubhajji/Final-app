package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.CveSeverityBreakdownSummary
import com.example.data.model.CveTimelineTrendPoint
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
import com.example.ui.theme.SeverityMedium
import com.example.ui.theme.SeverityLow
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max

enum class CveVisualizationEngine(val title: String) {
    RECHARTS_COMPOSE("Recharts-Compose Hybrid"),
    D3_JS_INTERACTIVE("D3.js SVG Engine")
}

/**
 * Enterprise CVE frequency & severity trend visualization component.
 * Supports both high-performance Recharts/Compose dual-axis canvas rendering
 * and a live D3.js SVG stacked visual engine with real-time CVSS curves.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CveTrendVisualizationCard(
    points7Days: List<CveTimelineTrendPoint>,
    points30Days: List<CveTimelineTrendPoint>,
    summary: CveSeverityBreakdownSummary,
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableIntStateOf(7) }
    var selectedEngine by remember { mutableStateOf(CveVisualizationEngine.RECHARTS_COMPOSE) }
    val points = if (selectedRange == 7) points7Days else points30Days
    var selectedPointIndex by remember { mutableIntStateOf(-1) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cve_trend_visualization_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyberCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CVE Frequency & Severity Trends",
                                color = CyberTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CyberEmerald.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(0.6.dp, CyberEmerald)
                            ) {
                                Text(
                                    text = "D3 / Recharts",
                                    color = CyberEmerald,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Historical distribution across CVSS severity spectrum",
                            color = CyberTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time range & Engine toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Range filters
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedRange == 7,
                        onClick = {
                            selectedRange = 7
                            selectedPointIndex = -1
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
                            selectedPointIndex = -1
                        },
                        label = { Text("30 Days", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan
                        ),
                        modifier = Modifier.height(30.dp)
                    )
                }

                // Engine Switcher
                Row(
                    modifier = Modifier
                        .background(CyberNavySurfaceVariant, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, CyberBorder, shape = RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (selectedEngine == CveVisualizationEngine.RECHARTS_COMPOSE) CyberCyan.copy(alpha = 0.2f) else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedEngine = CveVisualizationEngine.RECHARTS_COMPOSE }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Recharts",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedEngine == CveVisualizationEngine.RECHARTS_COMPOSE) CyberCyan else CyberTextMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                if (selectedEngine == CveVisualizationEngine.D3_JS_INTERACTIVE) CyberEmerald.copy(alpha = 0.2f) else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedEngine = CveVisualizationEngine.D3_JS_INTERACTIVE }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "D3.js",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedEngine == CveVisualizationEngine.D3_JS_INTERACTIVE) CyberEmerald else CyberTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Severity Totals Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberNavyDark, shape = RoundedCornerShape(10.dp))
                    .border(1.dp, CyberBorder, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SeverityStatPill("Critical (9.0+)", summary.criticalTotal, SeverityCritical)
                SeverityStatPill("High (7.0-8.9)", summary.highTotal, SeverityHigh)
                SeverityStatPill("Medium (4.0-6.9)", summary.mediumTotal, SeverityMedium)
                SeverityStatPill("Low (0.1-3.9)", summary.lowTotal, SeverityLow)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Content based on engine
            if (selectedEngine == CveVisualizationEngine.RECHARTS_COMPOSE) {
                RechartsComposeDualAxisChart(
                    points = points,
                    selectedIndex = selectedPointIndex,
                    onSelectIndex = { selectedPointIndex = it }
                )
            } else {
                D3JsWebViewChart(
                    points = points,
                    summary = summary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recent tracked CVE chips
            if (summary.topCveList.isNotEmpty()) {
                Column {
                    Text(
                        text = "Actively Monitored CVE Identifiers:",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        summary.topCveList.forEach { cveId ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CyberNavySurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (cveId.contains("2025") || cveId.contains("40766")) SeverityCritical else CyberCyan)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cveId,
                                        color = CyberTextPrimary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeverityStatPill(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = count.toString(),
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            color = CyberTextMuted,
            fontSize = 9.sp
        )
    }
}

/**
 * High-performance Compose Canvas chart engineered like Recharts:
 * Stacked Bar Frequency (Critical, High, Medium, Low) + Smooth Dual-Axis CVSS Average Severity Line
 */
@Composable
private fun RechartsComposeDualAxisChart(
    points: List<CveTimelineTrendPoint>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    if (points.isEmpty()) return

    val maxTotal = max(points.maxOfOrNull { it.totalCves } ?: 1, 6)

    Column {
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LegendItem("Critical", SeverityCritical)
                LegendItem("High", SeverityHigh)
                LegendItem("Medium", SeverityMedium)
                LegendItem("Low", SeverityLow)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(3.dp)
                        .background(CyberCyan, shape = RoundedCornerShape(1.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Avg CVSS", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            val n = points.size
                            if (n <= 0) return@detectTapGestures
                            val colWidth = size.width / n
                            val tapped = (offset.x / colWidth).toInt().coerceIn(0, n - 1)
                            onSelectIndex(if (selectedIndex == tapped) -1 else tapped)
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val paddingBottom = 24.dp.toPx()
                val chartHeight = height - paddingBottom
                val n = points.size
                if (n == 0) return@Canvas

                val slotWidth = width / n
                val barWidth = slotWidth * 0.45f

                // Draw horizontal grid lines
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

                // Draw Stacked Frequency Bars
                points.forEachIndexed { i, pt ->
                    val centerX = (i * slotWidth) + (slotWidth / 2)
                    val left = centerX - (barWidth / 2)

                    // Heights
                    val critH = (pt.criticalCount.toFloat() / maxTotal) * chartHeight
                    val highH = (pt.highCount.toFloat() / maxTotal) * chartHeight
                    val medH = (pt.mediumCount.toFloat() / maxTotal) * chartHeight
                    val lowH = (pt.lowCount.toFloat() / maxTotal) * chartHeight

                    var currentBottom = chartHeight

                    // Low segment
                    if (lowH > 0) {
                        drawRect(
                            color = SeverityLow,
                            topLeft = Offset(left, currentBottom - lowH),
                            size = Size(barWidth, lowH)
                        )
                        currentBottom -= lowH
                    }

                    // Medium segment
                    if (medH > 0) {
                        drawRect(
                            color = SeverityMedium,
                            topLeft = Offset(left, currentBottom - medH),
                            size = Size(barWidth, medH)
                        )
                        currentBottom -= medH
                    }

                    // High segment
                    if (highH > 0) {
                        drawRect(
                            color = SeverityHigh,
                            topLeft = Offset(left, currentBottom - highH),
                            size = Size(barWidth, highH)
                        )
                        currentBottom -= highH
                    }

                    // Critical segment
                    if (critH > 0) {
                        drawRect(
                            color = SeverityCritical,
                            topLeft = Offset(left, currentBottom - critH),
                            size = Size(barWidth, critH)
                        )
                    }

                    // Selection highlight column
                    if (i == selectedIndex) {
                        drawRect(
                            color = CyberCyan.copy(alpha = 0.12f),
                            topLeft = Offset(i * slotWidth, 0f),
                            size = Size(slotWidth, chartHeight)
                        )
                    }
                }

                // Dual-Axis: CVSS Severity Trend Line (Scale 0 to 10)
                val linePath = Path()
                points.forEachIndexed { i, pt ->
                    val x = (i * slotWidth) + (slotWidth / 2)
                    val normalizedCvss = (pt.averageSeverityScore.coerceIn(0f, 10f) / 10f)
                    val y = chartHeight * (1f - normalizedCvss)

                    if (i == 0) {
                        linePath.moveTo(x, y)
                    } else {
                        val prevX = ((i - 1) * slotWidth) + (slotWidth / 2)
                        val prevNorm = (points[i - 1].averageSeverityScore.coerceIn(0f, 10f) / 10f)
                        val prevY = chartHeight * (1f - prevNorm)
                        val midX = prevX + (x - prevX) / 2
                        linePath.cubicTo(midX, prevY, midX, y, x, y)
                    }
                }

                drawPath(
                    path = linePath,
                    color = CyberCyan,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // CVSS Points Dots
                points.forEachIndexed { i, pt ->
                    val x = (i * slotWidth) + (slotWidth / 2)
                    val normalizedCvss = (pt.averageSeverityScore.coerceIn(0f, 10f) / 10f)
                    val y = chartHeight * (1f - normalizedCvss)
                    val isSelected = i == selectedIndex

                    if (isSelected) {
                        drawCircle(
                            color = CyberCyan.copy(alpha = 0.35f),
                            radius = 8.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }

                    drawCircle(
                        color = if (isSelected) Color.White else CyberCyan,
                        radius = if (isSelected) 4.5.dp.toPx() else 3.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            // Interactive Tooltip (Recharts Floating Style)
            if (selectedIndex in points.indices) {
                val pt = points[selectedIndex]
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                        .background(Color(0xFF0F172A).copy(alpha = 0.95f), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, CyberCyan, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${pt.label} (${pt.dateStr})",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${pt.totalCves} CVEs • Avg CVSS: ${pt.averageSeverityScore}/10",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text("Crit: ${pt.criticalCount}", color = SeverityCritical, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("High: ${pt.highCount}", color = SeverityHigh, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("Med: ${pt.mediumCount}", color = SeverityMedium, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("Low: ${pt.lowCount}", color = SeverityLow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
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
            val sampledLabels = if (points.size <= 7) {
                points.map { it.label }
            } else {
                listOf(
                    points.first().label,
                    points[points.size / 4].label,
                    points[points.size / 2].label,
                    points[3 * points.size / 4].label,
                    points.last().label
                )
            }
            sampledLabels.forEach { label ->
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

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, shape = RoundedCornerShape(1.dp))
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = label, color = CyberTextSecondary, fontSize = 10.sp)
    }
}

/**
 * Interactive D3.js Webview component with full SVG data binding and dual Y axes.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun D3JsWebViewChart(
    points: List<CveTimelineTrendPoint>,
    summary: CveSeverityBreakdownSummary
) {
    val jsonArrayString = remember(points) {
        val array = JSONArray()
        points.forEach { pt ->
            val obj = JSONObject()
            obj.put("label", pt.label)
            obj.put("dateStr", pt.dateStr)
            obj.put("totalCves", pt.totalCves)
            obj.put("criticalCount", pt.criticalCount)
            obj.put("highCount", pt.highCount)
            obj.put("mediumCount", pt.mediumCount)
            obj.put("lowCount", pt.lowCount)
            obj.put("averageSeverityScore", pt.averageSeverityScore)
            val cvesArr = JSONArray()
            pt.cveIds.forEach { cvesArr.put(it) }
            obj.put("cveIds", cvesArr)
            array.put(obj)
        }
        array.toString()
    }

    val html = remember(jsonArrayString) {
        CveTrendChartHtmlBuilder.buildHtml(
            pointsJsonArray = jsonArrayString,
            totalCves = summary.totalCveRecords,
            criticalTotal = summary.criticalTotal,
            highTotal = summary.highTotal,
            mediumTotal = summary.mediumTotal,
            lowTotal = summary.lowTotal
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
            .testTag("d3_cve_trend_webview")
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    setBackgroundColor(0xFF0A0F1D.toInt())
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = false
                        displayZoomControls = false
                    }
                    tag = html
                    loadDataWithBaseURL("https://d3js.org", html, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                if (webView.tag != html) {
                    webView.tag = html
                    webView.loadDataWithBaseURL("https://d3js.org", html, "text/html", "UTF-8", null)
                }
            }
        )
    }
}
