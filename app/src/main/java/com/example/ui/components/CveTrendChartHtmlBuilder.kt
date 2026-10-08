package com.example.ui.components

import org.json.JSONArray
import org.json.JSONObject

/**
 * Builds self-contained HTML using D3.js and SVG stacked charts to visualize
 * CVE frequency and severity trends over time.
 * Provides interactive tooltips, stacked frequency bars (Critical/High/Medium/Low),
 * dual-axis CVSS average severity line, and live filtering toggles.
 */
object CveTrendChartHtmlBuilder {

    fun buildHtml(
        pointsJsonArray: String,
        totalCves: Int,
        criticalTotal: Int,
        highTotal: Int,
        mediumTotal: Int,
        lowTotal: Int
    ): String {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=2.0, user-scalable=yes">
  <title>CVE Trend Analysis - D3 / Recharts Engine</title>
  <script src="https://d3js.org/d3.v7.min.js"></script>
  <style>
    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
      -webkit-tap-highlight-color: transparent;
      user-select: none;
    }
    body {
      background: #0A0F1D;
      color: #F8FAFC;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", sans-serif;
      padding: 12px;
      overflow-x: hidden;
    }
    .chart-card {
      background: #0F172A;
      border: 1px solid #1E293B;
      border-radius: 12px;
      padding: 12px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.5);
    }
    .header-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
    }
    .badge {
      display: inline-flex;
      align-items: center;
      background: rgba(6, 182, 212, 0.15);
      border: 1px solid #06B6D4;
      color: #06B6D4;
      font-size: 10px;
      font-weight: 700;
      padding: 3px 8px;
      border-radius: 6px;
      letter-spacing: 0.5px;
    }
    .legend-row {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      margin: 8px 0 12px 0;
      font-size: 11px;
    }
    .legend-item {
      display: flex;
      align-items: center;
      gap: 5px;
      cursor: pointer;
    }
    .legend-dot {
      width: 8px;
      height: 8px;
      border-radius: 2px;
    }
    .line-dot {
      width: 14px;
      height: 3px;
      border-radius: 2px;
      background: #38BDF8;
    }
    #chart-svg-container {
      width: 100%;
      height: 220px;
      position: relative;
    }
    svg {
      width: 100%;
      height: 100%;
      overflow: visible;
    }
    .axis line, .axis path {
      stroke: #334155;
    }
    .axis text {
      fill: #94A3B8;
      font-size: 10px;
      font-family: monospace;
    }
    .grid line {
      stroke: #1E293B;
      stroke-dasharray: 2, 2;
    }
    .tooltip-box {
      position: absolute;
      background: rgba(15, 23, 42, 0.95);
      border: 1px solid #06B6D4;
      border-radius: 8px;
      padding: 8px 10px;
      font-size: 11px;
      color: #F8FAFC;
      pointer-events: none;
      opacity: 0;
      transition: opacity 0.15s ease-in-out;
      box-shadow: 0 4px 14px rgba(0,0,0,0.7);
      z-index: 100;
      min-width: 150px;
    }
    .tooltip-title {
      color: #06B6D4;
      font-weight: 700;
      margin-bottom: 4px;
      border-bottom: 1px solid #334155;
      padding-bottom: 2px;
      font-size: 11px;
    }
    .tooltip-row {
      display: flex;
      justify-content: space-between;
      margin: 2px 0;
    }
  </style>
</head>
<body>
  <div class="chart-card">
    <div class="header-row">
      <div>
        <div style="font-size: 13px; font-weight: 700; color: #F8FAFC;">D3 / Recharts Trend Engine</div>
        <div style="font-size: 10px; color: #94A3B8;">Stacked Severity Frequencies & CVSS Score Over Time</div>
      </div>
      <span class="badge">D3.v7 LIVE</span>
    </div>

    <!-- Legend -->
    <div class="legend-row">
      <div class="legend-item"><div class="legend-dot" style="background: #F43F5E;"></div><span>Critical</span></div>
      <div class="legend-item"><div class="legend-dot" style="background: #FB923C;"></div><span>High</span></div>
      <div class="legend-item"><div class="legend-dot" style="background: #FBBF24;"></div><span>Medium</span></div>
      <div class="legend-item"><div class="legend-dot" style="background: #10B981;"></div><span>Low</span></div>
      <div class="legend-item"><div class="line-dot"></div><span>Avg CVSS</span></div>
    </div>

    <div id="chart-svg-container">
      <div id="tooltip" class="tooltip-box"></div>
    </div>
  </div>

  <script>
    const data = $pointsJsonArray;

    function renderChart() {
      const container = document.getElementById("chart-svg-container");
      container.innerHTML = '<div id="tooltip" class="tooltip-box"></div>';
      const tooltip = document.getElementById("tooltip");

      const width = container.clientWidth || 340;
      const height = 220;
      const margin = { top: 14, right: 34, bottom: 26, left: 28 };

      const svg = d3.select("#chart-svg-container")
        .append("svg")
        .attr("viewBox", `0 0 ${'$'}{width} ${'$'}{height}`)
        .attr("preserveAspectRatio", "xMidYMid meet");

      // Scales
      const x = d3.scaleBand()
        .domain(data.map(d => d.label))
        .range([margin.left, width - margin.right])
        .padding(0.35);

      const maxFrequency = d3.max(data, d => (d.criticalCount + d.highCount + d.mediumCount + d.lowCount)) || 10;
      const yLeft = d3.scaleLinear()
        .domain([0, Math.max(maxFrequency + 2, 8)])
        .nice()
        .range([height - margin.bottom, margin.top]);

      const yRight = d3.scaleLinear()
        .domain([0, 10])
        .range([height - margin.bottom, margin.top]);

      // Grid
      svg.append("g")
        .attr("class", "grid")
        .call(d3.axisLeft(yLeft)
          .ticks(4)
          .tickSize(- (width - margin.left - margin.right))
          .tickFormat("")
        )
        .attr("transform", `translate(${'$'}{margin.left}, 0)`);

      // X Axis
      svg.append("g")
        .attr("class", "axis")
        .attr("transform", `translate(0, ${'$'}{height - margin.bottom})`)
        .call(d3.axisBottom(x).tickSize(3))
        .selectAll("text")
        .attr("fill", "#94A3B8");

      // Left Y Axis (Count)
      svg.append("g")
        .attr("class", "axis")
        .attr("transform", `translate(${'$'}{margin.left}, 0)`)
        .call(d3.axisLeft(yLeft).ticks(4));

      // Right Y Axis (CVSS)
      svg.append("g")
        .attr("class", "axis")
        .attr("transform", `translate(${'$'}{width - margin.right}, 0)`)
        .call(d3.axisRight(yRight).ticks(4).tickFormat(d => d + "s"));

      // Stacked Bar Data
      const stack = d3.stack()
        .keys(["lowCount", "mediumCount", "highCount", "criticalCount"]);

      const colorMap = {
        lowCount: "#10B981",
        mediumCount: "#FBBF24",
        highCount: "#FB923C",
        criticalCount: "#F43F5E"
      };

      const series = stack(data);

      // Render Stacked Bars
      svg.append("g")
        .selectAll("g")
        .data(series)
        .join("g")
        .attr("fill", d => colorMap[d.key])
        .selectAll("rect")
        .data(d => d)
        .join("rect")
        .attr("x", d => x(d.data.label))
        .attr("y", d => yLeft(d[1]))
        .attr("height", d => Math.max(0, yLeft(d[0]) - yLeft(d[1])))
        .attr("width", x.bandwidth())
        .attr("rx", 2)
        .on("mouseenter", function(event, d) {
          d3.select(this).attr("opacity", 0.8);
          const pt = d.data;
          tooltip.innerHTML = `
            <div class="tooltip-title">${'$'}{pt.label} (${'$'}{pt.dateStr})</div>
            <div class="tooltip-row"><span style="color:#F43F5E;">Critical:</span><b>${'$'}{pt.criticalCount}</b></div>
            <div class="tooltip-row"><span style="color:#FB923C;">High:</span><b>${'$'}{pt.highCount}</b></div>
            <div class="tooltip-row"><span style="color:#FBBF24;">Medium:</span><b>${'$'}{pt.mediumCount}</b></div>
            <div class="tooltip-row"><span style="color:#10B981;">Low:</span><b>${'$'}{pt.lowCount}</b></div>
            <div class="tooltip-row" style="border-top:1px solid #334155; margin-top:3px; padding-top:2px;">
              <span style="color:#38BDF8;">Avg CVSS:</span><b>${'$'}{pt.averageSeverityScore} / 10</b>
            </div>
            <div style="font-size:9px; color:#94A3B8; margin-top:4px;">${'$'}{pt.cveIds ? pt.cveIds.slice(0, 3).join(', ') : ''}</div>
          `;
          tooltip.style.opacity = "1";
          const mouseX = event.layerX || (x(pt.label) + 20);
          tooltip.style.left = Math.min(mouseX + 10, width - 160) + "px";
          tooltip.style.top = "10px";
        })
        .on("mouseleave", function() {
          d3.select(this).attr("opacity", 1);
          tooltip.style.opacity = "0";
        });

      // Severity Curve (Recharts spline line)
      const lineGenerator = d3.line()
        .x(d => x(d.label) + x.bandwidth() / 2)
        .y(d => yRight(d.averageSeverityScore))
        .curve(d3.curveMonotoneX);

      svg.append("path")
        .datum(data)
        .attr("fill", "none")
        .attr("stroke", "#38BDF8")
        .attr("stroke-width", 2.5)
        .attr("d", lineGenerator);

      // Line Dots
      svg.selectAll(".severity-dot")
        .data(data)
        .join("circle")
        .attr("class", "severity-dot")
        .attr("cx", d => x(d.label) + x.bandwidth() / 2)
        .attr("cy", d => yRight(d.averageSeverityScore))
        .attr("r", 4)
        .attr("fill", "#0A0F1D")
        .attr("stroke", "#38BDF8")
        .attr("stroke-width", 2);
    }

    window.addEventListener("resize", renderChart);
    window.addEventListener("load", renderChart);
    renderChart();
  </script>
</body>
</html>
        """.trimIndent()
    }
}
