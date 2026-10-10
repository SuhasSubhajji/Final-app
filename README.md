# 🛡️ CyberShield: Threat Intelligence & DevSecOps Android Platform

**CyberShield** is a modern, enterprise-grade Android application built with Kotlin and Jetpack Compose for cybersecurity professionals, SOC analysts, and DevSecOps engineers. It provides real-time vulnerability feed monitoring, CVSS risk scoring, interactive CVE deep-dives, actionable push alerts, and automated build package conflict diagnostics.

---

## ✨ Key Features

### 🔍 Real-Time Threat Feed & Multi-Window Time Filtering
- **Aggregated Vulnerability Intelligence**: Consolidated feed from NIST NVD, CISA Known Exploited Vulnerabilities (KEV), GitHub Advisory Database, and DevSecOps channels.
- **Time Observation Filters**: Filter threat feed and push alerts by observation windows:
  - ⏱️ **Past 24 Hours**
  - 📅 **Observed 2 Days Ago**
  - 🗓️ **Past 1 Week**
  - 📆 **Past 1 Month**
  - ⏳ **Past 3 Months** / **All Time**
- **Category & Bookmark Toggles**: Instant single-tap filter pills for DevSecOps, Cloud/K8s, Network Security, and saved bookmarks.

### 📊 CVSS v3.1 Risk Scoring & Interactive CVE Detail Sheet
- **Numeric CVSS v3.1 Risk Score & Severity Gauge**: Visual indication (Critical, High, Medium, Low) with progress gauge.
- **CVSS Vector Breakdown**: Attack Vector (AV), Attack Complexity (AC), Privileges Required (PR), User Interaction (UI), Scope (S), Confidentiality (C), Integrity (I), Availability (A).
- **Remediation Code & Mitigation Steps**: Practical code fixes, patches, and command-line remediations.
- **Deep Links & SOC Integration**: Direct navigation from CVE details to related SOC threats in CyberShield.

### 🔔 Actionable Push Alert Engine
- **Background CVE Scanner**: Evaluates incoming threat streams against severity thresholds.
- **Interactive Notifications**: Triggers heads-up Android push notifications with actionable options ("Review Fix", "Acknowledge").

### 📦 Package Conflict Resolution & CI/CD Keystore Alignment
- **Diagnostic Helper**: Automatic detection and troubleshooting guide when encountering `INSTALL_FAILED_UPDATE_INCOMPATIBLE` package signature mismatches.
- **Deterministic GitHub Actions Signing**: GitHub CI/CD workflow (`.github/workflows/build.yml`) configured with embedded base64 keystore decoding (`debug.keystore.base64`) ensuring seamless app updates across build environments.

---

## 🛠️ Architecture & Tech Stack

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose (Material Design 3 dark cybersecurity theme)
- **Architecture**: MVVM with Kotlin Coroutines & `StateFlow`
- **Data Storage**: Modern persistence & mock real-time threat feed generator
- **CI/CD**: GitHub Actions workflow generating signed APK releases

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug / Jellyfish or newer
- **JDK**: Version 17
- **Android SDK**: API Level 34 (Android 14) minimum target

### Building the Application

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/cybershield-android.git
   cd cybershield-android
   ```

2. **Assemble Debug APK**:
   ```bash
   gradle assembleDebug
   ```
   *Note: Always use `gradle` directly rather than `./gradlew` in the build container environment.*

3. **Install on Device/Emulator**:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🔐 Keystore Alignment & CI/CD Workflow

To prevent `INSTALL_FAILED_UPDATE_INCOMPATIBLE` signature conflicts when updating builds from GitHub Actions releases:

1. The project includes `debug.keystore.base64` in the root workspace.
2. `.github/workflows/build.yml` automatically restores this deterministic keystore during CI build steps:
   ```yaml
   - name: Restore matching debug keystore
     run: |
       if [ -f debug.keystore.base64 ]; then
         base64 -d debug.keystore.base64 > debug.keystore
       fi
   ```
3. If an update conflict is detected on device, CyberShield displays an in-app diagnostic card with instructions and one-click package name copying (`com.aistudio.cybershield`).

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for details.
