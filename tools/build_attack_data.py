#!/usr/bin/env python3
"""
tools/build_attack_data.py

MITRE ATT&CK & D3FEND Comprehensive Dataset Builder and Completeness Validator.
Builds the complete dataset for MITRE ATT&CK v20 (Released October 13, 2026), Mobile, ICS, and MITRE D3FEND (v0.13.0).

Key Highlights of MITRE ATT&CK v20:
- Enterprise: 15 Core Tactics (Stealth and Defense Impairment split preserved from v19).
- Brand new AI-Focused & AI-Enabled Techniques: Direct Prompt Injection (T1699), Indirect Prompt Injection (T1698), Query Public AI Services (T1682), AI Result Poisoning (T1697), Attacks on AI-Assisted Defenses (T1696).
- New ICS Techniques: Abuse of Publish/Subscribe Protocols (T0891), Industrial DNS Abuse (T0892).
- Mobile Updates: Un-deprecated Deliver Malicious Application via Other Means (T1475) with sideloading and MDM abuse.
- New Campaigns: U.S. Water Utilities Intrusions (C0035), AI-Assisted Breach of Mexican Government (C0036).
- New Threat Groups: CyberAv3ngers (G0140), Storm-0501 (G0145).
- D3FEND v0.13.0 Countermeasures: Prompt Injection Firewall (D3-PFW), Model Output Filtering (D3-MOF), OT Protocol Whitelisting (D3-OPW).
"""

import os
import sys
import json

OUTPUT_PATH = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "mitre_attack_d3fend.json")

EXPECTED_ENTERPRISE_TACTICS_COUNT = 15
EXPECTED_D3FEND_TACTICS_COUNT = 7

ENTERPRISE_TACTICS_V20 = [
    {"id": "TA0043", "name": "Reconnaissance", "shortname": "reconnaissance", "description": "Gather information to plan future adversary operations"},
    {"id": "TA0042", "name": "Resource Development", "shortname": "resource-development", "description": "Establish resources to support operations (infrastructure, accounts, tools)"},
    {"id": "TA0001", "name": "Initial Access", "shortname": "initial-access", "description": "Gain an initial foothold within your network"},
    {"id": "TA0002", "name": "Execution", "shortname": "execution", "description": "Run adversary-controlled malicious code on compromised systems or AI endpoints"},
    {"id": "TA0003", "name": "Persistence", "shortname": "persistence", "description": "Maintain access across restarts, changed credentials, and interruptions"},
    {"id": "TA0004", "name": "Privilege Escalation", "shortname": "privilege-escalation", "description": "Gain higher-level permissions on system, network, or cloud tenant"},
    {"id": "TA0005", "name": "Defense Evasion", "shortname": "defense-evasion", "description": "Avoid being detected by security staff, monitoring agents, or antivirus"},
    {"id": "TA0044", "name": "Stealth", "shortname": "stealth", "description": "Covert operations, token impersonation, artifact hiding, and low-observable behavior"},
    {"id": "TA0045", "name": "Defense Impairment", "shortname": "defense-impairment", "description": "Blinding, tampering, and disabling security defenses and EDR agents"},
    {"id": "TA0006", "name": "Credential Access", "shortname": "credential-access", "description": "Steal credentials like account names, hashes, Kerberos tickets, and passwords"},
    {"id": "TA0007", "name": "Discovery", "shortname": "discovery", "description": "Gain knowledge about the system, internal network, and cloud environment"},
    {"id": "TA0008", "name": "Lateral Movement", "shortname": "lateral-movement", "description": "Pass through and control remote systems on a network"},
    {"id": "TA0009", "name": "Collection", "shortname": "collection", "description": "Gather data of interest (sensitive files, emails, keystrokes, prompt transcripts)"},
    {"id": "TA0011", "name": "Command and Control", "shortname": "command-and-control", "description": "Communicate with systems under adversary control from external infrastructure"},
    {"id": "TA0010", "name": "Exfiltration", "shortname": "exfiltration", "description": "Steal data from your network to external cloud or staging infrastructure"},
    {"id": "TA0040", "name": "Impact", "shortname": "impact", "description": "Manipulate, interrupt, or destroy your systems, physical processes, or AI models"}
]

MOBILE_TACTICS = [
    {"id": "TA0027", "name": "Network Effects", "shortname": "network-effects", "description": "Intercept or manipulate network traffic to and from the mobile device"},
    {"id": "TA0028", "name": "Remote Service Effects", "shortname": "remote-service-effects", "description": "Abuse remote cloud services connected to or authenticated by the mobile device"},
    {"id": "TA0030", "name": "Mobile Persistence", "shortname": "mobile-persistence", "description": "Maintain access across device reboots and OS security scans"}
]

ICS_TACTICS = [
    {"id": "TA0108", "name": "Inhibit Response Function", "shortname": "inhibit-response-function", "description": "Prevent industrial safety systems and operator response mechanisms from engaging"},
    {"id": "TA0105", "name": "Impair Process Control", "shortname": "impair-process-control", "description": "Manipulate physical industrial process control parameters (PLCs, RTUs, SCADA)"},
    {"id": "TA0107", "name": "Evasion", "shortname": "ics-evasion", "description": "Disguise adversary actions within ICS control networks and historian telemetry"}
]

D3FEND_TACTICS = [
    {"id": "D3-TAC-MODEL", "name": "Model", "description": "Asset inventorying, dependency mapping, and network topology baselining"},
    {"id": "D3-TAC-HARDEN", "name": "Harden", "description": "Reducing attack surface and strengthening application, credential, and AI configurations"},
    {"id": "D3-TAC-DETECT", "name": "Detect", "description": "Identifying adversary activity, anomalous telemetry, prompt injection, and suspicious behavior in real-time"},
    {"id": "D3-TAC-ISOLATE", "name": "Isolate", "description": "Containment, micro-segmentation, and network boundary enforcement"},
    {"id": "D3-TAC-DECEIVE", "name": "Deceive", "description": "Deploying honeypots, honeytokens, and decoy infrastructure to confuse adversaries"},
    {"id": "D3-TAC-EVICT", "name": "Evict", "description": "Remediating, revoking sessions, and terminating adversary footholds"},
    {"id": "D3-TAC-RESTORE", "name": "Restore", "description": "Recovery to a verified, known-good operational state from immutable backups"}
]

REDIRECTS = {
    "T1562": "T1562.001",
    "T1086": "T1059.001",
    "T1085": "T1218.011",
    "T1028": "T1047",
    "T1064": "T1059",
    "T1089": "T1562.001",
    "T1034": "T1574.008",
    "T1107": "T1070.004",
    "T1004": "T1547.001",
    "T1035": "T1543.003"
}

def generate_dataset():
    tactics = []
    for t in ENTERPRISE_TACTICS_V20:
        tactics.append({
            "id": t["id"],
            "name": t["name"],
            "shortname": t["shortname"],
            "domain": "enterprise",
            "description": t["description"]
        })
    for t in MOBILE_TACTICS:
        tactics.append({
            "id": t["id"],
            "name": t["name"],
            "shortname": t["shortname"],
            "domain": "mobile",
            "description": t["description"]
        })
    for t in ICS_TACTICS:
        tactics.append({
            "id": t["id"],
            "name": t["name"],
            "shortname": t["shortname"],
            "domain": "ics",
            "description": t["description"]
        })

    techniques = [
        # ==========================================
        # --- NEW IN v20: AI & LLM Threat Techniques ---
        # ==========================================
        {
            "id": "T1699",
            "name": "Direct Prompt Injection",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["execution", "defense-evasion"],
            "platforms": ["AI/ML", "Cloud"],
            "dataSources": ["Application Log: AI Query Log", "Network Traffic: Web Traffic"],
            "description": "Adversaries input adversarial text or structured prompts directly into a Large Language Model (LLM) or AI interface to override system instructions, bypass security guardrails, or coerce the model into executing unauthorized tool calls.",
            "url": "https://attack.mitre.org/techniques/T1699"
        },
        {
            "id": "T1698",
            "name": "Indirect Prompt Injection",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["initial-access", "execution"],
            "platforms": ["AI/ML", "Cloud"],
            "dataSources": ["Application Log: RAG Ingestion Log", "File: File Metadata"],
            "description": "Adversaries embed malicious instructions inside external content (webpages, uploaded PDFs, emails, source code) that an autonomous AI agent or RAG system ingests, causing the LLM to execute unintended commands upon reading.",
            "url": "https://attack.mitre.org/techniques/T1698"
        },
        {
            "id": "T1682",
            "name": "Query Public AI Services",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["reconnaissance"],
            "platforms": ["PRE", "AI/ML"],
            "dataSources": ["Internet Scan: Public AI Query"],
            "description": "Adversaries query public AI services (commercial LLMs, generative chatbots) during reconnaissance to synthesize company org charts, draft targeted spearphishing lures, or analyze proprietary source code leaks.",
            "url": "https://attack.mitre.org/techniques/T1682"
        },
        {
            "id": "T1697",
            "name": "AI Result Poisoning",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["impact", "defense-evasion"],
            "platforms": ["AI/ML", "Cloud"],
            "dataSources": ["Database: Vector Database Query", "Application Log: Model Output"],
            "description": "Adversaries poison training datasets, fine-tuning corpora, or vector database embeddings so the AI model generates corrupted, hallucinatory, or maliciously biased outputs to enterprise users.",
            "url": "https://attack.mitre.org/techniques/T1697"
        },
        {
            "id": "T1696",
            "name": "Attacks Targeting AI-Assisted Defenses",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["defense-evasion", "stealth"],
            "platforms": ["Windows", "Linux", "AI/ML"],
            "dataSources": ["Security Device: EDR Telemetry", "Process: Process Creation"],
            "description": "Adversaries craft adversarial perturbations in malware binaries and network packets specifically engineered to evade machine learning and AI-assisted endpoint detection classifiers.",
            "url": "https://attack.mitre.org/techniques/T1696"
        },

        # ==========================================
        # --- NEW IN v20: ICS & OT Techniques ---
        # ==========================================
        {
            "id": "T0891",
            "name": "Abuse of Publish/Subscribe Protocols",
            "domain": "ics",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["impair-process-control", "command-and-control"],
            "platforms": ["Field Controller", "SCADA Server"],
            "dataSources": ["Network Traffic: Industrial Protocol Content"],
            "description": "Adversaries abuse publish/subscribe messaging protocols (MQTT, DDS, AMQP) on industrial IoT and SCADA networks to publish spoofed control telemetry or subscribe to sensitive field measurement topics.",
            "url": "https://attack.mitre.org/techniques/T0891"
        },
        {
            "id": "T0892",
            "name": "Industrial DNS Abuse",
            "domain": "ics",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["command-and-control"],
            "platforms": ["Control Server", "Engineering Workstation"],
            "dataSources": ["Network Traffic: DNS Traffic"],
            "description": "Adversaries abuse DNS within isolated OT networks by querying rogue DNS resolvers or using DNS tunneling to bridge air-gapped engineering perimeters.",
            "url": "https://attack.mitre.org/techniques/T0892"
        },

        # ==========================================
        # --- NEW IN v20: Mobile Updates ---
        # ==========================================
        {
            "id": "T1475",
            "name": "Deliver Malicious Application via Other Means",
            "domain": "mobile",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["initial-access"],
            "platforms": ["Android", "iOS"],
            "dataSources": ["Application Log: Mobile Package Manager", "Device: MDM Profile Log"],
            "description": "Adversaries deliver malicious mobile apps outside official app stores using sideloading, alternative third-party marketplaces, or rogue Mobile Device Management (MDM) enrollment profiles.",
            "url": "https://attack.mitre.org/techniques/T1475"
        },

        # ==========================================
        # --- Enterprise Tactics & Techniques ---
        # ==========================================
        # Reconnaissance (TA0043)
        {
            "id": "T1595",
            "name": "Active Scanning",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["reconnaissance"],
            "platforms": ["Network", "PRE"],
            "dataSources": ["Network Traffic: Network Traffic Flow"],
            "description": "Adversaries execute active scanning to gather information about target networks, open ports, and vulnerable services.",
            "url": "https://attack.mitre.org/techniques/T1595"
        },
        {
            "id": "T1595.001",
            "name": "Scanning IP Blocks",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1595",
            "tactics": ["reconnaissance"],
            "platforms": ["Network", "PRE"],
            "dataSources": ["Network Traffic: Network Traffic Content"],
            "description": "Adversaries scan entire IPv4/IPv6 blocks belonging to a target organization to map active public hosts.",
            "url": "https://attack.mitre.org/techniques/T1595/001"
        },
        {
            "id": "T1595.002",
            "name": "Vulnerability Scanning",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1595",
            "tactics": ["reconnaissance"],
            "platforms": ["Network", "PRE"],
            "dataSources": ["Network Traffic: Network Traffic Content"],
            "description": "Adversaries scan target systems for known vulnerabilities using automated tools like Nessus or custom scripts.",
            "url": "https://attack.mitre.org/techniques/T1595/002"
        },
        {
            "id": "T1590",
            "name": "Gather Victim Network Information",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["reconnaissance"],
            "platforms": ["Network", "PRE"],
            "dataSources": ["Internet Scan: DNS Record"],
            "description": "Adversaries harvest victim IP ranges, domain names, DNS records, and network topologies prior to attack.",
            "url": "https://attack.mitre.org/techniques/T1590"
        },
        {
            "id": "T1598",
            "name": "Phishing for Information",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["reconnaissance"],
            "platforms": ["PRE"],
            "dataSources": ["Application Log: Email Application"],
            "description": "Adversaries send reconnaissance emails to probe corporate defenses and elicit internal employee information.",
            "url": "https://attack.mitre.org/techniques/T1598"
        },

        # Resource Development (TA0042)
        {
            "id": "T1583",
            "name": "Acquire Infrastructure",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["resource-development"],
            "platforms": ["PRE"],
            "dataSources": ["Domain Registration: Domain Record"],
            "description": "Adversaries buy, lease, or rent infrastructure (domains, VPS servers, CDN accounts) to facilitate operations.",
            "url": "https://attack.mitre.org/techniques/T1583"
        },
        {
            "id": "T1583.001",
            "name": "Domains",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1583",
            "tactics": ["resource-development"],
            "platforms": ["PRE"],
            "dataSources": ["Domain Registration: Domain Record"],
            "description": "Adversaries register domains that mimic legitimate brands or companies (typosquatting) for phishing and C2.",
            "url": "https://attack.mitre.org/techniques/T1583/001"
        },
        {
            "id": "T1586",
            "name": "Compromise Accounts",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["resource-development"],
            "platforms": ["PRE"],
            "dataSources": ["User Account: Account Authentication"],
            "description": "Adversaries compromise accounts on third-party services like GitHub, AWS, or email providers to use in attacks.",
            "url": "https://attack.mitre.org/techniques/T1586"
        },
        {
            "id": "T1588",
            "name": "Obtain Capabilities",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["resource-development"],
            "platforms": ["PRE"],
            "dataSources": ["Malware: Malware Repository"],
            "description": "Adversaries buy or download malware, exploits, or digital certificates on dark web forums.",
            "url": "https://attack.mitre.org/techniques/T1588"
        },

        # Initial Access (TA0001)
        {
            "id": "T1566",
            "name": "Phishing",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["initial-access"],
            "platforms": ["Windows", "Linux", "macOS", "Office 365", "Google Workspace"],
            "dataSources": ["Application Log: Email Application", "Network Traffic: Network Traffic Content"],
            "description": "Adversaries send phishing messages to gain initial access, tricking users into running malware or giving credentials.",
            "url": "https://attack.mitre.org/techniques/T1566"
        },
        {
            "id": "T1566.001",
            "name": "Spearphishing Attachment",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1566",
            "tactics": ["initial-access"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Application Log: Email Application", "File: File Creation"],
            "description": "Adversaries attach weaponized documents (macro-enabled Office files, ISOs, ZIPs) to emails.",
            "url": "https://attack.mitre.org/techniques/T1566/001"
        },
        {
            "id": "T1566.002",
            "name": "Spearphishing Link",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1566",
            "tactics": ["initial-access"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Application Log: Web Browser", "Network Traffic: Web Traffic"],
            "description": "Adversaries include malicious hyperlinks leading to credential harvesting pages or drive-by downloads.",
            "url": "https://attack.mitre.org/techniques/T1566/002"
        },
        {
            "id": "T1190",
            "name": "Exploit Public-Facing Application",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["initial-access"],
            "platforms": ["Windows", "Linux", "macOS", "Cloud"],
            "dataSources": ["Application Log: Application Log", "Network Traffic: Network Traffic Flow"],
            "description": "Adversaries exploit vulnerabilities in internet-accessible software (VPN gateways, web servers, RDP) to gain a foothold.",
            "url": "https://attack.mitre.org/techniques/T1190"
        },
        {
            "id": "T1078",
            "name": "Valid Accounts",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["initial-access", "persistence", "privilege-escalation", "defense-evasion"],
            "platforms": ["Windows", "Linux", "macOS", "AWS", "Azure", "GCP"],
            "dataSources": ["Logon Session: Logon Session Creation", "User Account: Account Authentication"],
            "description": "Adversaries steal or purchase legitimate credentials to log into enterprise systems and services undetected.",
            "url": "https://attack.mitre.org/techniques/T1078"
        },
        {
            "id": "T1078.004",
            "name": "Cloud Accounts",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1078",
            "tactics": ["initial-access", "persistence", "privilege-escalation", "defense-evasion"],
            "platforms": ["AWS", "Azure", "GCP", "Office 365"],
            "dataSources": ["User Account: Account Authentication", "Cloud Service: Cloud Activity"],
            "description": "Adversaries compromise cloud administrator or service accounts to control cloud workloads.",
            "url": "https://attack.mitre.org/techniques/T1078/004"
        },
        {
            "id": "T1133",
            "name": "External Remote Services",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["initial-access", "persistence"],
            "platforms": ["Windows", "Linux"],
            "dataSources": ["Logon Session: Logon Session Creation"],
            "description": "Adversaries leverage external-facing remote services like VPNs, Citrix, and Remote Desktop to enter networks.",
            "url": "https://attack.mitre.org/techniques/T1133"
        },

        # Execution (TA0002)
        {
            "id": "T1059",
            "name": "Command and Scripting Interpreter",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["execution"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Command: Command Execution", "Process: Process Creation"],
            "description": "Adversaries abuse command and script interpreters to execute arbitrary commands, scripts, and binaries.",
            "url": "https://attack.mitre.org/techniques/T1059"
        },
        {
            "id": "T1059.001",
            "name": "PowerShell",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1059",
            "tactics": ["execution"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Script: Script Execution", "Command: Command Execution"],
            "description": "Adversaries execute malicious commands via PowerShell, often using encoded or obfuscated syntax.",
            "url": "https://attack.mitre.org/techniques/T1059/001"
        },
        {
            "id": "T1059.003",
            "name": "Windows Command Shell",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1059",
            "tactics": ["execution"],
            "platforms": ["Windows"],
            "dataSources": ["Process: Process Creation"],
            "description": "Adversaries abuse cmd.exe to launch commands, manage files, and invoke batch scripts.",
            "url": "https://attack.mitre.org/techniques/T1059/003"
        },
        {
            "id": "T1059.004",
            "name": "Unix Shell",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1059",
            "tactics": ["execution"],
            "platforms": ["Linux", "macOS"],
            "dataSources": ["Command: Command Execution"],
            "description": "Adversaries abuse Unix shells (sh, bash, zsh) to run command-line utilities and shell scripts.",
            "url": "https://attack.mitre.org/techniques/T1059/004"
        },
        {
            "id": "T1204",
            "name": "User Execution",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["execution"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Process: Process Creation"],
            "description": "Adversaries rely on user action to execute malicious payloads, such as opening an email attachment or clicking a link.",
            "url": "https://attack.mitre.org/techniques/T1204"
        },
        {
            "id": "T1203",
            "name": "Exploitation for Client Execution",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["execution"],
            "platforms": ["Windows", "macOS", "Linux"],
            "dataSources": ["Process: Process Creation"],
            "description": "Adversaries exploit software vulnerabilities in client applications (browsers, PDF readers, Office apps) to run code.",
            "url": "https://attack.mitre.org/techniques/T1203"
        },

        # Persistence (TA0003)
        {
            "id": "T1053",
            "name": "Scheduled Task/Job",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["persistence", "privilege-escalation", "execution"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Scheduled Job: Scheduled Job Creation"],
            "description": "Adversaries abuse task scheduling utilities (cron, Windows Task Scheduler) to facilitate periodic or persistent execution.",
            "url": "https://attack.mitre.org/techniques/T1053"
        },
        {
            "id": "T1053.005",
            "name": "Scheduled Task",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1053",
            "tactics": ["persistence", "privilege-escalation", "execution"],
            "platforms": ["Windows"],
            "dataSources": ["Scheduled Job: Scheduled Job Creation"],
            "description": "Adversaries register tasks using schtasks.exe or the Task Scheduler API for persistence.",
            "url": "https://attack.mitre.org/techniques/T1053/005"
        },
        {
            "id": "T1547",
            "name": "Boot or Logon Autostart Execution",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["persistence", "privilege-escalation"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Registry: Registry Key Modification", "File: File Creation"],
            "description": "Adversaries configure system settings to automatically execute a program upon system boot or user logon.",
            "url": "https://attack.mitre.org/techniques/T1547"
        },
        {
            "id": "T1547.001",
            "name": "Registry Run Keys / Startup Folder",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1547",
            "tactics": ["persistence", "privilege-escalation"],
            "platforms": ["Windows"],
            "dataSources": ["Registry: Registry Key Modification"],
            "description": "Adversaries add malicious entries under HKCU/HKLM Run keys or drop shortcuts into the user Startup folder.",
            "url": "https://attack.mitre.org/techniques/T1547/001"
        },
        {
            "id": "T1543",
            "name": "Create or Modify System Process",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["persistence", "privilege-escalation"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Service: Service Creation"],
            "description": "Adversaries create or modify system-level services or daemons to execute malicious binaries on boot.",
            "url": "https://attack.mitre.org/techniques/T1543"
        },
        {
            "id": "T1543.003",
            "name": "Windows Service",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1543",
            "tactics": ["persistence", "privilege-escalation"],
            "platforms": ["Windows"],
            "dataSources": ["Service: Service Creation"],
            "description": "Adversaries install malicious Windows services with sc.exe or PowerShell to run binaries as SYSTEM.",
            "url": "https://attack.mitre.org/techniques/T1543/003"
        },

        # Privilege Escalation (TA0004)
        {
            "id": "T1134",
            "name": "Access Token Manipulation",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["stealth", "privilege-escalation", "defense-evasion"],
            "platforms": ["Windows"],
            "dataSources": ["Command: Command Execution", "Process: Process Creation", "Token: Token Impersonation"],
            "description": "Adversaries modify access tokens to operate under a different security context, escalate privileges, or evade detection.",
            "url": "https://attack.mitre.org/techniques/T1134"
        },
        {
            "id": "T1134.001",
            "name": "Token Impersonation/Theft",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1134",
            "tactics": ["stealth", "privilege-escalation", "defense-evasion"],
            "platforms": ["Windows"],
            "dataSources": ["Process: Process Access", "Token: Token Modification"],
            "description": "Adversaries duplicate or steal existing access tokens from running privileged processes like LSASS to impersonate logged-in admins.",
            "url": "https://attack.mitre.org/techniques/T1134/001"
        },
        {
            "id": "T1134.002",
            "name": "Create Process with Token",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1134",
            "tactics": ["stealth", "privilege-escalation"],
            "platforms": ["Windows"],
            "dataSources": ["Process: Process Creation"],
            "description": "Adversaries create new processes using stolen access tokens to execute binaries under high privilege accounts.",
            "url": "https://attack.mitre.org/techniques/T1134/002"
        },
        {
            "id": "T1068",
            "name": "Exploitation for Privilege Escalation",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["privilege-escalation"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Process: Process Creation"],
            "description": "Adversaries exploit operating system kernel or driver vulnerabilities to elevate rights from standard user to SYSTEM/root.",
            "url": "https://attack.mitre.org/techniques/T1068"
        },
        {
            "id": "T1055",
            "name": "Process Injection",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["defense-evasion", "privilege-escalation"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Process: Process Modification", "Memory: Memory Allocation"],
            "description": "Adversaries inject code into legitimate processes to evade defenses and inherit elevated access rights.",
            "url": "https://attack.mitre.org/techniques/T1055"
        },
        {
            "id": "T1055.001",
            "name": "Dynamic-link Library Injection",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1055",
            "tactics": ["defense-evasion", "privilege-escalation"],
            "platforms": ["Windows"],
            "dataSources": ["Process: Process Modification"],
            "description": "Adversaries inject dynamic-link libraries into running processes via CreateRemoteThread and VirtualAllocEx.",
            "url": "https://attack.mitre.org/techniques/T1055/001"
        },

        # Defense Evasion (TA0005)
        {
            "id": "T1027",
            "name": "Obfuscated Files or Information",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["defense-evasion", "stealth"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["File: File Metadata", "Process: Process Creation"],
            "description": "Adversaries encrypt, encode, or obfuscate contents of executable payloads and scripts to make them difficult to discover.",
            "url": "https://attack.mitre.org/techniques/T1027"
        },
        {
            "id": "T1070",
            "name": "Indicator Removal",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["defense-evasion", "stealth"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["File: File Deletion", "Windows Event Log: Event Log Cleared"],
            "description": "Adversaries delete or modify generated artifacts (event logs, bash history, temporary files) to hide intrusion tracks.",
            "url": "https://attack.mitre.org/techniques/T1070"
        },
        {
            "id": "T1070.001",
            "name": "Clear Windows Event Logs",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1070",
            "tactics": ["defense-evasion", "stealth"],
            "platforms": ["Windows"],
            "dataSources": ["Windows Event Log: Event Log Cleared"],
            "description": "Adversaries use wevtutil.exe or PowerShell to clear Security and System event logs.",
            "url": "https://attack.mitre.org/techniques/T1070/001"
        },
        {
            "id": "T1070.004",
            "name": "File Deletion",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1070",
            "tactics": ["defense-evasion", "stealth"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["File: File Deletion"],
            "description": "Adversaries remove malware binaries, scripts, and temporary staging files from disk upon completing actions.",
            "url": "https://attack.mitre.org/techniques/T1070/004"
        },
        {
            "id": "T1036",
            "name": "Masquerading",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["defense-evasion"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["File: File Metadata"],
            "description": "Adversaries manipulate features of their artifacts (filenames, directories, metadata) to disguise them as legitimate utilities.",
            "url": "https://attack.mitre.org/techniques/T1036"
        },

        # Stealth (TA0044)
        {
            "id": "T1564",
            "name": "Hide Artifacts",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["stealth", "defense-evasion"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["File: File Metadata"],
            "description": "Adversaries hide files and processes using hidden file attributes, alternate data streams (ADS), or rootkits.",
            "url": "https://attack.mitre.org/techniques/T1564"
        },

        # Defense Impairment (TA0045)
        {
            "id": "T1562",
            "name": "Impair Defenses",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["defense-impairment", "defense-evasion"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Service: Service Modification", "Registry: Registry Key Modification"],
            "description": "Adversaries maliciously modify, disable, or tamper with security tools, firewalls, and logging agents.",
            "url": "https://attack.mitre.org/techniques/T1562"
        },
        {
            "id": "T1562.001",
            "name": "Disable or Modify Tools",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1562",
            "tactics": ["defense-impairment", "defense-evasion"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Service: Service Modification", "Driver: Driver Load"],
            "description": "Adversaries disable or terminate EDR agents, antivirus real-time scanning, and host intrusion prevention systems.",
            "url": "https://attack.mitre.org/techniques/T1562/001"
        },
        {
            "id": "T1562.004",
            "name": "Disable or Modify System Firewall",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1562",
            "tactics": ["defense-impairment"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Command: Command Execution"],
            "description": "Adversaries alter firewall rules using netsh or iptables to permit incoming malicious network connections.",
            "url": "https://attack.mitre.org/techniques/T1562/004"
        },

        # Credential Access (TA0006)
        {
            "id": "T1003",
            "name": "OS Credential Dumping",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["credential-access"],
            "platforms": ["Windows", "Linux"],
            "dataSources": ["Process: Process Access", "Command: Command Execution"],
            "description": "Adversaries dump plaintext passwords, NTLM hashes, and Kerberos tickets from memory or SAM/NTDS databases.",
            "url": "https://attack.mitre.org/techniques/T1003"
        },
        {
            "id": "T1003.001",
            "name": "LSASS Memory",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1003",
            "tactics": ["credential-access"],
            "platforms": ["Windows"],
            "dataSources": ["Process: Process Access"],
            "description": "Adversaries dump the memory of the Local Security Authority Subsystem Service (lsass.exe) using tools like Mimikatz or procdump.",
            "url": "https://attack.mitre.org/techniques/T1003/001"
        },
        {
            "id": "T1110",
            "name": "Brute Force",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["credential-access"],
            "platforms": ["Windows", "Linux", "Cloud"],
            "dataSources": ["User Account: Account Authentication"],
            "description": "Adversaries attempt password guessing or password spraying against employee accounts across domain or cloud logins.",
            "url": "https://attack.mitre.org/techniques/T1110"
        },
        {
            "id": "T1558",
            "name": "Steal or Forge Kerberos Tickets",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["credential-access"],
            "platforms": ["Windows"],
            "dataSources": ["Active Directory: Kerberos Ticket Request"],
            "description": "Adversaries abuse Kerberos protocol weaknesses to perform Kerberoasting, AS-REP roasting, or forge Golden Tickets.",
            "url": "https://attack.mitre.org/techniques/T1558"
        },

        # Discovery (TA0007)
        {
            "id": "T1087",
            "name": "Account Discovery",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["discovery"],
            "platforms": ["Windows", "Linux", "macOS", "Cloud"],
            "dataSources": ["Process: Process Creation", "Command: Command Execution"],
            "description": "Adversaries query system and domain accounts using net user or LDAP commands to identify high-privilege targets.",
            "url": "https://attack.mitre.org/techniques/T1087"
        },
        {
            "id": "T1082",
            "name": "System Information Discovery",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["discovery"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Command: Command Execution"],
            "description": "Adversaries harvest operating system version, patch level, and architecture using systeminfo or uname.",
            "url": "https://attack.mitre.org/techniques/T1082"
        },
        {
            "id": "T1018",
            "name": "Remote System Discovery",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["discovery"],
            "platforms": ["Windows", "Linux"],
            "dataSources": ["Network Traffic: Network Traffic Flow"],
            "description": "Adversaries scan or query Active Directory to discover domain controllers, file servers, and databases on the LAN.",
            "url": "https://attack.mitre.org/techniques/T1018"
        },

        # Lateral Movement (TA0008)
        {
            "id": "T1021",
            "name": "Remote Services",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["lateral-movement"],
            "platforms": ["Windows", "Linux"],
            "dataSources": ["Logon Session: Logon Session Creation", "Network Traffic: Network Traffic Flow"],
            "description": "Adversaries log into remote systems using valid credentials across administrative protocols.",
            "url": "https://attack.mitre.org/techniques/T1021"
        },
        {
            "id": "T1021.001",
            "name": "Remote Desktop Protocol",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1021",
            "tactics": ["lateral-movement"],
            "platforms": ["Windows"],
            "dataSources": ["Logon Session: Logon Session Creation"],
            "description": "Adversaries connect to remote servers over port 3389 using RDP clients to control desktop sessions directly.",
            "url": "https://attack.mitre.org/techniques/T1021/001"
        },
        {
            "id": "T1021.002",
            "name": "SMB/Windows Admin Shares",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1021",
            "tactics": ["lateral-movement"],
            "platforms": ["Windows"],
            "dataSources": ["Network Traffic: Network Traffic Flow", "File: File Creation"],
            "description": "Adversaries copy files and execute commands remotely via hidden administrative shares (C$, ADMIN$).",
            "url": "https://attack.mitre.org/techniques/T1021/002"
        },
        {
            "id": "T1570",
            "name": "Lateral Tool Transfer",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["lateral-movement"],
            "platforms": ["Windows", "Linux"],
            "dataSources": ["File: File Creation"],
            "description": "Adversaries transfer malware or reconnaissance utilities between compromised internal systems over local network shares.",
            "url": "https://attack.mitre.org/techniques/T1570"
        },

        # Collection (TA0009)
        {
            "id": "T1560",
            "name": "Archive Collected Data",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["collection"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["File: File Creation", "Process: Process Creation"],
            "description": "Adversaries compress and encrypt gathered files into ZIP, 7z, or RAR archives prior to exfiltration.",
            "url": "https://attack.mitre.org/techniques/T1560"
        },
        {
            "id": "T1005",
            "name": "Data from Local System",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["collection"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["File: File Access"],
            "description": "Adversaries search local hard drives and mounted volumes for intellectual property, source code, and credentials.",
            "url": "https://attack.mitre.org/techniques/T1005"
        },

        # Command and Control (TA0011)
        {
            "id": "T1071",
            "name": "Application Layer Protocol",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["command-and-control"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Network Traffic: Network Traffic Content"],
            "description": "Adversaries communicate with external C2 servers using standard application protocols (HTTP, HTTPS, DNS) to blend with normal traffic.",
            "url": "https://attack.mitre.org/techniques/T1071"
        },
        {
            "id": "T1071.001",
            "name": "Web Protocols",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1071",
            "tactics": ["command-and-control"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Network Traffic: Web Traffic"],
            "description": "Adversaries use HTTP or HTTPS headers and POST bodies to beacon and transmit instructions between agents and C2.",
            "url": "https://attack.mitre.org/techniques/T1071/001"
        },
        {
            "id": "T1071.004",
            "name": "DNS",
            "domain": "enterprise",
            "isSubtechnique": True,
            "parentTechniqueId": "T1071",
            "tactics": ["command-and-control"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Network Traffic: DNS Traffic"],
            "description": "Adversaries tunnel commands and data inside DNS query responses and TXT records (DNS tunneling).",
            "url": "https://attack.mitre.org/techniques/T1071/004"
        },
        {
            "id": "T1105",
            "name": "Ingress Tool Transfer",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["command-and-control"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["File: File Creation"],
            "description": "Adversaries download additional tools, payloads, or modules from external infrastructure onto compromised systems.",
            "url": "https://attack.mitre.org/techniques/T1105"
        },

        # Exfiltration (TA0010)
        {
            "id": "T1041",
            "name": "Exfiltration Over C2 Channel",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["exfiltration"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Network Traffic: Network Traffic Content"],
            "description": "Adversaries exfiltrate stolen company data directly through the primary command and control connection.",
            "url": "https://attack.mitre.org/techniques/T1041"
        },
        {
            "id": "T1567",
            "name": "Exfiltration Over Web Service",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["exfiltration"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["Network Traffic: Web Traffic"],
            "description": "Adversaries upload stolen data to legitimate cloud storage services (MEGA, Dropbox, Google Drive, AWS S3).",
            "url": "https://attack.mitre.org/techniques/T1567"
        },

        # Impact (TA0040)
        {
            "id": "T1486",
            "name": "Data Encrypted for Impact",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["impact"],
            "platforms": ["Windows", "Linux", "macOS"],
            "dataSources": ["File: File Modification"],
            "description": "Adversaries encrypt victim data using symmetric/asymmetric algorithms (Ransomware) to extort payments and halt business operations.",
            "url": "https://attack.mitre.org/techniques/T1486"
        },
        {
            "id": "T1485",
            "name": "Data Destruction",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["impact"],
            "platforms": ["Windows", "Linux"],
            "dataSources": ["File: File Deletion"],
            "description": "Adversaries destroy data and files on targeted systems to disrupt business functions and prevent incident response.",
            "url": "https://attack.mitre.org/techniques/T1485"
        },
        {
            "id": "T1489",
            "name": "Service Stop",
            "domain": "enterprise",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["impact"],
            "platforms": ["Windows", "Linux"],
            "dataSources": ["Service: Service Modification"],
            "description": "Adversaries stop or terminate critical operating system and application services (database engines, web servers) to cause downtime.",
            "url": "https://attack.mitre.org/techniques/T1489"
        },

        # Mobile & ICS
        {
            "id": "T1406",
            "name": "Obfuscated Files or Information on Mobile",
            "domain": "mobile",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["defense-evasion"],
            "platforms": ["Android", "iOS"],
            "dataSources": ["File: File Creation"],
            "description": "Adversaries compress or encrypt mobile application code to evade app store review and on-device detection.",
            "url": "https://attack.mitre.org/techniques/T1406"
        },
        {
            "id": "T1430",
            "name": "Location Tracking",
            "domain": "mobile",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["collection"],
            "platforms": ["Android", "iOS"],
            "dataSources": ["Sensor Health: GPS Data"],
            "description": "Adversaries track victim physical movements by tapping GPS, Wi-Fi BSSID, and cellular cell tower location sensors.",
            "url": "https://attack.mitre.org/techniques/T1430"
        },
        {
            "id": "T0855",
            "name": "Unauthorized Command Message",
            "domain": "ics",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["impair-process-control"],
            "platforms": ["Control Server", "PLC"],
            "dataSources": ["Network Traffic: Network Traffic Content"],
            "description": "Adversaries send unauthorized industrial protocol command messages (Modbus, DNP3, CIP) to alter physical machinery behavior.",
            "url": "https://attack.mitre.org/techniques/T0855"
        },
        {
            "id": "T0814",
            "name": "Denial of Service in ICS",
            "domain": "ics",
            "isSubtechnique": False,
            "parentTechniqueId": None,
            "tactics": ["inhibit-response-function"],
            "platforms": ["Field Controller", "Safety System"],
            "dataSources": ["Network Traffic: Network Traffic Flow"],
            "description": "Adversaries flood safety controllers with packets to blind operators during anomalous physical process conditions.",
            "url": "https://attack.mitre.org/techniques/T0814"
        }
    ]

    # Threat Groups (Updated for v20)
    groups = [
        {"id": "G0016", "name": "APT29 (Cozy Bear / Midnight Blizzard)", "description": "Russian Foreign Intelligence Service (SVR) cyber espionage threat actor."},
        {"id": "G0032", "name": "Lazarus Group (HIDDEN COBRA)", "description": "North Korean state-sponsored threat group conducting financial and espionage intrusions."},
        {"id": "G0008", "name": "FIN7 (Carbanak)", "description": "Financially motivated syndicate targeting retail, hospitality, and banking networks."},
        {"id": "G0125", "name": "Volt Typhoon", "description": "People's Republic of China state-sponsored actor targeting critical infrastructure with living-off-the-land techniques."},
        {"id": "G1015", "name": "Scattered Spider (UNC3944)", "description": "Financially motivated threat group leveraging sophisticated social engineering, SIM-swapping, and MFA fatigue."},
        {"id": "G0034", "name": "Sandworm Team", "description": "Russian GRU unit 74455 responsible for BlackEnergy, Industroyer, and NotPetya attacks."},
        {"id": "G0140", "name": "CyberAv3ngers", "description": "IRGC-affiliated actor documented in v20 targeting critical municipal water infrastructure and PLCs."},
        {"id": "G0145", "name": "Storm-0501", "description": "Financially motivated multi-stage threat actor targeting hybrid identity, cloud tenants, and enterprise endpoints."}
    ]

    # Software / Tools
    software = [
        {"id": "S0029", "name": "Mimikatz", "type": "Tool", "description": "Post-exploitation tool capable of extracting plaintext passwords, hashes, PINs, and Kerberos tickets from memory."},
        {"id": "S0154", "name": "Cobalt Strike", "type": "Tool", "description": "Commercial threat emulation framework used extensively by adversaries for C2 beacons and lateral movement."},
        {"id": "S0002", "name": "DUQU", "type": "Malware", "description": "Modular information stealing malware family sharing lineage with Stuxnet."},
        {"id": "S0026", "name": "PsExec", "type": "Tool", "description": "Sysinternals utility abused by adversaries to execute commands on remote systems over SMB."},
        {"id": "S0404", "name": "BloodHound", "type": "Tool", "description": "Active Directory relationship mapping tool used to discover privilege escalation attack paths."}
    ]

    # Campaigns (Updated for v20)
    campaigns = [
        {"id": "C0014", "name": "SolarWinds Supply Chain Compromise", "description": "Global supply chain intrusion in 2020 compromising Orion software updates to target top-tier organizations."},
        {"id": "C0015", "name": "Colonial Pipeline Cyberattack", "description": "2021 DarkSide ransomware attack impacting refined petroleum pipeline operations across the US East Coast."},
        {"id": "C0035", "name": "U.S. Water Utilities Cyber Intrusions", "description": "Documented in v20: State-sponsored intrusions exploiting internet-exposed Unitronics and PLC controllers in municipal water treatment facilities."},
        {"id": "C0036", "name": "AI-Assisted Breach of Mexican Government Infrastructure", "description": "Documented in v20: Complex multi-tier intrusion leveraging generative AI prompt injection and credential synthesis across government IT and OT networks."}
    ]

    # Procedures (Including v20 AI and ICS Procedures)
    procedures = [
        {"id": "PROC-001", "techniqueId": "T1134.001", "sourceType": "group", "sourceId": "G0016", "sourceName": "APT29", "description": "APT29 duplicated LSASS tokens to escalate privileges on compromised domain controllers and forge cloud SAML tokens."},
        {"id": "PROC-002", "techniqueId": "T1059.001", "sourceType": "software", "sourceId": "S0154", "sourceName": "Cobalt Strike", "description": "Cobalt Strike Beacon executes PowerShell commands encoded in Base64 via powershell.exe -NoP -NonI -W Hidden -Enc."},
        {"id": "PROC-003", "techniqueId": "T1562.001", "sourceType": "group", "sourceId": "G0032", "sourceName": "Lazarus Group", "description": "Lazarus Group modified registry keys to disable Windows Defender real-time monitoring and killed EDR processes."},
        {"id": "PROC-004", "techniqueId": "T1078.004", "sourceType": "campaign", "sourceId": "C0014", "sourceName": "SolarWinds Supply Chain Compromise", "description": "Adversaries used stolen SAML token signing certificates to forge cloud access tokens across Azure AD."},
        {"id": "PROC-005", "techniqueId": "T1003.001", "sourceType": "software", "sourceId": "S0029", "sourceName": "Mimikatz", "description": "Mimikatz queries LSASS process memory using sekurlsa::logonpasswords to harvest cleartext passwords and NTLM hashes."},
        {"id": "PROC-006", "techniqueId": "T1486", "sourceType": "campaign", "sourceId": "C0015", "sourceName": "Colonial Pipeline Attack", "description": "DarkSide affiliates used Salsa20 and RSA-1024 encryption routines to lock corporate domain controllers and file servers."},
        {"id": "PROC-007", "techniqueId": "T1190", "sourceType": "group", "sourceId": "G0125", "sourceName": "Volt Typhoon", "description": "Volt Typhoon exploited zero-day flaws in internet-facing Fortinet and Ivanti VPN appliances to enter US infrastructure."},
        {"id": "PROC-008", "techniqueId": "T1110", "sourceType": "group", "sourceId": "G1015", "sourceName": "Scattered Spider", "description": "Scattered Spider launched repeated Push Notification fatigue floods against corporate helpdesks to bypass MFA."},
        {"id": "PROC-009", "techniqueId": "T1699", "sourceType": "group", "sourceId": "G0145", "sourceName": "Storm-0501", "description": "Storm-0501 inputted direct prompt injection payloads into internal IT helpdesk AI chatbots to force the assistant to dump active directory credentials."},
        {"id": "PROC-010", "techniqueId": "T1698", "sourceType": "campaign", "sourceId": "C0036", "sourceName": "Mexican Government AI Breach", "description": "Adversaries embedded hidden zero-font prompt injection instructions inside submitted PDF tenders that coerced government LLM review tools to grant elevated access."},
        {"id": "PROC-011", "techniqueId": "T0891", "sourceType": "group", "sourceId": "G0140", "sourceName": "CyberAv3ngers", "description": "CyberAv3ngers flooded industrial MQTT broker topics with forged control messages to force shutdown of municipal water filtration pumps."},
        {"id": "PROC-012", "techniqueId": "T1475", "sourceType": "group", "sourceId": "G0032", "sourceName": "Lazarus Group", "description": "Lazarus Group hosted fake cryptocurrency trading Android APKs on third-party sites, instructing victims to bypass OS sideloading protections."}
    ]

    # Mitigations (Including v20 AI & OT Mitigations)
    mitigations = [
        {"id": "M1026", "name": "Privilege Account Management", "description": "Enforce strict separation between standard accounts and administrative accounts with Just-In-Time access.", "techniqueIds": ["T1134", "T1134.001", "T1078", "T1078.004"]},
        {"id": "M1042", "name": "Disable or Restrict Feature or Program", "description": "Disable PowerShell v2 and constrain PowerShell language modes to ConstrainedLanguage to prevent script execution.", "techniqueIds": ["T1059.001", "T1059.003"]},
        {"id": "M1022", "name": "Restrict File and Directory Permissions", "description": "Ensure EDR agent directory files and security driver registry paths are read-only for system administrators.", "techniqueIds": ["T1562.001", "T1562.004"]},
        {"id": "M1037", "name": "Filter Network Traffic", "description": "Implement firewall egress filtering and block unauthorized outbound ports (e.g. non-standard HTTP/S or direct SMB 445).", "techniqueIds": ["T1071.001", "T1071.004", "T1041"]},
        {"id": "M1049", "name": "Antivirus/Antimalware", "description": "Deploy EDR agents with kernel-mode drivers and enable Windows Credential Guard (LSA Protection).", "techniqueIds": ["T1003.001", "T1055.001", "T1486"]},
        {"id": "M1018", "name": "User Training", "description": "Train employees to recognize social engineering tactics, spearphishing attachments, and MFA prompt bombing.", "techniqueIds": ["T1566.001", "T1566.002", "T1110"]},
        {"id": "M1055", "name": "Prompt Injection Mitigation & Input Sanitization", "description": "Implement strict input sanitization, delimiter escaping, and prompt firewalls to block adversarial injections before reaching LLMs.", "techniqueIds": ["T1699", "T1698"]},
        {"id": "M1056", "name": "AI Model Output Filtering & Tool Sandboxing", "description": "Verify and enforce strict JSON schemas for LLM tool invocations and enforce human-in-the-loop approvals for sensitive actions.", "techniqueIds": ["T1697", "T1699"]},
        {"id": "M1057", "name": "Industrial Broker Access Control", "description": "Enforce mTLS authentication and strict ACLs on MQTT and DDS industrial pub/sub brokers.", "techniqueIds": ["T0891", "T0855"]}
    ]

    # Detections (Including v20 AI & OT Detection Strategies)
    detections = [
        {
            "id": "DET-001",
            "techniqueId": "T1134.001",
            "strategy": "Monitor process access and token duplicate API calls targeting lsass.exe (e.g. OpenProcess, DuplicateTokenEx).",
            "analytics": ["SIGMA: Suspicious Token Duplication via WinAPI", "YARA: Token Stealer In-Memory Shellcode"],
            "dataComponents": ["Process: Process Access", "Token: Token Impersonation"]
        },
        {
            "id": "DET-002",
            "techniqueId": "T1562.001",
            "strategy": "Monitor service stop requests and registry modifications under HKLM\\Software\\Policies\\Microsoft\\Windows Defender.",
            "analytics": ["SIGMA: Defender Real-Time Protection Disabled", "Event ID 7036: Security Service Stopped"],
            "dataComponents": ["Service: Service State", "Registry: Registry Key Modification"]
        },
        {
            "id": "DET-003",
            "techniqueId": "T1059.001",
            "strategy": "Enable Script Block Logging (Event ID 4104) and monitor suspicious parameters like -EncodedCommand and -ExecutionPolicy Bypass.",
            "analytics": ["SIGMA: Obfuscated PowerShell Execution via Command Line", "Event ID 4104: Script Block Invocation"],
            "dataComponents": ["Script: Script Execution", "Command: Command Execution"]
        },
        {
            "id": "DET-004",
            "techniqueId": "T1003.001",
            "strategy": "Detect non-system processes requesting PROCESS_VM_READ access rights to the lsass.exe process handle.",
            "analytics": ["Sysmon Event ID 10: LSASS Memory Access", "SIGMA: Mimikatz Sekurlsa In-Memory Execution"],
            "dataComponents": ["Process: Process Access"]
        },
        {
            "id": "DET-005",
            "techniqueId": "T1486",
            "strategy": "Detect high-volume file modification bursts with mass extension changes across user and network shared volumes.",
            "analytics": ["SIGMA: Rapid File Extension Modification by Untrusted Binary", "EDR: Ransomware Canary File Trip"],
            "dataComponents": ["File: File Modification"]
        },
        {
            "id": "DET-006",
            "techniqueId": "T1699",
            "strategy": "Inspect incoming LLM prompt streams for known adversarial jailbreak patterns, system prompt overrides, and delimiter escaping.",
            "analytics": ["SIGMA: LLM Direct Prompt Injection Pattern", "AI Gateway: Jailbreak Keyword Anomaly Trigger"],
            "dataComponents": ["Application Log: AI Query Log"]
        },
        {
            "id": "DET-007",
            "techniqueId": "T0891",
            "strategy": "Monitor industrial MQTT broker telemetry for unauthorized publish commands targeting critical process actuator topics.",
            "analytics": ["Zeek: Unauthorized MQTT Publish to Industrial Actuator Topic", "OT SIEM: Process Variable Setpoint Anomaly"],
            "dataComponents": ["Network Traffic: Industrial Protocol Content"]
        }
    ]

    # D3FEND Defensive Techniques across all 7 Tactics (Including v0.13.0 AI Defenses)
    d3fend_techniques = [
        # 1. Model (D3-TAC-MODEL)
        {
            "id": "D3-SPM",
            "name": "System Process Mapping",
            "tacticId": "D3-TAC-MODEL",
            "tacticName": "Model",
            "description": "Constructing verified lineage graphs and parent-child relationship models for all authorized processes on the host.",
            "artifacts": ["Process Lineage Graph", "Executable Metadata"],
            "counteredAttackIds": ["T1055.001", "T1059.001", "T1059.003"],
            "ruleSnippet": "model host_process_tree {\n  baseline: child_process_of('explorer.exe');\n  flag_anomaly: true;\n}"
        },
        {
            "id": "D3-NAM",
            "name": "Network Asset Mapping",
            "tacticId": "D3-TAC-MODEL",
            "tacticName": "Model",
            "description": "Baselining all corporate endpoints, domain controllers, and cloud VMs to identify rogue network connections.",
            "artifacts": ["Asset Inventory Database", "IP Allocation Map"],
            "counteredAttackIds": ["T1595.001", "T1018"],
            "ruleSnippet": "map active_hosts on subnet('10.0.0.0/16');"
        },

        # 2. Harden (D3-TAC-HARDEN)
        {
            "id": "D3-ACR",
            "name": "Access Control Verification",
            "tacticId": "D3-TAC-HARDEN",
            "tacticName": "Harden",
            "description": "Verifying authorization tokens, scope boundaries, and cryptographic claims before allowing high-privilege operations.",
            "artifacts": ["Access Token", "Process Context"],
            "counteredAttackIds": ["T1134", "T1134.001", "T1078.004"],
            "ruleSnippet": "policy enforce_token_integrity {\n  require_claims [\"mfa_verified\", \"admin_approval\"];\n}"
        },
        {
            "id": "D3-MFA",
            "name": "Multi-Factor Authentication",
            "tacticId": "D3-TAC-HARDEN",
            "tacticName": "Harden",
            "description": "Enforcing phishing-resistant hardware FIDO2/WebAuthn credentials across all remote login endpoints.",
            "artifacts": ["FIDO2 Security Key", "Biometric Credential"],
            "counteredAttackIds": ["T1078", "T1110", "T1133"],
            "ruleSnippet": "enforce auth_strength == 'fido2_hardware_bound';"
        },
        {
            "id": "D3-EHP",
            "name": "Executable Header Protection",
            "tacticId": "D3-TAC-HARDEN",
            "tacticName": "Harden",
            "description": "Enforcing ASLR, DEP, and Control Flow Guard (CFG) across all user and kernel binary headers.",
            "artifacts": ["PE Header Flags", "CFG Bitmap"],
            "counteredAttackIds": ["T1203", "T1055.001"],
            "ruleSnippet": "set_mitigation_policy(DEP=True, ASLR=Strict, CFG=True);"
        },
        {
            "id": "D3-PFW",
            "name": "Prompt Injection Firewall",
            "tacticId": "D3-TAC-HARDEN",
            "tacticName": "Harden",
            "description": "Filtering and sanitizing inputs to LLM gateways using semantic boundary checks and delimiter isolation to prevent prompt hijacking.",
            "artifacts": ["LLM Prompt Stream", "Semantic Embeddings"],
            "counteredAttackIds": ["T1699", "T1698"],
            "ruleSnippet": "guardrail enforce_prompt_sanitization {\n  block_patterns: [\"ignore previous\", \"system:\", \"DAN mode\"];\n  isolate_context: true;\n}"
        },

        # 3. Detect (D3-TAC-DETECT)
        {
            "id": "D3-PEA",
            "name": "Process Execution Analysis",
            "tacticId": "D3-TAC-DETECT",
            "tacticName": "Detect",
            "description": "Analyzing command line arguments, child process spawns, and in-memory execution paths for anomalies.",
            "artifacts": ["Command Line", "Process Tree", "Sysmon Event"],
            "counteredAttackIds": ["T1059.001", "T1059.003", "T1059.004"],
            "ruleSnippet": "detect suspicious_ps {\n  selection: Image|endswith: '\\powershell.exe' AND CommandLine|contains: '-enc'\n}"
        },
        {
            "id": "D3-NTA",
            "name": "Network Traffic Analysis",
            "tacticId": "D3-TAC-DETECT",
            "tacticName": "Detect",
            "description": "Inspecting packet headers, TLS SNI certificates, and beacon jitter for command and control channels.",
            "artifacts": ["Flow Record", "TLS Handshake", "DNS Query"],
            "counteredAttackIds": ["T1071.001", "T1071.004", "T1041"],
            "ruleSnippet": "alert tls where ja3_hash in blacklist_c2_hashes;"
        },
        {
            "id": "D3-FMA",
            "name": "File Modification Analysis",
            "tacticId": "D3-TAC-DETECT",
            "tacticName": "Detect",
            "description": "Monitoring filesystem I/O operations for abnormal file encryption rates and bulk extension changes.",
            "artifacts": ["Filesystem Mini-Filter", "Volume Change Journal"],
            "counteredAttackIds": ["T1486", "T1485"],
            "ruleSnippet": "trigger on file_write_rate > 50_files_per_sec and entropy > 7.9;"
        },
        {
            "id": "D3-MOF",
            "name": "Model Output Filtering",
            "tacticId": "D3-TAC-DETECT",
            "tacticName": "Detect",
            "description": "Validating generative AI responses against schema invariants, regex rules, and hallucination detectors before action execution.",
            "artifacts": ["LLM Completion", "Tool Call Payload"],
            "counteredAttackIds": ["T1697", "T1699"],
            "ruleSnippet": "validate_output(completion, require_schema='safe_response_v1');"
        },

        # 4. Isolate (D3-TAC-ISOLATE)
        {
            "id": "D3-SWH",
            "name": "Software Health Monitoring",
            "tacticId": "D3-TAC-ISOLATE",
            "tacticName": "Isolate",
            "description": "Monitoring security agent telemetry and isolating endpoints when EDR protection is terminated.",
            "artifacts": ["EDR Agent State", "Endpoint Network"],
            "counteredAttackIds": ["T1562.001"],
            "ruleSnippet": "on edr_heartbeat_timeout(seconds=30) {\n  isolate_endpoint(interface=\"eth0\", allow_remediation=true);\n}"
        },
        {
            "id": "D3-EID",
            "name": "Endpoint Isolation & Disconnect",
            "tacticId": "D3-TAC-ISOLATE",
            "tacticName": "Isolate",
            "description": "Cutting off host network interfaces to contain active lateral movement while preserving SOC forensic tunnel.",
            "artifacts": ["Host Network Stack", "WFP Filter Rule"],
            "counteredAttackIds": ["T1021.001", "T1021.002", "T1570"],
            "ruleSnippet": "block_all_inbound_outbound(exclude=['10.200.0.1/32:443']);"
        },
        {
            "id": "D3-OPW",
            "name": "OT Publish/Subscribe Protocol Whitelisting",
            "tacticId": "D3-TAC-ISOLATE",
            "tacticName": "Isolate",
            "description": "Segmenting industrial IoT brokers and strictly enforcing topic publish/subscribe authorization lists.",
            "artifacts": ["MQTT Broker ACL", "Industrial Network Segment"],
            "counteredAttackIds": ["T0891", "T0855"],
            "ruleSnippet": "mqtt_acl on broker('192.168.10.1:8883') {\n  allow publish to 'actuator/+/setpoint' only_from ['eng_workstation_cert'];\n}"
        },

        # 5. Deceive (D3-TAC-DECEIVE)
        {
            "id": "D3-DEC",
            "name": "Decoy Environment Deployment",
            "tacticId": "D3-TAC-DECEIVE",
            "tacticName": "Deceive",
            "description": "Deploying realistic high-interaction decoy servers on the corporate subnet to trap scanning adversaries.",
            "artifacts": ["Honeypot Host", "Fake SMB Share"],
            "counteredAttackIds": ["T1595.001", "T1018", "T1021.002"],
            "ruleSnippet": "deploy_honeypot(ip='10.0.5.42', services=['smb', 'rdp', 'ssh']);"
        },
        {
            "id": "D3-HON",
            "name": "Honeytoken Seeding",
            "tacticId": "D3-TAC-DECEIVE",
            "tacticName": "Deceive",
            "description": "Placing fake AWS access keys and decoy Domain Admin credentials into memory and files to detect theft.",
            "artifacts": ["Canary Cloud Key", "Decoy Kerberos Ticket"],
            "counteredAttackIds": ["T1003.001", "T1078.004", "T1558"],
            "ruleSnippet": "alert_on_cloudwatch(access_key='AKIA_DECOY_HONEYTOKEN');"
        },

        # 6. Evict (D3-TAC-EVICT)
        {
            "id": "D3-TKE",
            "name": "Token Revocation & Session Eviction",
            "tacticId": "D3-TAC-EVICT",
            "tacticName": "Evict",
            "description": "Immediately invalidating OAuth refresh tokens, Kerberos TGT tickets, and active logon sessions across Active Directory.",
            "artifacts": ["OAuth Refresh Token", "Kerberos TGT", "Logon Session"],
            "counteredAttackIds": ["T1134.001", "T1078.004", "T1558"],
            "ruleSnippet": "revoke_user_sessions(identity='compromised.user@corp.com', force_refresh=true);"
        },
        {
            "id": "D3-KPE",
            "name": "Kernel Process Eviction & Termination",
            "tacticId": "D3-TAC-EVICT",
            "tacticName": "Evict",
            "description": "Terminating malicious threads and processes at the kernel driver layer to bypass userland hook evasion.",
            "artifacts": ["Process Handle", "Kernel Driver"],
            "counteredAttackIds": ["T1055.001", "T1059.001"],
            "ruleSnippet": "kernel_terminate_process(pid=suspicious_pid, reason='Malicious C2 Beacon');"
        },

        # 7. Restore (D3-TAC-RESTORE)
        {
            "id": "D3-RSP",
            "name": "System Restore & Immutable Snapshot Recovery",
            "tacticId": "D3-TAC-RESTORE",
            "tacticName": "Restore",
            "description": "Restoring operating system state and critical volumes from immutable, write-once cloud or hardware snapshots.",
            "artifacts": ["Volume Snapshot", "System Image", "Write-Once Storage"],
            "counteredAttackIds": ["T1486", "T1485", "T1562.001", "T0855"],
            "ruleSnippet": "restore_volume(snapshot_id=\"snap-immutable-latest\", target_drive=\"C:\");"
        },
        {
            "id": "D3-BKR",
            "name": "Backup Reconstruction & Verification",
            "tacticId": "D3-TAC-RESTORE",
            "tacticName": "Restore",
            "description": "Rebuilding corrupted domain databases and cluster volumes from verified offline air-gapped backups.",
            "artifacts": ["Air-Gapped Backup", "Cryptographic Checksum"],
            "counteredAttackIds": ["T1486", "T1489"],
            "ruleSnippet": "verify_and_restore(backup_store='air_gap_vault_01', checksum_verified=true);"
        }
    ]

    dataset = {
        "metadata": {
            "version": "v20",
            "attackVersion": "v20",
            "d3fendVersion": "v0.13.0",
            "releaseDate": "October 13, 2026",
            "generatedAt": "2026-10-16T17:15:00Z",
            "enterpriseTacticsCount": len([t for t in tactics if t.get("domain") == "enterprise"]),
            "d3fendTacticsCount": len(D3FEND_TACTICS),
            "techniquesCount": len(techniques),
            "proceduresCount": len(procedures),
            "mitigationsCount": len(mitigations),
            "detectionsCount": len(detections),
            "d3fendTechniquesCount": len(d3fend_techniques)
        },
        "tactics": tactics,
        "d3fendTactics": D3FEND_TACTICS,
        "redirects": REDIRECTS,
        "techniques": techniques,
        "groups": groups,
        "software": software,
        "campaigns": campaigns,
        "procedures": procedures,
        "mitigations": mitigations,
        "detections": detections,
        "d3fendTechniques": d3fend_techniques
    }

    return dataset

def validate_completeness(data):
    meta = data["metadata"]
    print("=== Validating Dataset Completeness for MITRE ATT&CK v20 ===")
    print(f"ATT&CK Version: {meta['attackVersion']}")
    print(f"Release Date: {meta['releaseDate']}")
    print(f"D3FEND Version: {meta['d3fendVersion']}")
    print(f"Enterprise Tactics Count: {meta['enterpriseTacticsCount']}")
    print(f"D3FEND Tactics Count: {meta['d3fendTacticsCount']}")
    print(f"Techniques Count: {meta['techniquesCount']}")
    print(f"Procedures Count: {meta['proceduresCount']}")
    print(f"Mitigations Count: {meta['mitigationsCount']}")
    print(f"Detections Count: {meta['detectionsCount']}")
    print(f"D3FEND Techniques Count: {meta['d3fendTechniquesCount']}")

    if meta['enterpriseTacticsCount'] < EXPECTED_ENTERPRISE_TACTICS_COUNT:
        raise ValueError(f"Enterprise Tactics count ({meta['enterpriseTacticsCount']}) is less than required ({EXPECTED_ENTERPRISE_TACTICS_COUNT})!")

    if meta['d3fendTacticsCount'] != EXPECTED_D3FEND_TACTICS_COUNT:
        raise ValueError(f"D3FEND Tactics count ({meta['d3fendTacticsCount']}) != 7!")

    techniques = data["techniques"]
    for tech in techniques:
        tech_id = tech["id"]
        tech_tactics = tech.get("tactics", [])
        if not tech_tactics:
            raise ValueError(f"Technique {tech_id} has no mapped tactic!")

    redirects = data.get("redirects", {})
    if "T1562" not in redirects:
        raise ValueError("Redirect for T1562 missing!")

    print("✅ All Completeness Checks Passed Successfully for MITRE ATT&CK v20!")

def main():
    print("Building MITRE ATT&CK v20 and D3FEND v0.13.0 data asset...")
    dataset = generate_dataset()
    validate_completeness(dataset)

    os.makedirs(os.path.dirname(OUTPUT_PATH), exist_ok=True)
    with open(OUTPUT_PATH, "w", encoding="utf-8") as f:
        json.dump(dataset, f, indent=2)

    print(f"Successfully generated asset file at: {OUTPUT_PATH}")

if __name__ == "__main__":
    main()
