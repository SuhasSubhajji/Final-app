package com.example.ui.screens

import com.example.data.mitre.MitreMatrixRepository
import com.example.data.mitre.MitreTactic
import org.json.JSONArray
import org.json.JSONObject

object MitreFlowChartHtmlBuilder {

    private const val S = "$"

    fun buildDataJson(focusMode: String = "ALL"): String {
        val root = JSONObject()
        val title = when (focusMode) {
            "ATTACK" -> "MITRE ATT&CK Enterprise Matrix"
            "DEFEND" -> "MITRE D3FEND Countermeasures Graph"
            else -> "MITRE ATT&CK & D3FEND"
        }
        root.put("name", title)
        root.put("type", "root")
        root.put("focusMode", focusMode)

        val tacticsArray = JSONArray()

        MitreTactic.entries.forEach { tactic ->
            val tacticObj = JSONObject()
            tacticObj.put("id", tactic.tacticId)
            tacticObj.put("name", tactic.displayName)
            tacticObj.put("color", String.format("#%06X", (0xFFFFFF and tactic.color.hashCode())))
            tacticObj.put("description", tactic.description)
            tacticObj.put("type", "tactic")

            val ttps = MitreMatrixRepository.allTtps.filter { it.tactic == tactic }
            val ttpsArray = JSONArray()

            ttps.forEach { ttp ->
                val ttpObj = JSONObject()
                ttpObj.put("id", ttp.techniqueId)
                ttpObj.put("name", ttp.name)
                ttpObj.put("subTechnique", ttp.subTechnique ?: "")
                ttpObj.put("description", ttp.description)
                ttpObj.put("attackTitle", ttp.attackExampleTitle)
                ttpObj.put("attackCode", ttp.attackCodeCommand)
                ttpObj.put("d3fendId", ttp.d3fendId)
                ttpObj.put("d3fendName", ttp.d3fendName)
                ttpObj.put("d3fendDesc", ttp.d3fendDescription)
                ttpObj.put("d3fendRule", ttp.d3fendRuleOrFix)
                ttpObj.put("aptGroups", JSONArray(ttp.knownAptGroups))
                ttpObj.put("tags", JSONArray(ttp.tags))
                ttpObj.put("type", "technique")
                ttpObj.put("color", String.format("#%06X", (0xFFFFFF and tactic.color.hashCode())))
                ttpsArray.put(ttpObj)
            }

            tacticObj.put("techniques", ttpsArray)
            tacticsArray.put(tacticObj)
        }

        root.put("tactics", tacticsArray)
        return root.toString()
    }

    fun buildHtml(focusMode: String = "ALL"): String {
        val dataJson = buildDataJson(focusMode)
        val pageTitle = when (focusMode) {
            "ATTACK" -> "MITRE ATT&CK Enterprise Flow Chart"
            "DEFEND" -> "MITRE D3FEND Countermeasures Flow Chart"
            else -> "MITRE ATT&CK & D3FEND Interactive Flow Chart"
        }

        return """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes">
  <title>$pageTitle</title>
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
      overflow: hidden;
      width: 100vw;
      height: 100vh;
      display: flex;
      flex-direction: column;
    }
    /* Header Toolbar */
    .toolbar {
      background: #0F172A;
      border-bottom: 1px solid rgba(148, 163, 184, 0.2);
      padding: 8px 12px;
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      gap: 8px;
      z-index: 10;
    }
    .view-modes {
      display: flex;
      background: #1E293B;
      border-radius: 8px;
      padding: 2px;
      border: 1px solid #334155;
    }
    .btn-mode {
      background: transparent;
      color: #94A3B8;
      border: none;
      padding: 6px 10px;
      font-size: 11px;
      font-weight: 600;
      border-radius: 6px;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 4px;
    }
    .btn-mode.active {
      background: #06B6D4;
      color: #0A0F1D;
    }
    .controls {
      display: flex;
      gap: 6px;
      align-items: center;
    }
    .btn-ctrl {
      background: #1E293B;
      color: #38BDF8;
      border: 1px solid #334155;
      border-radius: 6px;
      padding: 5px 9px;
      font-size: 12px;
      font-weight: bold;
      cursor: pointer;
    }
    .btn-ctrl:active {
      background: #334155;
    }
    /* Search Bar */
    .search-row {
      background: #0B1120;
      padding: 6px 12px;
      display: flex;
      gap: 8px;
      border-bottom: 1px solid #1E293B;
      overflow-x: auto;
      white-space: nowrap;
    }
    .search-box {
      background: #1E293B;
      border: 1px solid #334155;
      color: #F8FAFC;
      border-radius: 6px;
      padding: 4px 8px;
      font-size: 11px;
      width: 140px;
      outline: none;
    }
    .tactic-pill {
      background: #1E293B;
      border: 1px solid #334155;
      color: #94A3B8;
      padding: 3px 8px;
      border-radius: 12px;
      font-size: 10px;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 4px;
      flex-shrink: 0;
    }
    .tactic-pill.active {
      border-color: #06B6D4;
      color: #06B6D4;
      background: rgba(6, 182, 212, 0.15);
      font-weight: bold;
    }
    .pill-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
    }
    /* SVG Canvas */
    #chart-container {
      flex: 1;
      width: 100%;
      height: 100%;
      position: relative;
      overflow: hidden;
      background: radial-gradient(circle at 50% 50%, #0F172A 0%, #0A0F1D 100%);
    }
    svg {
      width: 100%;
      height: 100%;
      cursor: grab;
    }
    svg:active {
      cursor: grabbing;
    }
    /* Node and Link styling */
    .link {
      fill: none;
      stroke: #334155;
      stroke-width: 1.5px;
      stroke-opacity: 0.7;
      transition: stroke 0.3s, stroke-width 0.3s;
    }
    .link.highlighted {
      stroke: #06B6D4;
      stroke-width: 2.5px;
      stroke-opacity: 1;
    }
    .link.defend-link {
      stroke: #10B981;
      stroke-dasharray: 4,4;
    }
    .node {
      cursor: pointer;
      transition: transform 0.2s;
    }
    .node rect {
      stroke-width: 1.5px;
      rx: 8;
      ry: 8;
      filter: drop-shadow(0 2px 4px rgba(0,0,0,0.4));
    }
    .node text {
      font-family: monospace, sans-serif;
      pointer-events: none;
    }
    .node.selected rect {
      stroke-width: 3px;
      stroke: #F43F5E !important;
      filter: drop-shadow(0 0 10px #F43F5E);
    }
    /* Hint Banner */
    .hint-banner {
      position: absolute;
      bottom: 8px;
      left: 12px;
      background: rgba(15, 23, 42, 0.85);
      border: 1px solid #334155;
      padding: 4px 8px;
      border-radius: 6px;
      font-size: 10px;
      color: #94A3B8;
      pointer-events: none;
    }
    /* Bottom Drill-Down Inspector */
    #inspector {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: #0F172A;
      border-top: 2px solid #06B6D4;
      padding: 14px;
      box-shadow: 0 -8px 24px rgba(0,0,0,0.6);
      transform: translateY(105%);
      transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
      z-index: 50;
      max-height: 70vh;
      overflow-y: auto;
    }
    #inspector.open {
      transform: translateY(0);
    }
    .insp-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 10px;
    }
    .insp-badge-row {
      display: flex;
      gap: 6px;
      align-items: center;
      margin-bottom: 4px;
    }
    .badge-tech {
      background: #EF4444;
      color: #FFF;
      font-weight: bold;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-family: monospace;
    }
    .badge-tactic {
      background: #1E293B;
      color: #38BDF8;
      border: 1px solid #38BDF8;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
    }
    .badge-d3fend {
      background: rgba(16, 185, 129, 0.2);
      color: #10B981;
      border: 1px solid #10B981;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: bold;
    }
    .insp-title {
      font-size: 15px;
      font-weight: bold;
      color: #F8FAFC;
    }
    .insp-close {
      background: #1E293B;
      color: #94A3B8;
      border: 1px solid #334155;
      border-radius: 6px;
      padding: 4px 10px;
      font-size: 12px;
      font-weight: bold;
      cursor: pointer;
    }
    .insp-desc {
      font-size: 12px;
      color: #CBD5E1;
      line-height: 1.4;
      margin-bottom: 12px;
    }
    .box-code {
      background: #020617;
      border: 1px solid #1E293B;
      border-radius: 8px;
      padding: 10px;
      margin-bottom: 10px;
      position: relative;
    }
    .box-title {
      font-size: 10px;
      font-weight: bold;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      margin-bottom: 6px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .box-title.attack { color: #F87171; }
    .box-title.defend { color: #34D399; }
    .code-content {
      font-family: monospace;
      font-size: 11px;
      color: #E2E8F0;
      white-space: pre-wrap;
      word-break: break-all;
      user-select: text;
    }
    .btn-copy {
      background: #1E293B;
      color: #06B6D4;
      border: 1px solid #06B6D4;
      border-radius: 4px;
      padding: 2px 6px;
      font-size: 9px;
      font-weight: bold;
      cursor: pointer;
    }
    .apt-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;
      margin-top: 6px;
    }
    .apt-tag {
      background: #1E293B;
      color: #E2E8F0;
      border: 1px solid #475569;
      font-size: 10px;
      padding: 2px 6px;
      border-radius: 4px;
    }
  </style>
</head>
<body>

  <!-- Top Toolbar -->
  <div class="toolbar">
    <div class="view-modes">
      <button class="btn-mode active" id="mode-tree" onclick="switchMode('tree')">
        <span>🌲</span> Hierarchy Tree
      </button>
      <button class="btn-mode" id="mode-killchain" onclick="switchMode('killchain')">
        <span>⚡</span> Kill Chain Pipeline
      </button>
      <button class="btn-mode" id="mode-force" onclick="switchMode('force')">
        <span>🕸️</span> Force Network
      </button>
    </div>

    <div class="controls">
      <button class="btn-ctrl" onclick="zoomIn()">+</button>
      <button class="btn-ctrl" onclick="zoomOut()">−</button>
      <button class="btn-ctrl" onclick="resetZoom()">⟲ Reset</button>
      <button class="btn-ctrl" onclick="toggleExpandAll()">📂 Toggle All</button>
    </div>
  </div>

  <!-- Search & Tactic Row -->
  <div class="search-row" id="tactic-filter-row">
    <input type="text" class="search-box" id="search-input" placeholder="Search T1190, D3FEND..." oninput="handleSearch(this.value)">
    <div class="tactic-pill active" onclick="filterByTactic(null)">All Tactics</div>
  </div>

  <!-- Main Canvas Container -->
  <div id="chart-container">
    <svg id="canvas"></svg>
    <div class="hint-banner">
      💡 Tip: Tap any node to drill down. Pinch to zoom, drag to pan.
    </div>
  </div>

  <!-- Bottom Drill-Down Inspector -->
  <div id="inspector">
    <div class="insp-header">
      <div>
        <div class="insp-badge-row">
          <span class="badge-tech" id="insp-tech-id">T1190</span>
          <span class="badge-tactic" id="insp-tactic">Initial Access</span>
          <span class="badge-d3fend" id="insp-d3fend-id">D3-ITF</span>
        </div>
        <div class="insp-title" id="insp-title">Exploit Public-Facing Application</div>
      </div>
      <button class="insp-close" onclick="closeInspector()">✕ Close</button>
    </div>

    <div class="insp-desc" id="insp-desc"></div>

    <div class="box-code">
      <div class="box-title attack">
        <span>⚔️ Offensive TTP Simulation Command</span>
        <button class="btn-copy" onclick="copySnippet('attack')">Copy Attack Command</button>
      </div>
      <div class="code-content" id="insp-attack-code"></div>
    </div>

    <div class="box-code">
      <div class="box-title defend">
        <span id="insp-d3fend-header">🛡️ D3FEND Countermeasure & Detection</span>
        <button class="btn-copy" onclick="copySnippet('defend')">Copy Detection Rule</button>
      </div>
      <div class="code-content" id="insp-defend-rule"></div>
    </div>

    <div style="font-size: 11px; color: #94A3B8; margin-top: 8px;">
      Known Threat Actors:
      <div class="apt-tags" id="insp-apt-tags"></div>
    </div>
  </div>

  <!-- Main JavaScript with D3.js -->
  <script>
    const RAW_DATA = $dataJson;
    let currentMode = 'tree';
    let selectedTacticId = null;
    let selectedNode = null;
    let currentSearch = '';
    let isAllExpanded = true;

    // Viewport dimensions
    let width = window.innerWidth;
    let height = window.innerHeight - 80;

    // Tactic filter pills
    const pillRow = document.getElementById("tactic-filter-row");
    RAW_DATA.tactics.forEach(function(t) {
      const pill = document.createElement("div");
      pill.className = "tactic-pill";
      pill.id = "pill-" + t.id;
      pill.innerHTML = '<span class="pill-dot" style="background:' + t.color + '"></span>' + t.name;
      pill.onclick = function() { filterByTactic(t.id); };
      pillRow.appendChild(pill);
    });

    // Check if D3 is available
    const hasD3 = (typeof d3 !== 'undefined');
    const svgEl = document.getElementById("canvas");
    let svgD3 = null;
    let gD3 = null;
    let zoomBehavior = null;

    if (hasD3) {
      svgD3 = d3.select("#canvas");
      gD3 = svgD3.append("g");
      zoomBehavior = d3.zoom()
        .scaleExtent([0.15, 3.5])
        .on("zoom", function(e) { gD3.attr("transform", e.transform); });
      svgD3.call(zoomBehavior);
    }

    // Build hierarchy
    function buildHierarchyData() {
      let tacticsList = RAW_DATA.tactics;
      if (selectedTacticId) {
        tacticsList = tacticsList.filter(function(t) { return t.id === selectedTacticId; });
      }

      return {
        name: "MITRE ATT&CK & D3FEND",
        id: "ROOT",
        type: "root",
        color: "#06B6D4",
        children: tacticsList.map(function(t) {
          let ttps = t.techniques;
          if (currentSearch) {
            const q = currentSearch.toLowerCase();
            ttps = ttps.filter(function(item) {
              return item.id.toLowerCase().indexOf(q) !== -1 ||
                item.name.toLowerCase().indexOf(q) !== -1 ||
                item.d3fendId.toLowerCase().indexOf(q) !== -1 ||
                item.d3fendName.toLowerCase().indexOf(q) !== -1 ||
                item.aptGroups.some(function(a) { return a.toLowerCase().indexOf(q) !== -1; });
            });
          }
          return {
            name: t.name,
            id: t.id,
            type: "tactic",
            color: t.color,
            description: t.description,
            children: ttps.map(function(p) {
              return Object.assign({}, p, {
                tacticName: t.name,
                tacticId: t.id,
                children: [
                  {
                    id: p.d3fendId,
                    name: p.d3fendName,
                    type: "d3fend",
                    color: "#10B981",
                    parentTechnique: p
                  }
                ]
              });
            })
          };
        })
      };
    }

    let rootNodeData = null;

    // Render Hierarchy Tree
    function renderTree() {
      if (!hasD3) {
        renderKillChainFallback();
        return;
      }
      gD3.selectAll("*").remove();

      const data = buildHierarchyData();
      rootNodeData = d3.hierarchy(data);
      rootNodeData.x0 = height / 2;
      rootNodeData.y0 = 40;

      if (!isAllExpanded && rootNodeData.children) {
        rootNodeData.children.forEach(function(c) {
          if (c.children) {
            c._children = c.children;
            c.children = null;
          }
        });
      }

      updateTree(rootNodeData);
      resetZoom(0.85);
    }

    function updateTree(source) {
      const treeLayout = d3.tree().nodeSize([48, 220]);
      treeLayout(rootNodeData);

      const nodes = rootNodeData.descendants();
      const links = rootNodeData.links();

      nodes.forEach(function(d) {
        d.y = d.depth * 230;
      });

      // Links
      const link = gD3.selectAll(".link")
        .data(links, function(d) { return d.target.data.id || d.target.id; });

      const linkEnter = link.enter().append("path")
        .attr("class", function(d) { return "link" + (d.target.data.type === "d3fend" ? " defend-link" : ""); })
        .attr("d", function(d) {
          const o = { x: source.x0 || 0, y: source.y0 || 0 };
          return diagonal(o, o);
        });

      link.merge(linkEnter).transition().duration(350)
        .attr("d", function(d) { return diagonal(d.source, d.target); });

      link.exit().transition().duration(350)
        .attr("d", function(d) {
          const o = { x: source.x || 0, y: source.y || 0 };
          return diagonal(o, o);
        })
        .remove();

      // Nodes
      const node = gD3.selectAll(".node")
        .data(nodes, function(d) { return d.data.id || d.id; });

      const nodeEnter = node.enter().append("g")
        .attr("class", function(d) { return "node " + d.data.type; })
        .attr("transform", function(d) { return "translate(" + (source.y0 || 0) + "," + (source.x0 || 0) + ")"; })
        .on("click", function(event, d) { handleNodeClick(event, d); });

      nodeEnter.append("rect")
        .attr("x", 0)
        .attr("y", -18)
        .attr("width", function(d) { return getNodeWidth(d.data); })
        .attr("height", 36)
        .attr("rx", 6)
        .attr("fill", function(d) { return getNodeBg(d.data); })
        .attr("stroke", function(d) { return d.data.color || "#38BDF8"; });

      nodeEnter.append("text")
        .attr("x", 10)
        .attr("y", -2)
        .attr("fill", function(d) { return d.data.type === 'd3fend' ? '#34D399' : (d.data.type === 'technique' ? '#F87171' : d.data.color); })
        .attr("font-size", "10px")
        .attr("font-weight", "bold")
        .text(function(d) { return getNodeBadge(d.data); });

      nodeEnter.append("text")
        .attr("x", 10)
        .attr("y", 12)
        .attr("fill", "#F8FAFC")
        .attr("font-size", "11px")
        .attr("font-weight", "500")
        .text(function(d) { return getNodeLabel(d.data); });

      // Expandable indicators
      nodeEnter.filter(function(d) { return (d.children || d._children) && d.data.type !== 'root'; })
        .append("circle")
        .attr("cx", function(d) { return getNodeWidth(d.data); })
        .attr("cy", 0)
        .attr("r", 7)
        .attr("fill", "#1E293B")
        .attr("stroke", function(d) { return d.data.color; });

      nodeEnter.filter(function(d) { return (d.children || d._children) && d.data.type !== 'root'; })
        .append("text")
        .attr("x", function(d) { return getNodeWidth(d.data); })
        .attr("y", 3)
        .attr("text-anchor", "middle")
        .attr("fill", "#38BDF8")
        .attr("font-size", "9px")
        .attr("font-weight", "bold")
        .text(function(d) { return d._children ? "+" : "−"; });

      const nodeUpdate = node.merge(nodeEnter).transition().duration(350)
        .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

      nodeUpdate.select("rect")
        .attr("stroke", function(d) { return (selectedNode && selectedNode.data.id === d.data.id) ? "#F43F5E" : (d.data.color || "#38BDF8"); });

      node.exit().transition().duration(350)
        .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
        .remove();

      nodes.forEach(function(d) {
        d.x0 = d.x;
        d.y0 = d.y;
      });
    }

    function diagonal(s, d) {
      const sy = s.y + (s.data ? getNodeWidth(s.data) : 0);
      return "M " + sy + " " + s.x +
             " C " + ((sy + d.y) / 2) + " " + s.x + "," +
             ((sy + d.y) / 2) + " " + d.x + "," +
             d.y + " " + d.x;
    }

    function getNodeWidth(data) {
      if (data.type === "root") return 190;
      if (data.type === "tactic") return 180;
      if (data.type === "technique") return 190;
      if (data.type === "d3fend") return 175;
      return 160;
    }

    function getNodeBg(data) {
      if (data.type === "root") return "#0F172A";
      if (data.type === "tactic") return "#131C31";
      if (data.type === "technique") return "#1A1A2E";
      if (data.type === "d3fend") return "#062822";
      return "#1E293B";
    }

    function getNodeBadge(data) {
      if (data.type === "root") return "FRAMEWORK";
      if (data.type === "tactic") return data.id + " • TACTIC";
      if (data.type === "technique") return data.id + " [ATT&CK]";
      if (data.type === "d3fend") return data.id + " [D3FEND]";
      return "";
    }

    function getNodeLabel(data) {
      const name = data.name || "";
      return name.length > 22 ? name.substring(0, 20) + "..." : name;
    }

    function handleNodeClick(event, d) {
      event.stopPropagation();
      selectedNode = d;

      if (d.data.type === "technique" || d.data.type === "d3fend") {
        const item = d.data.type === "technique" ? d.data : d.data.parentTechnique;
        openInspector(item);
      } else if (d.data.type === "tactic") {
        if (d.children) {
          d._children = d.children;
          d.children = null;
        } else {
          d.children = d._children;
          d._children = null;
        }
        updateTree(d);
      }
    }

    // Kill Chain Pipeline Layout
    function renderKillChain() {
      if (!hasD3) {
        renderKillChainFallback();
        return;
      }
      gD3.selectAll("*").remove();

      let tactics = RAW_DATA.tactics;
      if (selectedTacticId) {
        tactics = tactics.filter(function(t) { return t.id === selectedTacticId; });
      }

      const colWidth = 240;
      const startX = 60;
      const startY = 60;

      tactics.forEach(function(t, colIdx) {
        const colX = startX + colIdx * (colWidth + 24);

        const headerG = gD3.append("g")
          .attr("transform", "translate(" + colX + ", " + startY + ")")
          .style("cursor", "pointer")
          .on("click", function() { filterByTactic(t.id); });

        headerG.append("rect")
          .attr("width", colWidth)
          .attr("height", 46)
          .attr("rx", 8)
          .attr("fill", "#0F172A")
          .attr("stroke", t.color)
          .attr("stroke-width", 2);

        headerG.append("text")
          .attr("x", 12)
          .attr("y", 18)
          .attr("fill", t.color)
          .attr("font-size", "10px")
          .attr("font-weight", "bold")
          .text(t.id + " • STAGE " + (colIdx + 1));

        headerG.append("text")
          .attr("x", 12)
          .attr("y", 34)
          .attr("fill", "#F8FAFC")
          .attr("font-size", "13px")
          .attr("font-weight", "bold")
          .text(t.name);

        let ttps = t.techniques;
        if (currentSearch) {
          const q = currentSearch.toLowerCase();
          ttps = ttps.filter(function(item) {
            return item.id.toLowerCase().indexOf(q) !== -1 ||
              item.name.toLowerCase().indexOf(q) !== -1 ||
              item.d3fendId.toLowerCase().indexOf(q) !== -1;
          });
        }

        ttps.forEach(function(ttp, rowIdx) {
          const cardY = startY + 60 + rowIdx * 105;

          const cardG = gD3.append("g")
            .attr("transform", "translate(" + colX + ", " + cardY + ")")
            .style("cursor", "pointer")
            .on("click", function() { openInspector(Object.assign({}, ttp, { tacticName: t.name, tacticId: t.id })); });

          cardG.append("rect")
            .attr("width", colWidth)
            .attr("height", 92)
            .attr("rx", 8)
            .attr("fill", "#1E293B")
            .attr("stroke", "#334155")
            .attr("stroke-width", 1.5);

          cardG.append("rect")
            .attr("x", 10)
            .attr("y", 10)
            .attr("width", 60)
            .attr("height", 18)
            .attr("rx", 4)
            .attr("fill", "#EF4444");

          cardG.append("text")
            .attr("x", 15)
            .attr("y", 22)
            .attr("fill", "#FFF")
            .attr("font-size", "10px")
            .attr("font-weight", "bold")
            .text(ttp.id);

          cardG.append("text")
            .attr("x", 10)
            .attr("y", 44)
            .attr("fill", "#F8FAFC")
            .attr("font-size", "12px")
            .attr("font-weight", "600")
            .text(ttp.name.length > 25 ? ttp.name.substring(0, 23) + "..." : ttp.name);

          cardG.append("rect")
            .attr("x", 10)
            .attr("y", 56)
            .attr("width", colWidth - 20)
            .attr("height", 24)
            .attr("rx", 4)
            .attr("fill", "rgba(16, 185, 129, 0.15)")
            .attr("stroke", "#10B981");

          cardG.append("text")
            .attr("x", 16)
            .attr("y", 72)
            .attr("fill", "#34D399")
            .attr("font-size", "10px")
            .attr("font-weight", "500")
            .text("Defend: " + ttp.d3fendId + " " + (ttp.d3fendName.length > 18 ? ttp.d3fendName.substring(0, 16) + ".." : ttp.d3fendName));
        });

        if (colIdx < tactics.length - 1) {
          const arrowX = colX + colWidth + 4;
          const arrowY = startY + 23;
          gD3.append("line")
            .attr("x1", arrowX)
            .attr("y1", arrowY)
            .attr("x2", arrowX + 16)
            .attr("y2", arrowY)
            .attr("stroke", "#38BDF8")
            .attr("stroke-width", 2);
        }
      });

      resetZoom(0.65);
    }

    // Force-Directed Graph Layout
    let forceSim = null;
    function renderForce() {
      if (!hasD3) {
        renderKillChainFallback();
        return;
      }
      gD3.selectAll("*").remove();

      const nodes = [];
      const links = [];

      nodes.push({ id: "ROOT", name: "MITRE ATT&CK", type: "root", color: "#06B6D4", r: 24 });

      let tactics = RAW_DATA.tactics;
      if (selectedTacticId) {
        tactics = tactics.filter(function(t) { return t.id === selectedTacticId; });
      }

      tactics.forEach(function(t) {
        nodes.push({ id: t.id, name: t.name, type: "tactic", color: t.color, r: 18 });
        links.push({ source: "ROOT", target: t.id, stroke: t.color });

        t.techniques.forEach(function(p) {
          nodes.push({
            id: p.id,
            name: p.name,
            type: "technique",
            color: "#EF4444",
            r: 12,
            data: Object.assign({}, p, { tacticName: t.name, tacticId: t.id })
          });
          links.push({ source: t.id, target: p.id, stroke: "#64748B" });

          nodes.push({
            id: p.id + "-d3fend",
            name: p.d3fendId,
            type: "d3fend",
            color: "#10B981",
            r: 9,
            data: Object.assign({}, p, { tacticName: t.name, tacticId: t.id })
          });
          links.push({ source: p.id, target: p.id + "-d3fend", stroke: "#10B981", isDefend: true });
        });
      });

      forceSim = d3.forceSimulation(nodes)
        .force("link", d3.forceLink(links).id(function(d) { return d.id; }).distance(65))
        .force("charge", d3.forceManyBody().strength(-120))
        .force("center", d3.forceCenter(width / 2, height / 2))
        .force("collision", d3.forceCollide().radius(function(d) { return d.r + 10; }));

      const link = gD3.selectAll(".force-link")
        .data(links)
        .enter().append("line")
        .attr("class", function(d) { return "link" + (d.isDefend ? " defend-link" : ""); })
        .attr("stroke", function(d) { return d.stroke || "#334155"; })
        .attr("stroke-width", function(d) { return d.isDefend ? 1.5 : 1.2; });

      const node = gD3.selectAll(".force-node")
        .data(nodes)
        .enter().append("g")
        .attr("class", "node")
        .call(d3.drag()
          .on("start", function(event, d) {
            if (!event.active) forceSim.alphaTarget(0.3).restart();
            d.fx = d.x; d.fy = d.y;
          })
          .on("drag", function(event, d) {
            d.fx = event.x; d.fy = event.y;
          })
          .on("end", function(event, d) {
            if (!event.active) forceSim.alphaTarget(0);
            d.fx = null; d.fy = null;
          }))
        .on("click", function(event, d) {
          if (d.data) {
            openInspector(d.data);
          } else if (d.type === 'tactic') {
            filterByTactic(d.id);
          }
        });

      node.append("circle")
        .attr("r", function(d) { return d.r; })
        .attr("fill", function(d) { return d.color; })
        .attr("stroke", "#0F172A")
        .attr("stroke-width", 2);

      node.append("text")
        .attr("dx", function(d) { return d.r + 4; })
        .attr("dy", 4)
        .attr("fill", "#F8FAFC")
        .attr("font-size", function(d) { return d.type === 'technique' ? "10px" : "11px"; })
        .attr("font-weight", function(d) { return d.type === 'tactic' ? "bold" : "normal"; })
        .text(function(d) { return d.id; });

      forceSim.on("tick", function() {
        link
          .attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });

        node
          .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
      });

      resetZoom(0.8);
    }

    // Built-in offline fallback renderer
    function renderKillChainFallback() {
      svgEl.innerHTML = "";
      const containerG = document.createElementNS("http://www.w3.org/2000/svg", "g");
      containerG.setAttribute("transform", "translate(40, 40) scale(0.75)");
      svgEl.appendChild(containerG);

      let tactics = RAW_DATA.tactics;
      if (selectedTacticId) {
        tactics = tactics.filter(function(t) { return t.id === selectedTacticId; });
      }

      const colWidth = 220;
      tactics.forEach(function(t, colIdx) {
        const colX = colIdx * (colWidth + 20);
        const colG = document.createElementNS("http://www.w3.org/2000/svg", "g");
        colG.setAttribute("transform", "translate(" + colX + ", 0)");

        const headerRect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
        headerRect.setAttribute("width", colWidth);
        headerRect.setAttribute("height", "40");
        headerRect.setAttribute("rx", "6");
        headerRect.setAttribute("fill", "#0F172A");
        headerRect.setAttribute("stroke", t.color);
        headerRect.setAttribute("stroke-width", "2");
        colG.appendChild(headerRect);

        const headerTxt = document.createElementNS("http://www.w3.org/2000/svg", "text");
        headerTxt.setAttribute("x", "10");
        headerTxt.setAttribute("y", "24");
        headerTxt.setAttribute("fill", "#F8FAFC");
        headerTxt.setAttribute("font-size", "12");
        headerTxt.setAttribute("font-weight", "bold");
        headerTxt.textContent = t.name;
        colG.appendChild(headerTxt);

        t.techniques.forEach(function(ttp, rIdx) {
          const cardG = document.createElementNS("http://www.w3.org/2000/svg", "g");
          cardG.setAttribute("transform", "translate(0, " + (50 + rIdx * 80) + ")");
          cardG.style.cursor = "pointer";
          cardG.onclick = function() { openInspector(Object.assign({}, ttp, { tacticName: t.name, tacticId: t.id })); };

          const cardRect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
          cardRect.setAttribute("width", colWidth);
          cardRect.setAttribute("height", "70");
          cardRect.setAttribute("rx", "6");
          cardRect.setAttribute("fill", "#1E293B");
          cardRect.setAttribute("stroke", "#334155");
          cardG.appendChild(cardRect);

          const ttpIdTxt = document.createElementNS("http://www.w3.org/2000/svg", "text");
          ttpIdTxt.setAttribute("x", "10");
          ttpIdTxt.setAttribute("y", "22");
          ttpIdTxt.setAttribute("fill", "#F87171");
          ttpIdTxt.setAttribute("font-size", "10");
          ttpIdTxt.setAttribute("font-weight", "bold");
          ttpIdTxt.textContent = ttp.id;
          cardG.appendChild(ttpIdTxt);

          const ttpNameTxt = document.createElementNS("http://www.w3.org/2000/svg", "text");
          ttpNameTxt.setAttribute("x", "10");
          ttpNameTxt.setAttribute("y", "40");
          ttpNameTxt.setAttribute("fill", "#F8FAFC");
          ttpNameTxt.setAttribute("font-size", "11");
          ttpNameTxt.textContent = ttp.name.length > 20 ? ttp.name.substring(0, 18) + ".." : ttp.name;
          cardG.appendChild(ttpNameTxt);

          const d3fendTxt = document.createElementNS("http://www.w3.org/2000/svg", "text");
          d3fendTxt.setAttribute("x", "10");
          d3fendTxt.setAttribute("y", "58");
          d3fendTxt.setAttribute("fill", "#34D399");
          d3fendTxt.setAttribute("font-size", "10");
          d3fendTxt.textContent = "Def: " + ttp.d3fendId;
          cardG.appendChild(d3fendTxt);

          colG.appendChild(cardG);
        });

        containerG.appendChild(colG);
      });
    }

    // Switch View Mode
    function switchMode(mode) {
      currentMode = mode;
      document.querySelectorAll(".btn-mode").forEach(function(b) { b.classList.remove("active"); });
      document.getElementById("mode-" + mode).classList.add("active");

      if (forceSim) {
        forceSim.stop();
      }

      if (mode === 'tree') renderTree();
      else if (mode === 'killchain') renderKillChain();
      else if (mode === 'force') renderForce();
    }

    // Filter by tactic
    function filterByTactic(tacticId) {
      selectedTacticId = tacticId;
      document.querySelectorAll(".tactic-pill").forEach(function(p) { p.classList.remove("active"); });
      if (tacticId) {
        const p = document.getElementById("pill-" + tacticId);
        if (p) p.classList.add("active");
      } else {
        document.querySelector(".tactic-pill").classList.add("active");
      }

      if (currentMode === 'tree') renderTree();
      else if (currentMode === 'killchain') renderKillChain();
      else if (currentMode === 'force') renderForce();
    }

    // Search query
    function handleSearch(val) {
      currentSearch = val;
      if (currentMode === 'tree') renderTree();
      else if (currentMode === 'killchain') renderKillChain();
      else if (currentMode === 'force') renderForce();
    }

    function zoomIn() {
      if (hasD3) svgD3.transition().duration(250).call(zoomBehavior.scaleBy, 1.3);
    }
    function zoomOut() {
      if (hasD3) svgD3.transition().duration(250).call(zoomBehavior.scaleBy, 0.7);
    }
    function resetZoom(scale) {
      const s = scale || 1.0;
      if (hasD3) {
        svgD3.transition().duration(400).call(
          zoomBehavior.transform,
          d3.zoomIdentity.translate(width * 0.1, height * 0.1).scale(s)
        );
      }
    }

    function toggleExpandAll() {
      isAllExpanded = !isAllExpanded;
      if (currentMode === 'tree') renderTree();
    }

    // Inspector Drawer
    let currentInspectedItem = null;

    function openInspector(item) {
      currentInspectedItem = item;
      document.getElementById("insp-tech-id").innerText = item.id || "";
      document.getElementById("insp-tactic").innerText = item.tacticName || "Tactic";
      document.getElementById("insp-d3fend-id").innerText = item.d3fendId || "";
      document.getElementById("insp-title").innerText = item.name || "";
      document.getElementById("insp-desc").innerText = item.description || "";
      document.getElementById("insp-attack-code").innerText = item.attackCode || "";
      document.getElementById("insp-d3fend-header").innerText = "🛡️ D3FEND: " + (item.d3fendName || "");
      document.getElementById("insp-defend-rule").innerText = item.d3fendRule || item.d3fendDesc || "";

      const tagsContainer = document.getElementById("insp-apt-tags");
      tagsContainer.innerHTML = "";
      (item.aptGroups || []).forEach(function(a) {
        const span = document.createElement("span");
        span.className = "apt-tag";
        span.innerText = a;
        tagsContainer.appendChild(span);
      });

      document.getElementById("inspector").classList.add("open");

      if (window.AndroidBridge && window.AndroidBridge.onTechniqueSelected) {
        window.AndroidBridge.onTechniqueSelected(item.id);
      }
    }

    function closeInspector() {
      document.getElementById("inspector").classList.remove("open");
    }

    function copySnippet(type) {
      if (!currentInspectedItem) return;
      const text = type === 'attack' ? currentInspectedItem.attackCode : currentInspectedItem.d3fendRule;
      const label = type === 'attack' ? 'Attack Command' : 'D3FEND Rule';

      if (window.AndroidBridge && window.AndroidBridge.copyText) {
        window.AndroidBridge.copyText(text, label);
      } else if (navigator.clipboard) {
        navigator.clipboard.writeText(text);
        alert("Copied " + label + " to clipboard!");
      }
    }

    window.addEventListener("resize", function() {
      width = window.innerWidth;
      height = window.innerHeight - 80;
    });

    // Run initial view
    setTimeout(function() {
      renderTree();
    }, 120);
  </script>
</body>
</html>
        """.trimIndent()
    }
}
