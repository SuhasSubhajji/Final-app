# 🚀 CyberShield Release Notes (v1.0.2 - Build 3)

## 📌 Summary
This release resolves Android package update conflicts, embeds matching keystore configurations, and introduces multi-window date range filtering for vulnerability threat intelligence feeds.

---

## 🌟 Highlights

### 1. ⏱️ Date Range Threat Feed Filters
- Added horizontal quick-filter pills on the main Threat Feed:
  - **Past 24 Hours**
  - **Observed 2 Days Ago**
  - **Past 1 Week**
  - **Past 1 Month**
  - **Past 3+ Months / All Time**
- Real-time filtering by observation timestamp with dynamic result counting.

### 2. 🛡️ Package Conflict Resolution & Keystore Alignment
- Unique `applicationId` set to `com.aistudio.cybershield.v2` for clean installation without signature conflicts.
- Embedded `debug.keystore.base64` artifact in repository root for GitHub Actions workflow auto-signing.
- Added in-app Diagnostic & Troubleshooting guide for package update conflicts under Settings -> Diagnostics.

### 3. 🧠 Gemini AI & CVSS Vectors
- Integrated Gemini model for automated CVE impact analysis and remediation advice.
- Complete CVSS v3.1 / v4.0 vector string parser with breakdown cards for Attack Vector, Complexity, Privileges, User Interaction, Scope, and Confidentiality/Integrity/Availability impact.

### 4. 🗂️ Local Offline Persistence & Custom Cyber Themes
- Room Database offline caching with `OfflineCacheBanner` visual notification.
- M3 themes: Cyber Navy Dark, Cyber Obsidian, Matrix Green, and High Contrast.

---

## 🛠️ GitHub Actions CI/CD Integration
- Automated APK generation on `push` or `workflow_dispatch`.
- Restores matching debug keystore directly from `debug.keystore.base64` secret/artifact.
