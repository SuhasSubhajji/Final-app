package com.example.data.learning

data class Sc200Domain(
    val id: String,
    val title: String,
    val percentageWeight: String,
    val description: String,
    val keySkills: List<String>,
    val highYieldTips: List<String>
)

data class Sc200PracticeQuestion(
    val id: String,
    val domainId: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

data class KqlQueryItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val query: String,
    val expectedOutcome: String
)

object Sc200ExamData {

    val domains: List<Sc200Domain> = listOf(
        Sc200Domain(
            id = "domain_defender_endpoint",
            title = "Mitigate Threats using Microsoft Defender for Endpoint",
            percentageWeight = "25-30%",
            description = "Covers onboarding endpoints, managing Attack Surface Reduction (ASR) rules, configuring EDR in block mode, initiating Live Response sessions, investigating alerts, and executing automated containment.",
            keySkills = listOf(
                "Deploy and verify onboarding packages via Intune, Group Policy, or local scripts",
                "Configure and tune Attack Surface Reduction (ASR) rules (e.g. Block credential stealing from LSASS)",
                "Investigate alerts, timelines, process trees, and device graphs in Microsoft Defender XDR",
                "Execute Live Response sessions to collect memory, inspect active processes, and isolate endpoints",
                "Configure EDR in block mode and Automated Investigation and Remediation (AIR) capabilities"
            ),
            highYieldTips = listOf(
                "EDR in block mode works alongside non-Microsoft antivirus as a second line of defense when non-Microsoft AV is primary.",
                "ASR rules can be set to Audit, Block, or Warn; always test in Audit mode using telemetry before enforcing Block mode.",
                "Automated Investigation and Response (AIR) creates remediation actions (e.g. quarantine file, kill process) requiring approval based on automation level."
            )
        ),
        Sc200Domain(
            id = "domain_defender_cloud_identity",
            title = "Mitigate Threats using Microsoft Defender for Cloud & Identity",
            percentageWeight = "20-25%",
            description = "Covers Microsoft Defender for Cloud (CSPM & CWPP), regulatory compliance dashboards, server protection plans, Defender for Identity sensor deployment, and Entra ID Protection.",
            keySkills = listOf(
                "Enable Cloud Security Posture Management (Foundational & Defender CSPM)",
                "Configure Defender for Identity sensors on Domain Controllers and Active Directory Federation Services (AD FS)",
                "Identify identity threat alerts: Golden Ticket, Pass-the-Hash, DCSync, and Kerberoasting",
                "Configure Entra ID Identity Protection sign-in risk and user risk Conditional Access policies",
                "Implement automated remediation for compromised identities and password changes"
            ),
            highYieldTips = listOf(
                "Defender for Identity sensors require domain controller capture; if installing standalone sensors, configure port mirroring for all DC traffic.",
                "Tag high-value accounts as 'Sensitive' in Defender for Identity to increase alert sensitivity.",
                "Entra ID Protection calculates real-time risk (sign-in risk) and offline behavioral risk (user risk)."
            )
        ),
        Sc200Domain(
            id = "domain_sentinel",
            title = "Mitigate Threats using Microsoft Sentinel (SIEM & SOAR)",
            percentageWeight = "50-55%",
            description = "The largest domain of SC-200. Covers Log Analytics workspace architecture, data connectors, KQL query writing, scheduled & NRT analytic rules, incident investigation, and automation playbooks via Azure Logic Apps.",
            keySkills = listOf(
                "Design and configure Microsoft Sentinel workspaces, data retention, and access control (RBAC)",
                "Deploy data connectors (Microsoft 365, Defender XDR, Syslog, CEF, Azure resources, AWS, threat intel)",
                "Write advanced Kusto Query Language (KQL) for detection, aggregation, and threat hunting",
                "Configure Scheduled Query rules, Near Real-Time (NRT) rules, and Microsoft Security incident creation rules",
                "Build incident automation rules and SOAR playbooks using Azure Logic Apps with Sentinel triggers",
                "Create Watchlists, utilize Advanced Security Information Model (ASIM) parsers, and manage Threat Intelligence (STIX/TAXII)"
            ),
            highYieldTips = listOf(
                "Automation Rules run first and can change incident status/assignees or trigger Playbooks; Playbooks cannot directly run Automation Rules.",
                "NRT analytic rules run once every minute and inspect events from the preceding minute; only single-table queries are supported.",
                "Watchlists enable correlation of external data (e.g. VIP employees, terminated staff, known bad IPs) without hardcoding into KQL."
            )
        )
    )

    val practiceQuestions: List<Sc200PracticeQuestion> = listOf(
        Sc200PracticeQuestion(
            id = "q1",
            domainId = "domain_sentinel",
            question = "You are configuring Microsoft Sentinel to automatically close low-severity informational incidents and send a Teams notification when a high-severity incident is created. What components should you configure?",
            options = listOf(
                "Create a single KQL hunting query with an alert trigger",
                "Create two Sentinel Automation Rules: one with a Close Incident action, and another that triggers a Logic App Playbook",
                "Configure a Microsoft Defender XDR custom detection rule with an automated remediation action",
                "Create an ASIM parser with an Azure Function output"
            ),
            correctOptionIndex = 1,
            explanation = "Sentinel Automation Rules allow you to triage incidents automatically (change severity, close incident, change owner) and trigger automated playbooks (Logic Apps) based on incident criteria such as severity or title."
        ),
        Sc200PracticeQuestion(
            id = "q2",
            domainId = "domain_defender_endpoint",
            question = "An organization has a non-Microsoft antivirus running as primary real-time protection on all Windows 11 endpoints. You want Microsoft Defender for Endpoint to remediate malicious artifacts discovered post-breach. What feature must you enable?",
            options = listOf(
                "Tamper Protection",
                "Endpoint Detection and Response (EDR) in block mode",
                "SmartScreen Filter",
                "Application Guard"
            ),
            correctOptionIndex = 1,
            explanation = "EDR in block mode provides post-breach protection when a non-Microsoft antivirus is primary. It allows Defender to remediate malicious items detected by EDR telemetry even if the third-party AV missed it during initial execution."
        ),
        Sc200PracticeQuestion(
            id = "q3",
            domainId = "domain_sentinel",
            question = "You need to write a KQL query in Microsoft Sentinel that detects when more than 10 failed logon attempts occur for the same account within a 15-minute window on a Windows server. Which query construct should you use?",
            options = listOf(
                "SecurityEvent | where EventID == 4624 | take 10",
                "SecurityEvent | where EventID == 4625 | summarize FailedLogons = count() by Account, bin(TimeGenerated, 15m) | where FailedLogons > 10",
                "SigninLogs | where ResultType == 0 | count",
                "DeviceEvents | where ActionType == 'LogonFailed' | extend Count = 10"
            ),
            correctOptionIndex = 1,
            explanation = "EventID 4625 represents failed logons in Windows SecurityEvent logs. Using 'summarize FailedLogons = count() by Account, bin(TimeGenerated, 15m)' groups failures into 15-minute buckets per account, followed by filtering for counts greater than 10."
        ),
        Sc200PracticeQuestion(
            id = "q4",
            domainId = "domain_defender_cloud_identity",
            question = "A security analyst receives an alert in Microsoft Defender for Identity indicating that an attacker performed a DCSync attack. What specific Active Directory operation was detected?",
            options = listOf(
                "Requesting an SPN ticket for a service account",
                "Replicating directory changes (DS-Replication-Get-Changes-All) pretending to be a Domain Controller to harvest password hashes",
                "Modifying the local SAM database on a workstation",
                "Resetting the DSRM Administrator password"
            ),
            correctOptionIndex = 1,
            explanation = "DCSync leverages the Directory Replication Service (DRS) Remote Protocol (DRSUAPI) to request directory partition updates from a DC, extracting password hashes (including KRBTGT) without executing code on the DC."
        ),
        Sc200PracticeQuestion(
            id = "q5",
            domainId = "domain_defender_endpoint",
            question = "Which Attack Surface Reduction (ASR) rule is specifically designed to stop attackers from stealing credentials from the Local Security Authority Subsystem Service (LSASS)?",
            options = listOf(
                "Block executable files from running unless they meet a prevalence, age, or trusted list criterion",
                "Block credential stealing from the Windows local security authority subsystem (lsass.exe)",
                "Block Adobe Reader from creating child processes",
                "Block persistence through WMI event subscription"
            ),
            correctOptionIndex = 1,
            explanation = "The ASR rule 'Block credential stealing from the Windows local security authority subsystem (lsass.exe)' (GUID: 9e6c4e1f-7d60-472f-ba1a-a39ef669e4b2) prevents untrusted processes from opening handles to lsass.exe to dump memory."
        ),
        Sc200PracticeQuestion(
            id = "q6",
            domainId = "domain_sentinel",
            question = "What is the primary benefit of using Advanced Security Information Model (ASIM) parsers in Microsoft Sentinel?",
            options = listOf(
                "To automatically encrypt all syslog traffic in transit",
                "To normalize telemetry from disparate security vendors (e.g. Cisco, Palo Alto, Fortinet) into a unified standard schema so a single detection rule works across all sources",
                "To compress logs by 90% in Log Analytics workspaces",
                "To bypass Sentinel data ingestion billing"
            ),
            correctOptionIndex = 1,
            explanation = "ASIM normalizes network, authentication, DNS, and process data from different vendors into standard schemas (e.g. _Im_Dns, _Im_NetworkSession), allowing analysts to write single analytics rules that function regardless of the underlying vendor appliance."
        )
    )

    val kqlCheatSheet: List<KqlQueryItem> = listOf(
        KqlQueryItem(
            id = "kql_ps_encoded",
            title = "Detect Encoded PowerShell Execution (LotL)",
            category = "Execution Hunting",
            description = "Hunts for suspicious PowerShell command lines using base64 encoded parameters commonly used to evade string filters.",
            query = """
DeviceProcessEvents
| where FileName in~ ("powershell.exe", "pwsh.exe")
| where ProcessCommandLine has_any ("-enc", "-encodedcommand", "-e", "frombase64string", "downloadstring", "iex")
| project TimeGenerated, DeviceName, AccountName, ProcessCommandLine, InitiatingProcessFileName
| order by TimeGenerated desc
| take 100
            """.trimIndent(),
            expectedOutcome = "Returns process execution events where PowerShell executed obfuscated or remote download scripts."
        ),
        KqlQueryItem(
            id = "kql_ransomware_shadow",
            title = "Detect Volume Shadow Copy Deletion (Ransomware)",
            category = "Impact & Defense Evasion",
            description = "Identifies commands commonly executed by ransomware prior to encryption to disable system recovery.",
            query = """
DeviceProcessEvents
| where ProcessCommandLine has_any (
    "vssadmin delete shadows",
    "vssadmin.exe delete shadows",
    "wmic shadowcopy delete",
    "wbadmin delete catalog",
    "bcdedit /set {default} bootstatuspolicy ignoreallfailures"
)
| project TimeGenerated, DeviceName, AccountName, FileName, ProcessCommandLine
            """.trimIndent(),
            expectedOutcome = "Immediate high-fidelity indicator of ransomware staging."
        ),
        KqlQueryItem(
            id = "kql_brute_force",
            title = "Detect Password Spraying / Brute Force",
            category = "Credential Access",
            description = "Identifies accounts with more than 10 failed authentication attempts within 15 minutes followed by a successful login.",
            query = """
SecurityEvent
| where TimeGenerated > ago(24h)
| where EventID in (4625, 4624)
| summarize 
    FailedCount = countif(EventID == 4625),
    SuccessCount = countif(EventID == 4624)
    by Account, IpAddress, bin(TimeGenerated, 15m)
| where FailedCount >= 10 and SuccessCount >= 1
| project TimeGenerated, Account, IpAddress, FailedCount, SuccessCount
            """.trimIndent(),
            expectedOutcome = "Flags potential compromised accounts resulting from brute-force attempts."
        ),
        KqlQueryItem(
            id = "kql_impossible_travel",
            title = "Detect Risky & Anomalous Sign-ins (Entra ID)",
            category = "Identity Threat Hunting",
            description = "Queries Entra ID sign-in telemetry for high-risk sign-ins and unusual location logins.",
            query = """
SigninLogs
| where TimeGenerated > ago(7d)
| where RiskLevelDuringSignIn in ("high", "medium") or RiskState in ("atRisk", "confirmedCompromised")
| project TimeGenerated, UserPrincipalName, IPAddress, Location, RiskDetail, RiskLevelDuringSignIn, AppDisplayName
| order by TimeGenerated desc
            """.trimIndent(),
            expectedOutcome = "Lists all high/medium risk Entra ID sign-in sessions for immediate credential revocation."
        ),
        KqlQueryItem(
            id = "kql_rare_parent_child",
            title = "Rare Office Spawning Shell Lineage",
            category = "Initial Access / Macro",
            description = "Detects productivity applications (Word, Excel, Outlook) spawning shell binaries or script interpreters.",
            query = """
DeviceProcessEvents
| where InitiatingProcessFileName in~ ("winword.exe", "excel.exe", "powerpnt.exe", "outlook.exe")
| where FileName in~ ("cmd.exe", "powershell.exe", "wscript.exe", "cscript.exe", "mshta.exe", "certutil.exe")
| project TimeGenerated, DeviceName, InitiatingProcessFileName, FileName, ProcessCommandLine, AccountName
            """.trimIndent(),
            expectedOutcome = "Uncovers malicious document execution and exploitation."
        )
    )

    val resourceLinks: List<com.example.data.model.RoadmapResourceLink> = listOf(
        com.example.data.model.RoadmapResourceLink(
            title = "Microsoft Learn: SC-200 Certification Study Guide",
            description = "Official curriculum breakdown, measured skills, exam updates, and sandbox assessment guides.",
            url = "https://learn.microsoft.com/en-us/credentials/certifications/resources/study-guides/sc-200",
            tag = "Official Guide",
            domainOrProvider = "learn.microsoft.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Microsoft Sentinel Ninja Training (Level 400)",
            description = "Complete masterclass covering Sentinel workspace architecture, data connectors, analytics rules, SOAR playbooks, and UEBA.",
            url = "https://techcommunity.microsoft.com/t5/microsoft-sentinel-blog/become-a-microsoft-sentinel-ninja-the-complete-level-400-training/ba-p/1246310",
            tag = "Deep Dive Lab",
            domainOrProvider = "techcommunity.microsoft.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Microsoft Defender XDR Virtual Ninja Training",
            description = "Hands-on training covering Defender for Endpoint (MDE), Defender for Identity (MDI), Defender for Cloud Apps (MDCA), and AIR automation.",
            url = "https://techcommunity.microsoft.com/t5/microsoft-defender-xdr-blog/become-a-microsoft-defender-xdr-ninja/ba-p/1589357",
            tag = "EDR & XDR Lab",
            domainOrProvider = "techcommunity.microsoft.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Kusto Query Language (KQL) Official Operator Reference",
            description = "Comprehensive query reference for summarize, make-series, join, extend, mv-expand, and externaldata hunting.",
            url = "https://learn.microsoft.com/en-us/azure/data-explorer/kusto/query/",
            tag = "Query Reference",
            domainOrProvider = "learn.microsoft.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "SC-200 Interactive Practice Assessment Sandbox",
            description = "Free official 50-question practice assessment testing all 3 core exam domains directly from Microsoft.",
            url = "https://learn.microsoft.com/en-us/credentials/certifications/exams/sc-200/practice/assessment",
            tag = "Practice Exam",
            domainOrProvider = "learn.microsoft.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Mitigate Threats using Microsoft Defender for Cloud Labs",
            description = "Interactive modules on Cloud Workload Protection (CWPP), regulatory standards, and multi-cloud security posture (CSPM).",
            url = "https://learn.microsoft.com/en-us/training/paths/mitigate-threats-using-microsoft-defender-for-cloud/",
            tag = "Cloud Security",
            domainOrProvider = "learn.microsoft.com"
        )
    )
}
