# Google Search Grounding with Gemini 3.5 Flash

Enables real-time Google Search Grounding across CyberShield using `gemini-3.5-flash`. Cyber threat intelligence requires up-to-the-minute awareness of zero-day exploits, newly released CISA KEV advisories, vendor patches, and active in-the-wild campaigns. This feature equips the Gemini AI engine with Google Search tools, grounding responses in live web sources and rendering source links/citations.

## User Review & Critical Decisions

> [!IMPORTANT]
> - **Model & Tool**: Using `gemini-3.5-flash` with the Google Search tool (`tools = [{"googleSearch": {}}]`) for live web grounding as requested.
> - **Search Grounding Toggle & Transparency**: In both the **Gemini AI Assistant** and **Threat Detail Analysis / Custom Q&A**, users will have a prominent, interactive "Google Search Grounding" toggle (enabled by default) and visual badges indicating web-grounded insights with clickable search queries and sources.
> - **Graceful Quota & Offline Handling**: If the API key is not configured or offline, high-fidelity cached DevSecOps heuristics and security feeds continue to operate seamlessly.

---

### 1. Overview & Core Concept

- **What It Does**:
  - Connects Gemini AI queries to live Google Search via the `tools: [{"googleSearch": {}}]` parameter in the REST API request.
  - Returns responses backed by up-to-date threat intel, active CVE exploits, and fresh vendor advisories.
  - Extracts and displays **Grounding Metadata** (search queries, web sources, citations, and reference links) in the UI.
- **Where It Is Applied**:
  1. **Gemini Chatbot (`GeminiChatbotScreen.kt`)**: Dedicated "Google Search Grounding" toggle pill in the input bar and top bar. Displays grounded web source badges and citation links directly under AI messages.
  2. **Threat Intelligence Detail (`ThreatDetailScreen.kt`)**: Real-time CVE and zero-day threat analysis with live web search grounding to check if a vulnerability is being actively exploited right now or if a hotfix was released today.
  3. **Live Web Query Quick Prompts**: Ready-to-tap search queries like *"Check active zero-days in CISA KEV today"*, *"Latest OpenSSH / XZ backdoor updates"*, and *"Recent Ransomware campaign indicators"*.

---

### 2. User Experience & Visual Design

- **Visual Style**: Cyber-themed (CyberCyan `#06B6D4`, Emerald `#10B981`, Navy surfaces `#0D1527`), matching CyberShield's high-tech SOC aesthetic.
- **Search Grounding Indicator Pill**:
  - A glowing Google Search chip: `🌐 Google Search Grounded` with a toggle switch.
  - Active state: Subtle cyan glow, badge indicating *"Live Web Intelligence Active"*.
- **Grounding Sources & Citations Component**:
  - Collapsible/expandable card below grounded responses showing:
    - 🔍 **Web Search Queries Used** (e.g. `cve-2024-3094 active exploitation status`)
    - 🔗 **Verified Sources & Web Citations** with favicon/source title and direct clickable URLs that launch the system browser safely.
- **Touch Targets & Edge-to-Edge**: Full compliance with M3 48.dp interactive touch targets and safe window insets.

---

### 3. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│               UI / View Layer                         │
│  - GeminiChatbotScreen (Search Grounding Toggle & Badges)│
│  - ThreatDetailScreen (Live Grounded Analysis Card)   │
│  - GroundingSourcesCard (Citations & Web Queries)     │
└──────────────────────────┬─────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│               ThreatFeedViewModel                     │
│  - isSearchGroundingEnabled: StateFlow<Boolean>       │
│  - sendChatMessage(..., enableSearchGrounding = true) │
│  - requestGroundedAiSummary(threat)                   │
└──────────────────────────┬─────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│               GeminiService (REST)                    │
│  - Model: "gemini-3.5-flash"                           │
│  - Body: contents + tools: [{"googleSearch": {}}]     │
│  - Response: candidate.content + groundingMetadata    │
│    (webSearchQueries, groundingChunks, sources)       │
└────────────────────────────────────────────────────────┘
```

#### API Request Schema with Google Search Tool:
```json
{
  "contents": [ ... ],
  "tools": [
    {
      "googleSearch": {}
    }
  ],
  "generationConfig": {
    "temperature": 0.4
  }
}
```

#### API Response Parsing:
Extract `candidates[0].groundingMetadata` which provides:
- `webSearchQueries`: list of search queries executed.
- `groundingChunks`: list of sources with `web: { uri, title }`.
- `groundingSupports`: grounding segments linking text to chunks.

---

### 4. Implementation Steps

1. **Update API Data Models (`GeminiService.kt`)**:
   - Add `GeminiTool` model:
     ```kotlin
     @JsonClass(generateAdapter = true)
     data class GeminiGoogleSearchTool(
         @Json(name = "googleSearch") val googleSearch: Map<String, String> = emptyMap()
     )
     ```
   - Add `groundingMetadata` models (`GeminiGroundingMetadata`, `GeminiGroundingChunk`, `GeminiWebChunk`, etc.).
   - Include `tools: List<GeminiGoogleSearchTool>? = null` in `GeminiGenerateRequest`.
2. **Update `ChatMessage` & `ChatbotReply` Models**:
   - Add `groundingMetadata` (queries and web sources list) to `ChatMessage` and `GeminiAnalysisResult`.
3. **Enhance `GeminiService.kt`**:
   - Support `enableSearchGrounding: Boolean` in `sendChatMessage`, `analyzeThreat`, and `askCustomQuestion`.
   - Force `gemini-3.5-flash` whenever search grounding is active for optimal search tool invocation.
   - Parse `groundingMetadata` and attach sources to the returned message.
4. **Update `ThreatFeedViewModel.kt`**:
   - Add `val isSearchGroundingEnabled = MutableStateFlow(true)`.
   - Pass search grounding flag to `geminiService`.
5. **Update UI Components (`GeminiChatbotScreen.kt` & `ThreatDetailScreen.kt`)**:
   - Add Grounding Toggle in `GeminiChatbotScreen` top/input bar.
   - Add `GroundingSourcesSection` displaying web search queries and citation chips that open source URLs safely.
   - Add a "Live Grounded Search" button in `ThreatDetailScreen` to search the web for the latest zero-day intel.
6. **Verification**:
   - Verify compilation with `compile_applet`.
   - Confirm safety handling for URL clicks and no regressions.
