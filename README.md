# 🛡️ CyberShield v2.0.0: Comprehensive Cyber Threat Intelligence & DevSecOps Platform

**CyberShield** (`com.aistudio.cybershield.v2`) is an enterprise-grade Android cybersecurity platform built with Kotlin and Jetpack Compose. It equips security engineers, SOC analysts, incident responders, and DevSecOps practitioners with complete threat intelligence, deep framework mapping, interactive visualizations, and automated defense tools.

---

## ✨ Comprehensive Capabilities

### ⚔️ Full MITRE ATT&CK & 🛡️ D3FEND Framework Integration
- **14 ATT&CK Tactics Matrix**: Initial Access, Execution, Persistence, Privilege Escalation, Defense Evasion, Credential Access, Discovery, Lateral Movement, Collection, Command & Control, Exfiltration, Impact, Reconnaissance, Resource Development.
- **Sub-Techniques Support**: Complete mapping of technique sub-ids (e.g. `T1190.001`, `T1059.001`, `T1003.001`) with specific attack commands, PowerShell/Bash proof-of-concept snippets, and exploitation rules.
- **D3FEND Countermeasures**: Defensive mapping (e.g. `D3-EFR`, `D3-IAM`, `D3-SCA`, `D3-AL`) with YARA/Sigma/Suricata/Zeek rules to neutralize offensive techniques.
- **Interactive Unified D3.js Graph & Matrix Screen**: Searchable matrix with fullscreen D3.js visualization, interactive node tooltips, filter modes, and bottom sheets.

### 🏛️ NIST Controls & 🎯 Cyber Kill Chain
- **NIST Special Publication 800-53 Mapping**: Direct mapping of offensive/defensive TTPs to NIST controls (`SI-4`, `AC-2`, `IA-5`, `SC-7`, `AU-6`, `CP-9`, `RA-5`, `SA-11`, `IR-4`).
- **Cyber Kill Chain Integration**: Phase-by-phase alignment (Reconnaissance, Weaponization, Delivery, Exploitation, Installation, Command & Control, Actions on Objectives) with real-world threat actor examples (e.g., MOVEit Transfer CVE-2023-34362, Log4Shell CVE-2021-44228, Okta MFA Bypass).

### 🎓 Microsoft SC-200 & SC-500 Exam Prep & AD Labs
- **SC-200 Certification**: Microsoft Security Operations Analyst study modules, KQL queries for Microsoft Defender XDR & Sentinel, and interactive quiz scenarios.
- **SC-500 Certification**: Microsoft Cybersecurity Architect study modules, Zero Trust architecture patterns, and Conditional Access policy design.
- **Active Directory & Red Team Labs**: Kerberoasting, AS-REP Roasting, DCSync attack paths, BloodHound enumeration, and domain escalation defense labs.

### 🤖 Gemini AI Threat Intelligence & On-Device RL Engine
- **Gemini AI Analysis**: Generative AI threat advisories with Google Search Grounding for real-time CVE analysis and zero-day threat verification.
- **SOC Reinforcement Learning Engine**: On-device RL scoring model evaluating alert severity, triage quality, and response recommendations.

### 🔄 Hardened In-App Updater
- **HTTPS & Version Check Engine**: Validates new updates directly against GitHub Releases (`app-version.json`), strictly comparing `versionCode` and `versionName` to prevent unnecessary prompts or downgrade attacks.
- **Secure File Provider**: Downloaded APKs are isolated within the private update cache directory (`cache/updates/`) with restricted FileProvider access.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3 Cybersecurity Theme)
- **Architecture**: MVVM with Kotlin Coroutines & `StateFlow`
- **Networking**: Retrofit 2 + OkHttp + Moshi (Type-safe JSON serialization)
- **Local Persistence**: Room Database (`cybershield.db`) + DataStore Preferences
- **Testing**: Robolectric local JVM test runner for fast, emulator-free testing

---

## 🚀 Building & Testing

### Prerequisites
- **Android Studio**: Ladybug / Jellyfish or newer
- **JDK**: Version 17
- **Android SDK**: API Level 36 target / API Level 24 minimum

### Build & Test Commands

1. **Compile & Build Debug APK**:
   ```bash
   gradle assembleDebug
   ```

2. **Execute Local Unit Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```

---

## 🔐 Security & Governance

- **API Keys**: Managed securely via AI Studio Secrets Panel (`BuildConfig.GEMINI_API_KEY`). Unconfigured keys gracefully fall back to local threat intelligence models.
- **WebView Hardening**: Restricted JavaScript interface, file/content access disabled (`allowFileAccess = false`), and URL scheme validation.
- **Backup Policy**: Local backups disabled (`android:allowBackup="false"`) to protect sensitive SOC logs and security credentials.

---

## 📜 License

Distributed under the MIT License.
