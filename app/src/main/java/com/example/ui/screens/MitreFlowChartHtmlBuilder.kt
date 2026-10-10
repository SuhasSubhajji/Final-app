package com.example.ui.screens

import androidx.compose.ui.graphics.toArgb
import com.example.data.mitre.MitreDefendTactic
import com.example.data.mitre.MitreMatrixRepository
import com.example.data.mitre.MitreTactic
import org.json.JSONArray
import org.json.JSONObject

object MitreFlowChartHtmlBuilder {

    private const val S = "$"

    fun buildDataJson(focusMode: String = "ALL"): String {
        val root = JSONObject()
        val title = when (focusMode) {
            "ATTACK" -> "MITRE ATT&CK Enterprise Matrix (14 Tactics)"
            "DEFEND" -> "MITRE D3FEND Countermeasures Matrix (6 Tactics)"
            else -> "MITRE ATT&CK & D3FEND Unified Cross-Link Flow"
        }
        root.put("name", title)
        root.put("type", "root")
        root.put("focusMode", focusMode)

        // 1. Build ATT&CK Tactics & Techniques Array
        val attackTacticsArray = JSONArray()
        MitreTactic.entries.forEach { tactic ->
            val tacticObj = JSONObject()
            tacticObj.put("id", tactic.tacticId)
            tacticObj.put("name", tactic.displayName)
            tacticObj.put("color", String.format("#%06X", (0xFFFFFF and tactic.color.toArgb())))
            tacticObj.put("description", tactic.description)
            tacticObj.put("type", "attack_tactic")

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
                ttpObj.put("type", "attack_technique")
                ttpObj.put("color", String.format("#%06X", (0xFFFFFF and tactic.color.toArgb())))
                ttpsArray.put(ttpObj)
            }

            tacticObj.put("techniques", ttpsArray)
            attackTacticsArray.put(tacticObj)
        }
        root.put("attackTactics", attackTacticsArray)

        // 2. Build D3FEND Tactics & Techniques Array
        val defendTacticsArray = JSONArray()
        MitreDefendTactic.entries.forEach { d3Tactic ->
            val tacticObj = JSONObject()
            tacticObj.put("id", d3Tactic.tacticId)
            tacticObj.put("name", d3Tactic.displayName)
            tacticObj.put("color", String.format("#%06X", (0xFFFFFF and d3Tactic.color.toArgb())))
            tacticObj.put("description", d3Tactic.description)
            tacticObj.put("type", "defend_tactic")

            val d3Items = MitreMatrixRepository.allD3fendItems.filter { it.tactic == d3Tactic }
            val d3Array = JSONArray()

            d3Items.forEach { d3 ->
                val d3Obj = JSONObject()
                d3Obj.put("id", d3.d3fendId)
                d3Obj.put("name", d3.name)
                d3Obj.put("description", d3.description)
                d3Obj.put("ruleOrFix", d3.ruleOrFix)
                d3Obj.put("counteredAttackIds", JSONArray(d3.counteredAttackIds))
                d3Obj.put("tags", JSONArray(d3.tags))
                d3Obj.put("type", "defend_technique")
                d3Obj.put("color", String.format("#%06X", (0xFFFFFF and d3Tactic.color.toArgb())))
                d3Array.put(d3Obj)
            }

            tacticObj.put("techniques", d3Array)
            defendTacticsArray.put(tacticObj)
        }
        root.put("defendTactics", defendTacticsArray)

        return root.toString()
    }

    fun buildHtml(focusMode: String = "ALL"): String {
        val dataJson = buildDataJson(focusMode)
        val pageTitle = when (focusMode) {
            "ATTACK" -> "MITRE ATT&CK Flow Chart"
            "DEFEND" -> "MITRE D3FEND Flow Chart"
            else -> "MITRE ATT&CK & D3FEND Cross-Link Flow"
        }

        val btnAttackClass = if (focusMode == "ATTACK") "active" else ""
        val btnDefendClass = if (focusMode == "DEFEND") "active" else ""
        val btnAllClass = if (focusMode == "ALL") "active" else ""

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
      padding: 6px 12px;
      font-size: 11px;
      font-weight: 600;
      border-radius: 6px;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 4px;
      transition: all 0.2s ease;
    }
    .btn-mode.active {
      background: #06B6D4;
      color: #0A0F1D;
      font-weight: 700;
      box-shadow: 0 0 8px rgba(6, 182, 212, 0.4);
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
      align-items: center;
      border-bottom: 1px solid #1E293B;
    }
    .search-input {
      background: #1E293B;
      border: 1px solid #334155;
      color: #F8FAFC;
      border-radius: 6px;
      padding: 6px 10px;
      font-size: 12px;
      width: 100%;
      outline: none;
    }
    .search-input:focus {
      border-color: #06B6D4;
      box-shadow: 0 0 6px rgba(6, 182, 212, 0.3);
    }
    /* Canvas */
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
    /* Node Styles */
    .node-group {
      cursor: pointer;
      transition: transform 0.2s ease;
    }
    .node-bg {
      rx: 8px;
      ry: 8px;
      stroke-width: 1.5px;
      transition: all 0.2s ease;
    }
    .node-bg.attack {
      fill: #1E293B;
      stroke: #334155;
    }
    .node-bg.defend {
      fill: #0F2942;
      stroke: #1E40AF;
    }
    .node-group:hover .node-bg {
      stroke: #06B6D4;
      filter: drop-shadow(0px 0px 8px rgba(6, 182, 212, 0.5));
    }
    .node-title {
      font-size: 11px;
      font-weight: 700;
      fill: #F8FAFC;
    }
    .node-sub {
      font-size: 9px;
      fill: #94A3B8;
    }
    .node-badge {
      font-size: 9px;
      font-weight: 800;
      fill: #06B6D4;
    }
    /* Flow Links */
    .link-path {
      fill: none;
      stroke: #334155;
      stroke-width: 1.5px;
      stroke-opacity: 0.5;
      transition: all 0.3s ease;
    }
    .link-path.highlight {
      stroke: #06B6D4;
      stroke-width: 3px;
      stroke-opacity: 0.9;
      filter: drop-shadow(0px 0px 6px #06B6D4);
    }
    .link-path.defend-highlight {
      stroke: #10B981;
      stroke-width: 3px;
      stroke-opacity: 0.9;
      filter: drop-shadow(0px 0px 6px #10B981);
    }
    /* Detail Modal Overlay */
    .modal-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: #0F172A;
      border-top: 2px solid #06B6D4;
      border-top-left-radius: 16px;
      border-top-right-radius: 16px;
      padding: 16px;
      max-height: 75vh;
      overflow-y: auto;
      box-shadow: 0 -8px 24px rgba(0, 0, 0, 0.8);
      transform: translateY(100%);
      transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
      z-index: 100;
    }
    .modal-overlay.open {
      transform: translateY(0);
    }
    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
    }
    .modal-title {
      font-size: 15px;
      font-weight: 800;
      color: #F8FAFC;
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .modal-badge {
      background: rgba(6, 182, 212, 0.2);
      color: #06B6D4;
      border: 1px solid #06B6D4;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: bold;
    }
    .modal-close {
      background: #1E293B;
      color: #94A3B8;
      border: none;
      border-radius: 50%;
      width: 28px;
      height: 28px;
      font-size: 16px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .modal-desc {
      font-size: 12px;
      color: #CBD5E1;
      line-height: 1.5;
      margin-bottom: 12px;
    }
    .code-box {
      background: #060913;
      border: 1px solid #1E293B;
      border-radius: 8px;
      padding: 10px;
      font-family: monospace;
      font-size: 11px;
      color: #38BDF8;
      overflow-x: auto;
      white-space: pre-wrap;
      word-break: break-all;
      position: relative;
      margin-bottom: 12px;
    }
    .btn-copy {
      position: absolute;
      top: 6px;
      right: 6px;
      background: #1E293B;
      color: #06B6D4;
      border: 1px solid #334155;
      border-radius: 4px;
      padding: 3px 8px;
      font-size: 10px;
      font-weight: bold;
      cursor: pointer;
    }
    .countermeasure-card {
      background: rgba(16, 185, 129, 0.1);
      border: 1px solid rgba(16, 185, 129, 0.4);
      border-radius: 8px;
      padding: 10px;
      margin-top: 8px;
    }
    .countermeasure-title {
      font-size: 11px;
      font-weight: bold;
      color: #10B981;
      margin-bottom: 4px;
      display: flex;
      align-items: center;
      gap: 6px;
    }
  </style>
</head>
<body>

  <!-- Top Toolbar -->
  <div class="toolbar">
    <div class="view-modes">
      <button class="btn-mode $btnAttackClass" onclick="switchMode('ATTACK')">⚔️ ATT&CK</button>
      <button class="btn-mode $btnDefendClass" onclick="switchMode('DEFEND')">🛡️ D3FEND</button>
      <button class="btn-mode $btnAllClass" onclick="switchMode('ALL')">🔗 Cross-Link</button>
    </div>
    <div class="controls">
      <button class="btn-ctrl" onclick="zoomIn()">+</button>
      <button class="btn-ctrl" onclick="zoomOut()">-</button>
      <button class="btn-ctrl" onclick="resetZoom()">🎯 Fit</button>
    </div>
  </div>

  <!-- Search Row -->
  <div class="search-row">
    <input type="text" id="searchInput" class="search-input" placeholder="Search ATT&CK & D3FEND techniques, rules, tags..." oninput="handleSearch()">
  </div>

  <!-- Chart Container -->
  <div id="chart-container">
    <svg id="svgCanvas"></svg>
  </div>

  <!-- Modal Detail Sheet -->
  <div id="detailModal" class="modal-overlay">
    <div class="modal-header">
      <div class="modal-title">
        <span id="modalIdBadge" class="modal-badge">TA0001</span>
        <span id="modalTitle">Technique Name</span>
      </div>
      <button class="modal-close" onclick="closeModal()">✕</button>
    </div>
    <div id="modalDescription" class="modal-desc">Description content...</div>
    
    <div id="modalCodeSection">
      <div style="font-size:11px; font-weight:bold; color:#06B6D4; margin-bottom:4px;" id="modalCodeLabel">ATTACK / REMEDIATION CODE</div>
      <div class="code-box">
        <button class="btn-copy" onclick="copyModalCode()">Copy Code</button>
        <code id="modalCodeText">command string</code>
      </div>
    </div>

    <div id="modalCountermeasure" class="countermeasure-card">
      <div class="countermeasure-title">🛡️ D3FEND Countermeasure Neutralizer</div>
      <div style="font-size:11px; color:#E2E8F0;" id="modalCounterText">Countermeasure description</div>
    </div>
  </div>

  <script>
    const fullData = $dataJson;
    let currentMode = "$focusMode";
    let svg, g, zoomBehavior;
    let currentModalCode = "";

    function initChart() {
      const container = document.getElementById("chart-container");
      const width = container.clientWidth;
      const height = container.clientHeight;

      d3.select("#svgCanvas").selectAll("*").remove();

      svg = d3.select("#svgCanvas")
        .attr("width", width)
        .attr("height", height);

      g = svg.append("g").attr("id", "mainGroup");

      zoomBehavior = d3.zoom()
        .scaleExtent([0.2, 3])
        .on("zoom", (event) => g.attr("transform", event.transform));

      svg.call(zoomBehavior);

      renderFlowGraph(currentMode);
    }

    function renderFlowGraph(mode) {
      g.selectAll("*").remove();

      const container = document.getElementById("chart-container");
      const width = Math.max(container.clientWidth, 1200);
      const nodeWidth = 190;
      const nodeHeight = 54;

      if (mode === "ATTACK") {
        renderAttackMatrix(width, nodeWidth, nodeHeight);
      } else if (mode === "DEFEND") {
        renderDefendMatrix(width, nodeWidth, nodeHeight);
      } else {
        renderCrossLinkFlow(width, nodeWidth, nodeHeight);
      }
    }

    function renderAttackMatrix(width, nw, nh) {
      const tactics = fullData.attackTactics || [];
      const colSpacing = 210;
      const startX = 20;

      tactics.forEach((tac, tacIdx) => {
        const x = startX + (tacIdx * colSpacing);
        let y = 30;

        // Tactic Header
        const tacG = g.append("g").attr("transform", `translate(${'$'}{x}, ${'$'}{y})`);
        tacG.append("rect")
          .attr("width", nw)
          .attr("height", 36)
          .attr("rx", 6)
          .attr("fill", tac.color)
          .attr("opacity", 0.25)
          .attr("stroke", tac.color)
          .attr("stroke-width", 1.5);

        tacG.append("text")
          .attr("x", 10)
          .attr("y", 18)
          .attr("fill", "#F8FAFC")
          .attr("font-size", "11px")
          .attr("font-weight", "800")
          .text(tac.name);

        tacG.append("text")
          .attr("x", 10)
          .attr("y", 29)
          .attr("fill", tac.color)
          .attr("font-size", "9px")
          .attr("font-weight", "bold")
          .text(tac.id);

        y += 48;

        // Techniques
        (tac.techniques || []).forEach(tech => {
          const techG = g.append("g")
            .attr("class", "node-group")
            .attr("data-id", tech.id)
            .attr("data-search", `${'$'}{tech.id} ${'$'}{tech.name} ${'$'}{tech.tags.join(" ")}`.toLowerCase())
            .attr("transform", `translate(${'$'}{x}, ${'$'}{y})`)
            .on("click", () => openModalForAttack(tech, tac));

          techG.append("rect")
            .attr("class", "node-bg attack")
            .attr("width", nw)
            .attr("height", nh);

          techG.append("rect")
            .attr("width", 4)
            .attr("height", nh)
            .attr("rx", 2)
            .attr("fill", tac.color);

          techG.append("text")
            .attr("class", "node-badge")
            .attr("x", 12)
            .attr("y", 16)
            .text(tech.id);

          const truncatedName = tech.name.length > 24 ? tech.name.substring(0, 22) + "…" : tech.name;
          techG.append("text")
            .attr("class", "node-title")
            .attr("x", 12)
            .attr("y", 31)
            .text(truncatedName);

          techG.append("text")
            .attr("class", "node-sub")
            .attr("x", 12)
            .attr("y", 44)
            .text(`🛡️ ${'$'}{tech.d3fendId}`);

          y += nh + 10;
        });
      });
    }

    function renderDefendMatrix(width, nw, nh) {
      const tactics = fullData.defendTactics || [];
      const colSpacing = 210;
      const startX = 20;

      tactics.forEach((tac, tacIdx) => {
        const x = startX + (tacIdx * colSpacing);
        let y = 30;

        // Defend Tactic Header
        const tacG = g.append("g").attr("transform", `translate(${'$'}{x}, ${'$'}{y})`);
        tacG.append("rect")
          .attr("width", nw)
          .attr("height", 36)
          .attr("rx", 6)
          .attr("fill", tac.color)
          .attr("opacity", 0.25)
          .attr("stroke", tac.color)
          .attr("stroke-width", 1.5);

        tacG.append("text")
          .attr("x", 10)
          .attr("y", 18)
          .attr("fill", "#F8FAFC")
          .attr("font-size", "12px")
          .attr("font-weight", "800")
          .text("🛡️ " + tac.name);

        tacG.append("text")
          .attr("x", 10)
          .attr("y", 29)
          .attr("fill", tac.color)
          .attr("font-size", "9px")
          .attr("font-weight", "bold")
          .text(tac.id);

        y += 48;

        // Defend Techniques
        (tac.techniques || []).forEach(tech => {
          const techG = g.append("g")
            .attr("class", "node-group")
            .attr("data-id", tech.id)
            .attr("data-search", `${'$'}{tech.id} ${'$'}{tech.name} ${'$'}{tech.tags.join(" ")}`.toLowerCase())
            .attr("transform", `translate(${'$'}{x}, ${'$'}{y})`)
            .on("click", () => openModalForDefend(tech, tac));

          techG.append("rect")
            .attr("class", "node-bg defend")
            .attr("width", nw)
            .attr("height", nh);

          techG.append("rect")
            .attr("width", 4)
            .attr("height", nh)
            .attr("rx", 2)
            .attr("fill", tac.color);

          techG.append("text")
            .attr("class", "node-badge")
            .attr("x", 12)
            .attr("y", 16)
            .style("fill", "#10B981")
            .text(tech.id);

          const truncatedName = tech.name.length > 24 ? tech.name.substring(0, 22) + "…" : tech.name;
          techG.append("text")
            .attr("class", "node-title")
            .attr("x", 12)
            .attr("y", 31)
            .text(truncatedName);

          const counteredStr = tech.counteredAttackIds.join(", ");
          techG.append("text")
            .attr("class", "node-sub")
            .attr("x", 12)
            .attr("y", 44)
            .text(`⚔️ Neutralizes: ${'$'}{counteredStr}`);

          y += nh + 10;
        });
      });
    }

    function renderCrossLinkFlow(width, nw, nh) {
      const attackTactics = fullData.attackTactics || [];
      const defendTactics = fullData.defendTactics || [];

      // Left Column: ATT&CK Techniques
      // Right Column: D3FEND Countermeasures
      const xLeft = 40;
      const xRight = 500;
      let yLeft = 40;
      let yRight = 40;

      const attackPosMap = {};
      const defendPosMap = {};

      // Draw ATT&CK Nodes
      attackTactics.forEach(tac => {
        (tac.techniques || []).forEach(tech => {
          const techG = g.append("g")
            .attr("class", "node-group")
            .attr("data-id", tech.id)
            .attr("data-search", `${'$'}{tech.id} ${'$'}{tech.name}`.toLowerCase())
            .attr("transform", `translate(${'$'}{xLeft}, ${'$'}{yLeft})`)
            .on("click", () => openModalForAttack(tech, tac));

          techG.append("rect")
            .attr("class", "node-bg attack")
            .attr("width", nw)
            .attr("height", nh);

          techG.append("rect")
            .attr("width", 4)
            .attr("height", nh)
            .attr("rx", 2)
            .attr("fill", tac.color);

          techG.append("text").attr("class", "node-badge").attr("x", 12).attr("y", 16).text(tech.id);
          techG.append("text").attr("class", "node-title").attr("x", 12).attr("y", 31)
            .text(tech.name.length > 22 ? tech.name.substring(0, 20) + "…" : tech.name);

          attackPosMap[tech.id] = { x: xLeft + nw, y: yLeft + (nh / 2), tech: tech, tac: tac };
          yLeft += nh + 12;
        });
      });

      // Draw D3FEND Nodes
      defendTactics.forEach(tac => {
        (tac.techniques || []).forEach(d3Node => {
          const d3G = g.append("g")
            .attr("class", "node-group")
            .attr("data-id", d3Node.id)
            .attr("data-search", `${'$'}{d3Node.id} ${'$'}{d3Node.name}`.toLowerCase())
            .attr("transform", `translate(${'$'}{xRight}, ${'$'}{yRight})`)
            .on("click", () => openModalForDefend(d3Node, tac));

          d3G.append("rect")
            .attr("class", "node-bg defend")
            .attr("width", nw)
            .attr("height", nh);

          d3G.append("rect")
            .attr("width", 4)
            .attr("height", nh)
            .attr("rx", 2)
            .attr("fill", "#10B981");

          d3G.append("text").attr("class", "node-badge").attr("x", 12).attr("y", 16).style("fill", "#10B981").text(d3Node.id);
          d3G.append("text").attr("class", "node-title").attr("x", 12).attr("y", 31)
            .text(d3Node.name.length > 22 ? d3Node.name.substring(0, 20) + "…" : d3Node.name);

          defendPosMap[d3Node.id] = { x: xRight, y: yRight + (nh / 2), d3: d3Node, tac: tac };
          yRight += nh + 12;
        });
      });

      // Draw Connecting Flow Lines between ATT&CK and D3FEND
      Object.keys(attackPosMap).forEach(attId => {
        const att = attackPosMap[attId];
        const targetD3Id = att.tech.d3fendId;
        const d3Node = defendPosMap[targetD3Id];

        if (d3Node) {
          const pathStr = d3.linkHorizontal()({
            source: [att.x, att.y],
            target: [d3Node.x, d3Node.y]
          });

          g.append("path")
            .attr("class", "link-path")
            .attr("data-att", attId)
            .attr("data-defend", targetD3Id)
            .attr("d", pathStr);
        }
      });
    }

    function switchMode(newMode) {
      currentMode = newMode;
      document.querySelectorAll('.btn-mode').forEach(b => b.classList.remove('active'));
      event.target.classList.add('active');
      renderFlowGraph(newMode);
    }

    function zoomIn() { svg.transition().duration(300).call(zoomBehavior.scaleBy, 1.3); }
    function zoomOut() { svg.transition().duration(300).call(zoomBehavior.scaleBy, 0.7); }
    function resetZoom() { svg.transition().duration(400).call(zoomBehavior.transform, d3.zoomIdentity); }

    function handleSearch() {
      const q = document.getElementById("searchInput").value.trim().toLowerCase();
      d3.selectAll(".node-group").each(function() {
        const searchStr = this.getAttribute("data-search") || "";
        if (!q || searchStr.includes(q)) {
          d3.select(this).style("opacity", 1);
        } else {
          d3.select(this).style("opacity", 0.15);
        }
      });
    }

    function openModalForAttack(tech, tac) {
      document.getElementById("modalIdBadge").innerText = `${'$'}{tac.displayName} • ${'$'}{tech.id}`;
      document.getElementById("modalIdBadge").style.borderColor = tac.color;
      document.getElementById("modalIdBadge").style.color = tac.color;
      document.getElementById("modalTitle").innerText = tech.name;
      document.getElementById("modalDescription").innerText = tech.description;

      document.getElementById("modalCodeLabel").innerText = "ATTACK PAYLOAD & EXPLOIT COMMAND";
      document.getElementById("modalCodeLabel").style.color = "#38BDF8";
      currentModalCode = tech.attackCode;
      document.getElementById("modalCodeText").innerText = tech.attackCode;

      document.getElementById("modalCounterText").innerText = `D3FEND ${'$'}{tech.d3fendId} (${'$'}{tech.d3fendName}): ${'$'}{tech.d3fendDesc}`;

      document.getElementById("detailModal").classList.add("open");

      // Highlight connections
      if (currentMode === "ALL") {
        d3.selectAll(".link-path").classed("highlight", false);
        d3.selectAll(`[data-att="${'$'}{tech.id}"]`).classed("highlight", true);
      }
    }

    function openModalForDefend(tech, tac) {
      document.getElementById("modalIdBadge").innerText = `${'$'}{tac.displayName} • ${'$'}{tech.id}`;
      document.getElementById("modalIdBadge").style.borderColor = "#10B981";
      document.getElementById("modalIdBadge").style.color = "#10B981";
      document.getElementById("modalTitle").innerText = tech.name;
      document.getElementById("modalDescription").innerText = tech.description;

      document.getElementById("modalCodeLabel").innerText = "D3FEND ENFORCEMENT & REMEDIATION RULE";
      document.getElementById("modalCodeLabel").style.color = "#10B981";
      currentModalCode = tech.ruleOrFix;
      document.getElementById("modalCodeText").innerText = tech.ruleOrFix;

      document.getElementById("modalCounterText").innerText = `Neutralizes ATT&CK Techniques: ${'$'}{tech.counteredAttackIds.join(", ")}`;

      document.getElementById("detailModal").classList.add("open");

      // Highlight connections
      if (currentMode === "ALL") {
        d3.selectAll(".link-path").classed("defend-highlight", false);
        d3.selectAll(`[data-defend="${'$'}{tech.id}"]`).classed("defend-highlight", true);
      }
    }

    function closeModal() {
      document.getElementById("detailModal").classList.remove("open");
      d3.selectAll(".link-path").classed("highlight", false).classed("defend-highlight", false);
    }

    function copyModalCode() {
      if (window.AndroidBridge && window.AndroidBridge.copyText) {
        window.AndroidBridge.copyText(currentModalCode, "Code Snippet");
      } else {
        navigator.clipboard.writeText(currentModalCode);
        alert("Copied to clipboard!");
      }
    }

    window.addEventListener("load", initChart);
    window.addEventListener("resize", initChart);
  </script>
</body>
</html>
        """.trimIndent()
    }
}
