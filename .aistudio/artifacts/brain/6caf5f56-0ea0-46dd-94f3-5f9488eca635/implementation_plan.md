# Implementation Plan: Package Conflict Resolution & Keystore Alignment

The goal of this task is to resolve Android package conflict errors (`INSTALL_FAILED_UPDATE_INCOMPATIBLE`) when updating the app via GitHub CI/CD releases. This is achieved by embedding a matching deterministic debug keystore in the GitHub Actions build workflow and providing an interactive diagnostic and troubleshooting guide in the app.

---

## Proposed Changes

### 1. GitHub CI/CD Workflow (`/.github/workflows/build.yml`)
- Update `/.github/workflows/build.yml` to decode the committed Base64 debug keystore (`debug.keystore.base64`) directly in CI, instead of generating a random ephemeral keystore with `keytool -genkeypair`.
- Ensure APKs built on GitHub Actions share the exact same cryptographic signature certificate (`SHA256: 86:89:DA:B2:...`) as the app build environment.

### 2. In-App Package Conflict & Signature Diagnostic UI (`AppUpdateDialog.kt` & `AppUpdateManager.kt`)
- Update `AppUpdateDialog.kt` to add a dedicated **Signature Mismatch & Package Conflict Diagnostic Card** when installation fails or package conflicts occur.
- Provide clear step-by-step guidance:
  1. Explanation of signature certificate mismatches (`INSTALL_FAILED_UPDATE_INCOMPATIBLE`).
  2. Quick "Copy Package Name" button (`com.aistudio.cybershield.trsk`) for `adb uninstall` or system settings search.
  3. Guidance on how updating works once installed from a GitHub release with the unified keystore.
  4. Direct action buttons to retry or review GitHub release settings.

---

## Verification Plan

### Automated Compilation & Validation
- Run `compile_applet` to ensure Kotlin code builds with zero errors.
- Verify `/.github/workflows/build.yml` YAML syntax and step logic.

### User Guidance Verification
- Test `AppUpdateDialog.kt` UI components, ensuring all buttons have accessibility descriptions and `testTag` identifiers.
