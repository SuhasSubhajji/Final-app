package com.example.data.learning

data class Sc500Domain(
    val id: String,
    val title: String,
    val percentageWeight: String,
    val description: String,
    val keySkills: List<String>,
    val highYieldTips: List<String>
)

data class Sc500PracticeQuestion(
    val id: String,
    val domainId: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

data class Sc500SecurityScript(
    val id: String,
    val title: String,
    val category: String,
    val commandOrPolicy: String,
    val description: String,
    val verificationCommand: String
)

object Sc500ExamData {

    val domains: List<Sc500Domain> = listOf(
        Sc500Domain(
            id = "domain_identity_access",
            title = "Manage Identity and Access in Microsoft Entra ID",
            percentageWeight = "25-30%",
            description = "Configuring Microsoft Entra ID (formerly Azure AD), Conditional Access policies, Privileged Identity Management (PIM), User Risk & Sign-in Risk evaluation, Access Reviews, and External Identities (B2B/B2C).",
            keySkills = listOf(
                "Configure Entra ID Conditional Access with compliant device, MFA, and continuous access evaluation (CAE)",
                "Implement Privileged Identity Management (PIM) for Azure Resources and Entra roles (JIT activation, approval workflows)",
                "Configure Entra ID Identity Protection user risk and sign-in risk policies with automated remediation",
                "Deploy and schedule Entra ID Access Reviews for group memberships and guest external identities",
                "Configure Managed Identities (System-assigned vs User-assigned) for Azure compute resources"
            ),
            highYieldTips = listOf(
                "PIM requires Entra ID P2 license; role assignments can be 'Eligible' (requires JIT elevation) or 'Active'.",
                "Continuous Access Evaluation (CAE) allows near real-time revocation of access tokens within seconds if critical events occur (password change, user disabled, IP location change).",
                "Managed Identities eliminate hardcoded credentials in source code by providing an automatically managed identity in Entra ID."
            )
        ),
        Sc500Domain(
            id = "domain_secure_networking",
            title = "Secure Networking & Platform Protection in Azure",
            percentageWeight = "20-25%",
            description = "Implementing Network Security Groups (NSGs), Application Security Groups (ASGs), Azure Firewall Premium with TLS inspection, Azure Bastion, Virtual Network peering, Private Endpoints, and Azure DDoS Protection.",
            keySkills = listOf(
                "Configure NSG stateful inbound/outbound rules and prioritize lower numerical values (100-4096)",
                "Deploy Azure Firewall Premium with IDPS (Intrusion Detection & Prevention System) and TLS inspection",
                "Implement Azure Private Link and Private Endpoints to eliminate public internet exposure of PaaS services",
                "Deploy Azure Bastion for secure clientless RDP/SSH management over TLS port 443 without public IPs",
                "Configure Azure Web Application Firewall (WAF) policies on Application Gateway and Azure Front Door"
            ),
            highYieldTips = listOf(
                "NSGs do not inspect application payload; use Azure Firewall Premium IDPS or WAF for Layer 7 deep packet inspection.",
                "Service Endpoints route traffic through Azure backbone but keep public IP endpoint; Private Endpoints assign a private IP directly into your VNet (RFC 1918).",
                "Application Security Groups (ASGs) allow grouping VMs by workload tag rather than explicit IP addresses in NSG rules."
            )
        ),
        Sc500Domain(
            id = "domain_compute_storage_database",
            title = "Secure Azure Compute, Storage, and Databases",
            percentageWeight = "20-25%",
            description = "Implementing Azure Key Vault (Access Policies vs RBAC), Customer-Managed Keys (CMK), Azure Disk Encryption with BitLocker/DM-Crypt, Storage Account firewalls, Container Security in AKS, and Azure SQL Transparent Data Encryption (TDE).",
            keySkills = listOf(
                "Configure Azure Key Vault secrets, keys, and certificates with Azure RBAC permission model",
                "Enable Key Vault Soft-Delete (mandatory) and Purge Protection to prevent ransomware destruction",
                "Implement Storage Account SAS (Shared Access Signature) with least-privilege, IP restrictions, and HTTPS enforcement",
                "Configure Transparent Data Encryption (TDE) with BYOK (Bring Your Own Key) on Azure SQL Database",
                "Harden Azure Kubernetes Service (AKS) clusters with Azure CNI, Pod Sandboxing, and Azure Policy for Kubernetes"
            ),
            highYieldTips = listOf(
                "Purge Protection ensures Key Vault secrets cannot be immediately wiped even by subscription owners until retention expires.",
                "Always prefer Azure RBAC over legacy Key Vault Access Policies for granular resource-level role assignments.",
                "Always disable Storage Account public blob access and enforce TLS 1.2+ minimum version."
            )
        ),
        Sc500Domain(
            id = "domain_security_operations",
            title = "Manage Security Operations & Governance in Azure",
            percentageWeight = "25-30%",
            description = "Deploying Microsoft Defender for Cloud, regulatory compliance benchmarks (CIS, NIST, ISO 27001), Azure Policy initiatives, Microsoft Sentinel workspace architecture, and Log Analytics retention.",
            keySkills = listOf(
                "Configure Microsoft Defender for Cloud plans (Defender for Servers, Defender for Storage, Defender for Containers)",
                "Assign Azure Policy initiatives and custom definitions to enforce compliance (e.g. deny unencrypted disks)",
                "Design and deploy Microsoft Sentinel log ingestion pipelines, workspace data caps, and retention policies",
                "Create Sentinel analytics rules (Scheduled, NRT, and Fusion correlation) with automated Logic App playbooks",
                "Conduct threat hunting in Sentinel using Kusto Query Language (KQL) and hunting bookmarks"
            ),
            highYieldTips = listOf(
                "Microsoft Defender for Cloud Security Score provides prioritized recommendations; disabling a recommendation lowers impact.",
                "Azure Policy 'DeployIfNotExists' and 'Modify' effects automatically remediate non-compliant resources.",
                "Sentinel Data Collection Rules (DCR) via the Azure Monitor Agent (AMA) replace the legacy MMA/OMS agent."
            )
        )
    )

    val practiceQuestions: List<Sc500PracticeQuestion> = listOf(
        Sc500PracticeQuestion(
            id = "sc500_q1",
            domainId = "domain_identity_access",
            question = "You need to ensure that global administrators only have high-level directory privileges when actively executing emergency tasks. Access must automatically expire after 4 hours, require MFA, and trigger an approval email to the CISO. Which feature should you configure?",
            options = listOf(
                "Azure Entra ID Conditional Access named locations",
                "Microsoft Entra Privileged Identity Management (PIM) with role eligibility and approval workflow",
                "Entra ID Access Reviews scheduled monthly",
                "System-assigned Managed Identity with Contributor rights"
            ),
            correctOptionIndex = 1,
            explanation = "Microsoft Entra Privileged Identity Management (PIM) allows assigning 'Eligible' status for administrative roles. When activated, administrators undergo JIT elevation requiring MFA, business justification, CISO approval, and a maximum activation duration (e.g. 4 hours)."
        ),
        Sc500PracticeQuestion(
            id = "sc500_q2",
            domainId = "domain_secure_networking",
            question = "Your organization hosts an Azure App Service that connects to an Azure SQL Database. Compliance mandates that traffic between the web app and the database must never traverse the public internet and the database must not have a public IP address. What should you deploy?",
            options = listOf(
                "Network Security Group on the web app subnet allowing port 1433",
                "Azure Service Endpoint with Microsoft.Sql service tag enabled",
                "Azure Private Endpoint in the database VNet with VNet integration on the App Service",
                "Azure Application Gateway with WAF v2 enabled"
            ),
            correctOptionIndex = 2,
            explanation = "Deploying an Azure Private Endpoint assigns a private RFC 1918 IP address from your Virtual Network to the Azure SQL Database. Combining this with App Service regional VNet integration ensures 100% private, internal-only connectivity without any public IP or public endpoint exposure."
        ),
        Sc500PracticeQuestion(
            id = "sc500_q3",
            domainId = "domain_compute_storage_database",
            question = "You are configuring an Azure Key Vault that holds database encryption keys. You must ensure that even a compromised Subscription Owner account cannot immediately delete or permanently purge the cryptographic keys during a ransomware incident. Which two configurations are required?",
            options = listOf(
                "Firewall IP filtering and Azure Bastion",
                "Soft-Delete enabled and Purge Protection enabled",
                "Local authentication enabled and TLS 1.0 enforcement",
                "Shared Access Signature (SAS) and Customer Lockbox"
            ),
            correctOptionIndex = 1,
            explanation = "Soft-Delete ensures deleted vaults/keys are retained in a recoverable state for a specified retention period (7 to 90 days). Purge Protection enforces that neither users nor Microsoft can permanently wipe items during the retention period, preventing ransomware key destruction."
        ),
        Sc500PracticeQuestion(
            id = "sc500_q4",
            domainId = "domain_security_operations",
            question = "You need to prevent developers in your subscription from creating virtual machines that do not have Azure Disk Encryption enabled. Non-compliant deployments must be blocked immediately at ARM deployment time. What should you use?",
            options = listOf(
                "Microsoft Sentinel scheduled analytics rule",
                "Azure Policy definition with an 'Audit' effect",
                "Azure Policy definition with a 'Deny' effect assigned to the resource group or subscription",
                "Microsoft Defender for Cloud alert suppression rule"
            ),
            correctOptionIndex = 2,
            explanation = "Azure Policy with a 'Deny' effect intercepts ARM template/API requests during pre-flight validation. If the resource definition violates the policy (e.g. unencrypted disk), the ARM deployment is blocked immediately and fails with a 403 Forbidden error."
        ),
        Sc500PracticeQuestion(
            id = "sc500_q5",
            domainId = "domain_identity_access",
            question = "An employee's password was compromised on the dark web and their Entra ID account is flagged as 'High User Risk'. Which Conditional Access control should be configured to let them automatically remediate their account without IT helpdesk intervention?",
            options = listOf(
                "Block access permanently for all high-risk accounts",
                "Require compliant device only",
                "Grant access requiring Multi-Factor Authentication AND secure self-service password reset (SSPR)",
                "Disable Continuous Access Evaluation (CAE)"
            ),
            correctOptionIndex = 2,
            explanation = "Configuring an Entra ID Identity Protection User Risk policy with the control 'Require password change' (coupled with MFA and SSPR) allows the user to perform a self-service password reset, immediately downgrading their risk score to low."
        )
    )

    val securityScripts: List<Sc500SecurityScript> = listOf(
        Sc500SecurityScript(
            id = "sc500_script_kv_purge",
            title = "Enforce Key Vault Purge Protection via Azure CLI",
            category = "Key Vault & Data Security",
            commandOrPolicy = "az keyvault update --name \"kv-prod-eastus-01\" --resource-group \"rg-security-prod\" --enable-purge-protection true",
            description = "Enables purge protection on Azure Key Vault. Once enabled, neither administrators nor subscription owners can permanently purge secrets before retention expires.",
            verificationCommand = "az keyvault show --name \"kv-prod-eastus-01\" --query \"properties.enablePurgeProtection\""
        ),
        Sc500SecurityScript(
            id = "sc500_script_storage_tls",
            title = "Enforce Minimum TLS 1.2 & Disable Public Blob Access",
            category = "Storage Hardening",
            commandOrPolicy = """
az storage account update \
  --name "stdataexportsprod" \
  --resource-group "rg-data-prod" \
  --min-tls-version TLS1_2 \
  --allow-blob-public-access false \
  --https-only true
            """.trimIndent(),
            description = "Hardens Azure Blob Storage by disallowing anonymous public read access, requiring minimum TLS 1.2, and enforcing secure HTTPS transfer.",
            verificationCommand = "az storage account show --name \"stdataexportsprod\" --query \"{minTls:minimumTlsVersion, publicAccess:allowBlobPublicAccess, httpsOnly:supportsHttpsTrafficOnly}\""
        ),
        Sc500SecurityScript(
            id = "sc500_script_nsg_bastion",
            title = "Deploy NSG Rule Restricting RDP/SSH to Azure Bastion",
            category = "Network Security",
            commandOrPolicy = """
az network nsg rule create \
  --resource-group "rg-networking-prod" \
  --nsg-name "nsg-backend-subnets" \
  --name "Allow-Bastion-Host-Inbound" \
  --priority 120 \
  --source-address-prefixes "10.0.1.0/27" \
  --source-port-ranges "*" \
  --destination-port-ranges 3389 22 \
  --access Allow \
  --protocol Tcp \
  --direction Inbound
            """.trimIndent(),
            description = "Creates an NSG rule permitting RDP (3389) and SSH (22) strictly from the dedicated AzureBastionSubnet, while blocking any inbound connections from the internet.",
            verificationCommand = "az network nsg rule list --resource-group \"rg-networking-prod\" --nsg-name \"nsg-backend-subnets\" -o table"
        ),
        Sc500SecurityScript(
            id = "sc500_script_pim_audit",
            title = "Audit Active Entra ID PIM Role Assignments via PowerShell",
            category = "Identity & Access Governance",
            commandOrPolicy = """
# Connect to Microsoft Graph PowerShell SDK
Connect-MgGraph -Scopes "RoleManagement.Read.Directory"

# Query all active privileged role assignments in Entra ID
Get-MgRoleManagementDirectoryRoleAssignmentScheduleInstance -Filter "assignmentType eq 'Assigned'" | 
  Select-Object PrincipalId, RoleDefinitionId, StartDateTime, EndDateTime | Format-Table
            """.trimIndent(),
            description = "Audits persistent (permanent) administrative role assignments to identify accounts that bypass PIM JIT elevation policies.",
            verificationCommand = "Get-MgRoleManagementDirectoryRoleAssignmentSchedule -All | Where-Object { \${'_'}.AssignmentType -eq 'Activated' }"
        ),
        Sc500SecurityScript(
            id = "sc500_script_azure_policy",
            title = "Assign Built-in Policy Denying Public Network Access to SQL",
            category = "Azure Governance & Compliance",
            commandOrPolicy = """
# Retrieve policy definition ID for denying public network access to Azure SQL
defId=${'$'}(az policy definition list --query "[?displayName=='Public network access on Azure SQL Database should be disabled'].id" -o tsv)

# Assign policy to subscription with Deny enforcement
az policy assignment create \
  --name "deny-sql-public-access" \
  --scope "/subscriptions/00000000-0000-0000-0000-000000000000" \
  --policy "${'$'}defId" \
  --enforcement-mode Default
            """.trimIndent(),
            description = "Assigns an Azure Policy that evaluates ARM deployment requests and denies creation of any Azure SQL Database with public network access enabled.",
            verificationCommand = "az policy assignment show --name \"deny-sql-public-access\" --scope \"/subscriptions/00000000-0000-0000-0000-000000000000\""
        )
    )

    val resourceLinks: List<com.example.data.model.RoadmapResourceLink> = listOf(
        com.example.data.model.RoadmapResourceLink(
            title = "Microsoft Learn: SC-500 Certification Study Guide",
            description = "Complete guide to Microsoft Azure Security Engineer Associate exam objectives, sandbox practice tests, and skills measured.",
            url = "https://learn.microsoft.com/en-us/credentials/certifications/resources/study-guides/sc-500",
            tag = "Official Study Guide",
            domainOrProvider = "learn.microsoft.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Microsoft Cloud Security Benchmark (MCSB) Blueprint",
            description = "Prescriptive security recommendations and cloud security best practices mapping to NIST, CIS, and PCI-DSS.",
            url = "https://learn.microsoft.com/en-us/security/benchmark/azure/introduction",
            tag = "Architecture Benchmark",
            domainOrProvider = "learn.microsoft.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Azure Key Vault & Managed HSM Security Architecture",
            description = "Detailed patterns for envelope encryption, Customer-Managed Keys (CMK), soft-delete, purge protection, and RBAC.",
            url = "https://learn.microsoft.com/en-us/azure/key-vault/general/best-practices",
            tag = "Cryptography & Secrets",
            domainOrProvider = "learn.microsoft.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Entra ID Privileged Identity Management (PIM) Masterclass",
            description = "Just-In-Time role activation, approval workflows, access reviews, and Break-Glass account management.",
            url = "https://learn.microsoft.com/en-us/entra/id-governance/privileged-identity-management/pim-configure",
            tag = "Identity Governance",
            domainOrProvider = "learn.microsoft.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "Azure Network Security Architecture (NSG, Firewall, Bastion)",
            description = "Zero Trust hub-and-spoke virtual network isolation, UDR forced tunneling, and DDoS protection planning.",
            url = "https://learn.microsoft.com/en-us/azure/architecture/guide/security/network-security",
            tag = "Network Security",
            domainOrProvider = "learn.microsoft.com"
        ),
        com.example.data.model.RoadmapResourceLink(
            title = "SC-500 Official Practice Assessment Quiz",
            description = "50 realistic scenario-based exam assessment questions with answer rationales and documentation cross-links.",
            url = "https://learn.microsoft.com/en-us/credentials/certifications/exams/sc-500/practice/assessment",
            tag = "Interactive Quiz",
            domainOrProvider = "learn.microsoft.com"
        )
    )
}
