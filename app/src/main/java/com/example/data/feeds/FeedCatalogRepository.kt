package com.example.data.feeds

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityMedium

enum class FeedDomainCategory(
    val displayName: String,
    val badgeColor: Color,
    val description: String
) {
    DEVSECOPS("DevSecOps", CyberEmerald, "Shift-left pipeline gates, SAST, DAST, SCA & IaC security"),
    DEVOPS("DevOps", Color(0xFF06B6D4), "CI/CD orchestration, Docker, Kubernetes & infrastructure automation"),
    BLUE_TEAM("Blue Team", Color(0xFF38BDF8), "SOC monitoring, SIEM, DFIR, threat hunting & detection rules"),
    RED_TEAM("Red Team", SeverityCritical, "Penetration testing, exploit development & adversary emulation"),
    PURPLE_TEAM("Purple Team", Color(0xFFA855F7), "Collaborative threat defense, Atomic tests & detection metrics"),
    GREY_HAT("Grey Hat / Bug Bounty", Color(0xFFF97316), "Responsible disclosure, web app penetration & bounty writeups"),
    BLACK_HAT("Black Hat / Threat Intel", Color(0xFFE11D48), "Dark web monitoring, ransomware cartels & APT actor tracking"),
    CYBER_NEWS("Cyber Intel & News", CyberCyan, "Global threat bulletins, breaking zero-days & breach advisories"),
    CLOUD_SECURITY("Cloud & K8s Sec", Color(0xFFEAB308), "AWS, Azure, GCP posture management, IAM & container security"),
    MALWARE_ANALYSIS("Malware & DFIR", Color(0xFFEC4899), "Reverse engineering, sandbox telemetry & memory forensics"),
    OSINT("OSINT & Recon", Color(0xFF14B8A6), "Open-source intelligence, domain monitoring & recon workflows"),
    GOV_CERTS("CERTs & Gov Advisories", Color(0xFF6366F1), "National security agencies, CISA alerts & emergency directives"),
    CRYPTOGRAPHY_PRIVACY("Cryptography & Privacy", Color(0xFF818CF8), "End-to-end encryption, TLS standards and privacy advocacy")
}

data class FeedCatalogItem(
    val id: String,
    val name: String,
    val feedUrl: String,
    val websiteUrl: String,
    val category: FeedDomainCategory,
    val description: String,
    val updateFrequency: String,
    val tag: String,
    val isMonitored: Boolean = false
)

object FeedCatalogRepository {

    val allFeeds: List<FeedCatalogItem> = listOf(
        // ==================== 1. DEVSECOPS (25 Feeds) ====================
        FeedCatalogItem("ds-01", "OWASP Foundation", "https://owasp.org/feed.xml", "https://owasp.org", FeedDomainCategory.DEVSECOPS, "Open Web Application Security Project top 10 guides and alerts", "Daily", "AppSec"),
        FeedCatalogItem("ds-02", "DevSecOps Weekly", "https://www.devsecops.org/feed.xml", "https://www.devsecops.org", FeedDomainCategory.DEVSECOPS, "Weekly curated articles on security automation and pipeline culture", "Weekly", "CI/CD"),
        FeedCatalogItem("ds-03", "Snyk Security Blog", "https://snyk.io/blog/feed/", "https://snyk.io/blog", FeedDomainCategory.DEVSECOPS, "Software composition analysis, container vulnerabilities and fixes", "Daily", "SCA"),
        FeedCatalogItem("ds-04", "GitHub Security Advisories", "https://github.blog/feed/", "https://github.blog/tag/security/", FeedDomainCategory.DEVSECOPS, "Advisories from GitHub Security Lab and Dependabot alerts", "Real-time", "Dependabot"),
        FeedCatalogItem("ds-05", "Checkmarx AppSec Intel", "https://checkmarx.com/feed/", "https://checkmarx.com", FeedDomainCategory.DEVSECOPS, "Static analysis and supply chain vulnerability research", "Daily", "SAST"),
        FeedCatalogItem("ds-06", "Aqua Security Blog", "https://blog.aquasec.com/rss.xml", "https://blog.aquasec.com", FeedDomainCategory.DEVSECOPS, "Container and Kubernetes cloud-native security insights", "Weekly", "Containers"),
        FeedCatalogItem("ds-07", "Semgrep Security Lab", "https://semgrep.dev/blog/rss.xml", "https://semgrep.dev/blog", FeedDomainCategory.DEVSECOPS, "Lightweight AST-based SAST rules and secret auditing", "Weekly", "Semgrep"),
        FeedCatalogItem("ds-08", "GitGuardian Blog", "https://blog.gitguardian.com/rss/", "https://blog.gitguardian.com", FeedDomainCategory.DEVSECOPS, "Secrets detection in source code and developer leak protection", "Weekly", "Secrets"),
        FeedCatalogItem("ds-09", "Bridgecrew by Prisma", "https://bridgecrew.io/blog/rss/", "https://bridgecrew.io", FeedDomainCategory.DEVSECOPS, "Infrastructure as Code scanning with Checkov rules", "Weekly", "IaC"),
        FeedCatalogItem("ds-10", "Chainguard Supply Chain", "https://www.chainguard.dev/unchained/rss.xml", "https://www.chainguard.dev", FeedDomainCategory.DEVSECOPS, "Software supply chain security, zero-cve base images and SLSA", "Daily", "SupplyChain"),
        FeedCatalogItem("ds-11", "Veracode Blog", "https://www.veracode.com/blog/feed", "https://www.veracode.com", FeedDomainCategory.DEVSECOPS, "Application security posture, enterprise DAST and flaw remediation", "Weekly", "DAST"),
        FeedCatalogItem("ds-12", "Contrast Security Labs", "https://www.contrastsecurity.com/security-quiet-corner/rss.xml", "https://www.contrastsecurity.com", FeedDomainCategory.DEVSECOPS, "Interactive application security testing (IAST) and runtime protection", "Weekly", "IAST"),
        FeedCatalogItem("ds-13", "StepSecurity Pipeline News", "https://blog.stepsecurity.io/rss", "https://blog.stepsecurity.io", FeedDomainCategory.DEVSECOPS, "Hardening GitHub Actions runners against malicious pull requests", "Bi-weekly", "GitHubActions"),
        FeedCatalogItem("ds-14", "Socket.dev Security Alert", "https://socket.dev/blog/rss", "https://socket.dev", FeedDomainCategory.DEVSECOPS, "Preventing npm, PyPI and RubyGem malicious dependency typosquats", "Daily", "npm"),
        FeedCatalogItem("ds-15", "Sonatype OSS Index", "https://blog.sonatype.com/rss.xml", "https://blog.sonatype.com", FeedDomainCategory.DEVSECOPS, "Maven and Open Source Software vulnerability ecosystem analysis", "Daily", "Maven"),
        FeedCatalogItem("ds-16", "OpenSSF News", "https://openssf.org/feed/", "https://openssf.org", FeedDomainCategory.DEVSECOPS, "Open Source Security Foundation tools like Scorecard and Allstar", "Weekly", "OpenSSF"),
        FeedCatalogItem("ds-17", "SLSA Framework Blog", "https://slsa.dev/blog/rss.xml", "https://slsa.dev", FeedDomainCategory.DEVSECOPS, "Supply chain Levels for Software Artifacts standards documentation", "Monthly", "SLSA"),
        FeedCatalogItem("ds-18", "Trivy Security Updates", "https://blog.aquasec.com/tag/trivy/rss.xml", "https://trivy.dev", FeedDomainCategory.DEVSECOPS, "Comprehensive scanner for OS packages, container images and IaC", "Weekly", "Trivy"),
        FeedCatalogItem("ds-19", "Truffle Security Lab", "https://trufflesecurity.com/blog/rss.xml", "https://trufflesecurity.com", FeedDomainCategory.DEVSECOPS, "High-entropy API key scanning and live secret verification", "Weekly", "Secrets"),
        FeedCatalogItem("ds-20", "Anchore Container Sec", "https://anchore.com/feed/", "https://anchore.com", FeedDomainCategory.DEVSECOPS, "Syft SBOM generator and Grype vulnerability scanner updates", "Weekly", "SBOM"),
        FeedCatalogItem("ds-21", "Praetorian AppSec", "https://www.praetorian.com/feed/", "https://www.praetorian.com", FeedDomainCategory.DEVSECOPS, "Offensive security engineers auditing source code architectures", "Monthly", "Architecture"),
        FeedCatalogItem("ds-22", "Kiuwan DevSecOps", "https://www.kiuwan.com/blog/feed/", "https://www.kiuwan.com", FeedDomainCategory.DEVSECOPS, "Code quality metrics and automated compliance governance", "Monthly", "Compliance"),
        FeedCatalogItem("ds-23", "Synopsys Software Integrity", "https://www.synopsys.com/blogs/software-security/feed/", "https://www.synopsys.com", FeedDomainCategory.DEVSECOPS, "Enterprise Coverity and Black Duck open source audits", "Weekly", "BlackDuck"),
        FeedCatalogItem("ds-24", "Legit Security Blog", "https://www.legitsecurity.com/blog/rss.xml", "https://www.legitsecurity.com", FeedDomainCategory.DEVSECOPS, "Software supply chain and developer pipeline posture protection", "Bi-weekly", "Pipelines"),
        FeedCatalogItem("ds-25", "Apiiro Cloud AppSec", "https://apiiro.com/feed/", "https://apiiro.com", FeedDomainCategory.DEVSECOPS, "Deep code-to-cloud application context and risk prioritization", "Monthly", "CodeToCloud"),

        // ==================== 2. DEVOPS & CLOUD-NATIVE (20 Feeds) ====================
        FeedCatalogItem("do-01", "Kubernetes Official Blog", "https://kubernetes.io/feed.xml", "https://kubernetes.io/blog/", FeedDomainCategory.DEVOPS, "Official announcements and security patches from the Kubernetes project", "Weekly", "K8s"),
        FeedCatalogItem("do-02", "Docker Security News", "https://www.docker.com/blog/feed/", "https://www.docker.com/blog/", FeedDomainCategory.DEVOPS, "Container daemon security, Docker Desktop and Scout updates", "Weekly", "Docker"),
        FeedCatalogItem("do-03", "Cloud Native Computing (CNCF)", "https://www.cncf.io/feed/", "https://www.cncf.io/blog/", FeedDomainCategory.DEVOPS, "Open-source cloud native ecosystem projects and graduating tools", "Daily", "CNCF"),
        FeedCatalogItem("do-04", "HashiCorp Releases", "https://www.hashicorp.com/blog/feed.xml", "https://www.hashicorp.com/blog", FeedDomainCategory.DEVOPS, "Terraform, Vault, Consul, and Nomad infrastructure tools", "Weekly", "Terraform"),
        FeedCatalogItem("do-05", "DevOps.com", "https://devops.com/feed/", "https://devops.com", FeedDomainCategory.DEVOPS, "Enterprise continuous integration, GitOps and site reliability news", "Daily", "CI/CD"),
        FeedCatalogItem("do-06", "Argo Project News", "https://blog.argoproj.io/feed", "https://argoproj.io", FeedDomainCategory.DEVOPS, "GitOps workflows using ArgoCD, Rollouts and Workflows in K8s", "Bi-weekly", "GitOps"),
        FeedCatalogItem("do-07", "Flux GitOps Blog", "https://fluxcd.io/blog/index.xml", "https://fluxcd.io", FeedDomainCategory.DEVOPS, "Open and extensible continuous delivery solution for Kubernetes", "Monthly", "Flux"),
        FeedCatalogItem("do-08", "GitLab Engineering", "https://about.gitlab.com/atom.xml", "https://about.gitlab.com/blog/", FeedDomainCategory.DEVOPS, "GitLab CI runners, deployment strategies and platform security", "Weekly", "GitLab"),
        FeedCatalogItem("do-09", "CircleCI Engineering", "https://circleci.com/blog/feed.xml", "https://circleci.com/blog/", FeedDomainCategory.DEVOPS, "High performance automation pipelines and testing best practices", "Weekly", "Automation"),
        FeedCatalogItem("do-10", "Red Hat OpenShift Blog", "https://cloud.redhat.com/blog/rss.xml", "https://cloud.redhat.com/blog/", FeedDomainCategory.DEVOPS, "Enterprise Kubernetes and Linux container architecture updates", "Daily", "OpenShift"),
        FeedCatalogItem("do-11", "Spinnaker Community", "https://spinnaker.io/feed.xml", "https://spinnaker.io", FeedDomainCategory.DEVOPS, "Multi-cloud continuous delivery platform developed by Netflix", "Monthly", "Deployment"),
        FeedCatalogItem("do-12", "Istio Service Mesh", "https://istio.io/latest/news/index.xml", "https://istio.io", FeedDomainCategory.DEVOPS, "Zero-trust service-to-service mTLS and traffic telemetry", "Monthly", "ServiceMesh"),
        FeedCatalogItem("do-13", "Cilium & eBPF News", "https://cilium.io/feed.xml", "https://cilium.io/blog/", FeedDomainCategory.DEVOPS, "eBPF-powered networking, observability and runtime security in K8s", "Weekly", "eBPF"),
        FeedCatalogItem("do-14", "OpenTofu Announcements", "https://opentofu.org/feed.xml", "https://opentofu.org", FeedDomainCategory.DEVOPS, "Open-source fork of Terraform governed by the Linux Foundation", "Monthly", "OpenTofu"),
        FeedCatalogItem("do-15", "Ansible Automation", "https://www.ansible.com/blog/rss.xml", "https://www.ansible.com/blog", FeedDomainCategory.DEVOPS, "Configuration management playbooks and infrastructure idempotency", "Weekly", "Ansible"),
        FeedCatalogItem("do-16", "Prometheus Monitoring", "https://prometheus.io/blog/feed.xml", "https://prometheus.io", FeedDomainCategory.DEVOPS, "Time-series telemetry, alertmanager thresholds and node metrics", "Monthly", "Monitoring"),
        FeedCatalogItem("do-17", "Grafana Labs Updates", "https://grafana.com/blog/index.xml", "https://grafana.com", FeedDomainCategory.DEVOPS, "Dashboards, Loki log aggregation and Tempo distributed tracing", "Weekly", "Observability"),
        FeedCatalogItem("do-18", "Helm Package Manager", "https://helm.sh/blog/index.xml", "https://helm.sh", FeedDomainCategory.DEVOPS, "Kubernetes package management and chart security practices", "Monthly", "Helm"),
        FeedCatalogItem("do-19", "Envoy Proxy Engineering", "https://blog.envoyproxy.io/feed", "https://blog.envoyproxy.io", FeedDomainCategory.DEVOPS, "High-performance edge and service proxy written in C++", "Monthly", "Envoy"),
        FeedCatalogItem("do-20", "Site Reliability Eng (SRE)", "https://sre.google/feed.xml", "https://sre.google", FeedDomainCategory.DEVOPS, "Google's architectural guidance for resilient production systems", "Monthly", "SRE"),

        // ==================== 3. BLUE TEAM, SOC & DFIR (25 Feeds) ====================
        FeedCatalogItem("bt-01", "SANS Internet Storm Center", "https://isc.sans.edu/rssfeed.xml", "https://isc.sans.edu", FeedDomainCategory.BLUE_TEAM, "Daily network telemetry, honeypot captures and threat handler logs", "Daily", "SANS"),
        FeedCatalogItem("bt-02", "DFIR Report", "https://thedfirreport.com/feed/", "https://thedfirreport.com", FeedDomainCategory.BLUE_TEAM, "Real-world intrusion case studies with timeline and detection artifacts", "Weekly", "DFIR"),
        FeedCatalogItem("bt-03", "SigmaHQ Detection Rules", "https://blog.sigmahq.io/feed", "https://sigmahq.io", FeedDomainCategory.BLUE_TEAM, "Generic signatures for log events and detection engineering", "Weekly", "Sigma"),
        FeedCatalogItem("bt-04", "Microsoft Security Threat Intel", "https://www.microsoft.com/en-us/security/blog/feed/", "https://www.microsoft.com/security/blog", FeedDomainCategory.BLUE_TEAM, "MSTIC adversary tracking, Defender telemetry and nation-state alerts", "Weekly", "MSTIC"),
        FeedCatalogItem("bt-05", "Splunk Security Research", "https://www.splunk.com/en_us/blog/security.rss", "https://www.splunk.com/blog/security", FeedDomainCategory.BLUE_TEAM, "SIEM hunting queries, correlation searches and ES detections", "Weekly", "Splunk"),
        FeedCatalogItem("bt-06", "Elastic Security Intelligence", "https://www.elastic.co/blog/feed", "https://www.elastic.co/blog", FeedDomainCategory.BLUE_TEAM, "SIEM detection rules, endpoint behavioral protections and KQL", "Weekly", "KQL"),
        FeedCatalogItem("bt-07", "SentinelOne Labs", "https://www.sentinelone.com/labs/feed/", "https://www.sentinelone.com/labs/", FeedDomainCategory.BLUE_TEAM, "EDR telemetry investigations, novel loader behavior and triage", "Weekly", "EDR"),
        FeedCatalogItem("bt-08", "CrowdStrike Threat Research", "https://www.crowdstrike.com/blog/feed/", "https://www.crowdstrike.com/blog/", FeedDomainCategory.BLUE_TEAM, "Falcon OverWatch threat hunting and adversary tradecraft reports", "Daily", "CrowdStrike"),
        FeedCatalogItem("bt-09", "Red Canary Threat Blog", "https://redcanary.com/blog/feed/", "https://redcanary.com/blog/", FeedDomainCategory.BLUE_TEAM, "MDR analysis, Windows Event Log hunting and atomic detection", "Weekly", "ThreatHunting"),
        FeedCatalogItem("bt-10", "Rapid7 Security Alerts", "https://www.rapid7.com/blog/feed/", "https://www.rapid7.com/blog", FeedDomainCategory.BLUE_TEAM, "Vulnerability disclosures, Metasploit modules and MDR insights", "Daily", "Rapid7"),
        FeedCatalogItem("bt-11", "Palo Alto Unit 42", "https://unit42.paloaltonetworks.com/feed/", "https://unit42.paloaltonetworks.com", FeedDomainCategory.BLUE_TEAM, "Global threat intelligence on ransomware, state actors and cloud leaks", "Daily", "Unit42"),
        FeedCatalogItem("bt-12", "Mandiant Threat Intelligence", "https://www.mandiant.com/resources/blog/rss.xml", "https://www.mandiant.com/resources/blog", FeedDomainCategory.BLUE_TEAM, "Frontline incident response discoveries and APT attribution", "Daily", "Mandiant"),
        FeedCatalogItem("bt-13", "Cisco Talos Intelligence", "https://blog.talosintelligence.com/rss/", "https://blog.talosintelligence.com", FeedDomainCategory.BLUE_TEAM, "Snort rules, ClamAV signatures and global telemetry analysis", "Daily", "Snort"),
        FeedCatalogItem("bt-14", "LogRhythm Security Blog", "https://logrhythm.com/blog/feed/", "https://logrhythm.com/blog", FeedDomainCategory.BLUE_TEAM, "Security Information and Event Management best practices", "Weekly", "SIEM"),
        FeedCatalogItem("bt-15", "Tenable Research", "https://www.tenable.com/blog/rss", "https://www.tenable.com/blog", FeedDomainCategory.BLUE_TEAM, "Nessus vulnerability plugins and enterprise exposure audits", "Daily", "Nessus"),
        FeedCatalogItem("bt-16", "Qualys Threat Research", "https://blog.qualys.com/feed", "https://blog.qualys.com", FeedDomainCategory.BLUE_TEAM, "Linux kernel vulnerabilities (Baron Samedit, Looney Tunables)", "Weekly", "Qualys"),
        FeedCatalogItem("bt-17", "Binary Defense Intel", "https://www.binarydefense.com/feed/", "https://www.binarydefense.com", FeedDomainCategory.BLUE_TEAM, "SOC analyst threat alerts and deceptive defense strategies", "Weekly", "Deception"),
        FeedCatalogItem("bt-18", "Cybereason Nocturnus", "https://www.cybereason.com/blog/rss.xml", "https://www.cybereason.com/blog", FeedDomainCategory.BLUE_TEAM, "Hunting complex APT campaigns across enterprise domains", "Monthly", "APT"),
        FeedCatalogItem("bt-19", "SpecterOps BloodHound", "https://posts.specterops.io/feed", "https://posts.specterops.io", FeedDomainCategory.BLUE_TEAM, "Active Directory attack paths and identity defense architecture", "Weekly", "ActiveDirectory"),
        FeedCatalogItem("bt-20", "Florian Roth (Neo23x0)", "https://www.nextron-systems.com/feed/", "https://www.nextron-systems.com", FeedDomainCategory.BLUE_TEAM, "YARA rules, THOR APT scanner and Sigma rule creator notes", "Weekly", "YARA"),
        FeedCatalogItem("bt-21", "Huntress Threat Intel", "https://www.huntress.com/blog/rss.xml", "https://www.huntress.com/blog", FeedDomainCategory.BLUE_TEAM, "SMB and MSP threat hunting, living-off-the-land persistence", "Daily", "Huntress"),
        FeedCatalogItem("bt-22", "Security Onion Solutions", "https://blog.securityonion.net/feeds/posts/default?alt=rss", "https://securityonionsolutions.com", FeedDomainCategory.BLUE_TEAM, "Network security monitoring, Zeek, Suricata and Wazuh integration", "Monthly", "Zeek"),
        FeedCatalogItem("bt-23", "Wazuh SIEM Updates", "https://wazuh.com/blog/feed/", "https://wazuh.com", FeedDomainCategory.BLUE_TEAM, "Open-source XDR and SIEM host agent security compliance", "Weekly", "Wazuh"),
        FeedCatalogItem("bt-24", "Exabeam Cyber Blog", "https://www.exabeam.com/feed/", "https://www.exabeam.com", FeedDomainCategory.BLUE_TEAM, "User and Entity Behavior Analytics (UEBA) threat detection", "Weekly", "UEBA"),
        FeedCatalogItem("bt-25", "Expel MDR Insights", "https://expel.com/blog/feed/", "https://expel.com/blog", FeedDomainCategory.BLUE_TEAM, "Transparent managed detection metrics and triage playbook", "Weekly", "MDR"),

        // ==================== 4. RED TEAM, EXPLOITS & OFFENSIVE (25 Feeds) ====================
        FeedCatalogItem("rt-01", "Exploit-DB Alerts", "https://www.exploit-db.com/rss.xml", "https://www.exploit-db.com", FeedDomainCategory.RED_TEAM, "Public exploits and vulnerable software proof-of-concepts", "Daily", "PoC"),
        FeedCatalogItem("rt-02", "Offensive Security (OffSec)", "https://www.offsec.com/blog/feed/", "https://www.offsec.com", FeedDomainCategory.RED_TEAM, "Kali Linux updates, OSCP insights and penetration techniques", "Weekly", "Kali"),
        FeedCatalogItem("rt-03", "PortSwigger Research", "https://portswigger.net/research/rss", "https://portswigger.net/research", FeedDomainCategory.RED_TEAM, "Burp Suite creators publishing groundbreaking web exploit vectors", "Monthly", "BurpSuite"),
        FeedCatalogItem("rt-04", "TrustedSec Blog", "https://trustedsec.com/blog/rss.xml", "https://trustedsec.com", FeedDomainCategory.RED_TEAM, "Physical security, red team tradecraft and adversary emulation", "Weekly", "Tradecraft"),
        FeedCatalogItem("rt-05", "Black Hills InfoSec", "https://www.blackhillsinfosec.com/feed/", "https://www.blackhillsinfosec.com", FeedDomainCategory.RED_TEAM, "Penetration testing webcasts, bypass scripts and payloads", "Daily", "BHIS"),
        FeedCatalogItem("rt-06", "Outflank Red Team", "https://outflank.nl/blog/feed/", "https://outflank.nl", FeedDomainCategory.RED_TEAM, "Advanced tradecraft: Windows internals, syscalls and cobalt strike", "Monthly", "Syscalls"),
        FeedCatalogItem("rt-07", "MDSec ActiveBreach", "https://www.mdsec.co.uk/feed/", "https://www.mdsec.co.uk", FeedDomainCategory.RED_TEAM, "Cutting-edge offensive research, EDR unhooking and AMSI evasion", "Monthly", "Evasion"),
        FeedCatalogItem("rt-08", "Cobalt Strike Blog", "https://www.cobaltstrike.com/feed/", "https://www.cobaltstrike.com", FeedDomainCategory.RED_TEAM, "Malleable C2 profiles, beacon payload development and updates", "Monthly", "C2"),
        FeedCatalogItem("rt-09", "Zero Day Initiative (ZDI)", "https://www.zerodayinitiative.com/blog?format=rss", "https://www.zerodayinitiative.com", FeedDomainCategory.RED_TEAM, "Pwn2Own competition writeups and zero-day advisory streams", "Daily", "Pwn2Own"),
        FeedCatalogItem("rt-10", "XPN Red Team Ramblings", "https://blog.xpn.sec/rss/", "https://blog.xpn.sec", FeedDomainCategory.RED_TEAM, "In-depth exploit development on Windows kernel and macOS sandbox", "Monthly", "Kernel"),
        FeedCatalogItem("rt-11", "Rastamouse Red Team", "https://rastamouse.me/feed.xml", "https://rastamouse.me", FeedDomainCategory.RED_TEAM, "Offensive development, C2 frameworks and Active Directory exploitation", "Monthly", "C#"),
        FeedCatalogItem("rt-12", "0x00sec - The Hacker Citadel", "https://0x00sec.org/posts.rss", "https://0x00sec.org", FeedDomainCategory.RED_TEAM, "Community research into malware mechanics, shellcoding and binary RE", "Weekly", "Community"),
        FeedCatalogItem("rt-13", "FuzzySecurity Research", "https://fuzzysecurity.com/rss.xml", "https://fuzzysecurity.com", FeedDomainCategory.RED_TEAM, "PowerShell offensive weaponization and local privilege escalation", "Monthly", "PrivEsc"),
        FeedCatalogItem("rt-14", "Hack The Box News", "https://www.hackthebox.com/blog/rss", "https://www.hackthebox.com", FeedDomainCategory.RED_TEAM, "Cybersecurity training lab challenges and machine writeup methodologies", "Weekly", "HTB"),
        FeedCatalogItem("rt-15", "Packet Storm Security", "https://rss.packetstormsecurity.com/news/", "https://packetstormsecurity.com", FeedDomainCategory.RED_TEAM, "Repository of hacking exploits, security advisories and exploits", "Daily", "Exploits"),
        FeedCatalogItem("rt-16", "Sliver C2 Releases", "https://github.com/BishopFox/sliver/releases.atom", "https://github.com/BishopFox/sliver", FeedDomainCategory.RED_TEAM, "Open-source cross-platform adversary emulation framework by Bishop Fox", "Monthly", "Sliver"),
        FeedCatalogItem("rt-17", "Metasploit GitHub Feed", "https://github.com/rapid7/metasploit-framework/releases.atom", "https://metasploit.com", FeedDomainCategory.RED_TEAM, "World's most used penetration testing framework releases", "Weekly", "Metasploit"),
        FeedCatalogItem("rt-18", "Bishop Fox Research", "https://bishopfox.com/blog/rss.xml", "https://bishopfox.com", FeedDomainCategory.RED_TEAM, "Tool releases, offensive tradecraft and hardware hacking", "Weekly", "BishopFox"),
        FeedCatalogItem("rt-19", "NCC Group Research", "https://research.nccgroup.com/feed/", "https://research.nccgroup.com", FeedDomainCategory.RED_TEAM, "Cryptographic audits, low-level reverse engineering and browser exploits", "Weekly", "Research"),
        FeedCatalogItem("rt-20", "Adam Chester (xpn)", "https://blog.xpn.sec/rss/", "https://blog.xpn.sec", FeedDomainCategory.RED_TEAM, "Techniques for in-memory code execution and process injection", "Monthly", "Injection"),
        FeedCatalogItem("rt-21", "Red Siege InfoSec", "https://redsiege.com/feed/", "https://redsiege.com", FeedDomainCategory.RED_TEAM, "Tactical offensive guidance, lateral movement and phishing", "Monthly", "PenTesting"),
        FeedCatalogItem("rt-22", "HarmJ0y (Will Schroeder)", "https://harmj0y.net/feed.xml", "https://harmj0y.net", FeedDomainCategory.RED_TEAM, "Creator of BloodHound, PowerView and Rubeus Kerberos exploitation", "Monthly", "Kerberos"),
        FeedCatalogItem("rt-23", "NetSPI Offensive Blog", "https://www.netspi.com/blog/feed/", "https://www.netspi.com", FeedDomainCategory.RED_TEAM, "Attack surface management, cloud penetration testing and breach sim", "Weekly", "NetSPI"),
        FeedCatalogItem("rt-24", "Scarsz Exploit Feed", "https://cxsecurity.com/wlb/rss/", "https://cxsecurity.com", FeedDomainCategory.RED_TEAM, "World Laboratory of Bug Tracking and public 0day alerts", "Daily", "0day"),
        FeedCatalogItem("rt-25", "Project Zero (Google)", "https://googleprojectzero.blogspot.com/feeds/posts/default?alt=rss", "https://googleprojectzero.blogspot.com", FeedDomainCategory.RED_TEAM, "Elite security researchers hunting zero-day vulnerabilities in common software", "Bi-weekly", "ZeroDay"),

        // ==================== 5. PURPLE TEAM & EMULATION (15 Feeds) ====================
        FeedCatalogItem("pt-01", "Atomic Red Team News", "https://github.com/redcanaryco/atomic-red-team/releases.atom", "https://atomicredteam.io", FeedDomainCategory.PURPLE_TEAM, "Simple, open-source library of tests mapped to MITRE ATT&CK", "Weekly", "Atomic"),
        FeedCatalogItem("pt-02", "MITRE ATT&CK Updates", "https://attack.mitre.org/feed.xml", "https://attack.mitre.org", FeedDomainCategory.PURPLE_TEAM, "Official updates to enterprise, mobile and cloud ATT&CK matrices", "Monthly", "MITRE"),
        FeedCatalogItem("pt-03", "MITRE D3FEND News", "https://d3fend.mitre.org/feed.xml", "https://d3fend.mitre.org", FeedDomainCategory.PURPLE_TEAM, "Knowledge graph of cybersecurity defensive countermeasures", "Monthly", "D3FEND"),
        FeedCatalogItem("pt-04", "SCYTHE Purple Team Blog", "https://scythe.io/blog/rss.xml", "https://scythe.io/blog", FeedDomainCategory.PURPLE_TEAM, "Adversary emulation plans and purple teaming methodologies", "Weekly", "SCYTHE"),
        FeedCatalogItem("pt-05", "Prelude Security Intel", "https://prelude.org/feed.xml", "https://prelude.org", FeedDomainCategory.PURPLE_TEAM, "Autonomous red-teaming and continuous defense verification", "Monthly", "Prelude"),
        FeedCatalogItem("pt-06", "AttackIQ Research", "https://www.attackiq.com/blog/feed/", "https://www.attackiq.com", FeedDomainCategory.PURPLE_TEAM, "Breach and attack simulation (BAS) mapped to defensive validation", "Weekly", "BAS"),
        FeedCatalogItem("pt-07", "CALDERA by MITRE", "https://github.com/mitre/caldera/releases.atom", "https://caldera.mitre.org", FeedDomainCategory.PURPLE_TEAM, "Automated adversary emulation system built on the ATT&CK framework", "Monthly", "Caldera"),
        FeedCatalogItem("pt-08", "Tidal Cyber Intelligence", "https://www.tidalcyber.com/blog/rss.xml", "https://www.tidalcyber.com", FeedDomainCategory.PURPLE_TEAM, "Threat-informed defense platforms mapping adversary TTPs", "Weekly", "Tidal"),
        FeedCatalogItem("pt-09", "Stratus Red Team (DataDog)", "https://github.com/DataDog/stratus-red-team/releases.atom", "https://stratus-red-team.cloud", FeedDomainCategory.PURPLE_TEAM, "Granular, actionable cloud adversary emulation for AWS/Azure/GCP", "Monthly", "CloudEmulation"),
        FeedCatalogItem("pt-10", "VECTR Community", "https://github.com/SecurityRiskAdvisors/VECTR/releases.atom", "https://vectr.io", FeedDomainCategory.PURPLE_TEAM, "Purple teaming assessment scoring and defense visibility metrics", "Monthly", "VECTR"),
        FeedCatalogItem("pt-11", "DeTT&CT Tool Updates", "https://github.com/rabobank-cdc/DeTTeCT/releases.atom", "https://github.com/rabobank-cdc/DeTTeCT", FeedDomainCategory.PURPLE_TEAM, "Detect Tactics, Techniques & Combat Threats by Rabobank CDC", "Monthly", "DeTT&CT"),
        FeedCatalogItem("pt-12", "Cymulate BAS Intel", "https://cymulate.com/blog/feed/", "https://cymulate.com", FeedDomainCategory.PURPLE_TEAM, "Automated validation of network, email and endpoint defenses", "Weekly", "Cymulate"),
        FeedCatalogItem("pt-13", "SafeBreach Labs", "https://safebreach.com/blog/feed/", "https://safebreach.com", FeedDomainCategory.PURPLE_TEAM, "Simulating malware payloads without causing business disruption", "Weekly", "SafeBreach"),
        FeedCatalogItem("pt-14", "PlexTrac Purple Portal", "https://plextrac.com/feed/", "https://plextrac.com", FeedDomainCategory.PURPLE_TEAM, "Collaborative reporting between offensive hackers and blue defenders", "Monthly", "Collaboration"),
        FeedCatalogItem("pt-15", "Picasso Cyber Emulation", "https://blog.picussecurity.com/rss.xml", "https://picussecurity.com", FeedDomainCategory.PURPLE_TEAM, "Complete cyber defense validation and security controls scoring", "Weekly", "Validation"),

        // ==================== 6. GREY HAT, BUG BOUNTY & WEB APP SEC (20 Feeds) ====================
        FeedCatalogItem("gh-01", "HackerOne Disclosure Blog", "https://www.hackerone.com/blog/rss.xml", "https://www.hackerone.com", FeedDomainCategory.GREY_HAT, "Responsible disclosure reports, top bounties and hacker community trends", "Weekly", "HackerOne"),
        FeedCatalogItem("gh-02", "Bugcrowd Vulnerability Reports", "https://www.bugcrowd.com/feed/", "https://www.bugcrowd.com", FeedDomainCategory.GREY_HAT, "Crowdsourced cybersecurity bug hunting guides and leaderboards", "Weekly", "Bugcrowd"),
        FeedCatalogItem("gh-03", "NahamSec Security Hub", "https://nahamsec.com/feed/", "https://nahamsec.com", FeedDomainCategory.GREY_HAT, "Bug bounty recon methodologies and educational web security streams", "Weekly", "Recon"),
        FeedCatalogItem("gh-04", "Intigriti Bug Bounty Blog", "https://blog.intigriti.com/feed/", "https://blog.intigriti.com", FeedDomainCategory.GREY_HAT, "European crowdsourced bounty platform publishing monthly 1337 challenges", "Weekly", "Intigriti"),
        FeedCatalogItem("gh-05", "Assetnote Security Research", "https://assetnote.io/resources/research/rss.xml", "https://assetnote.io", FeedDomainCategory.GREY_HAT, "Deep dives into critical remote code execution on Fortune 500 apps", "Monthly", "Assetnote"),
        FeedCatalogItem("gh-06", "ProjectDiscovery News", "https://blog.projectdiscovery.io/rss/", "https://projectdiscovery.io", FeedDomainCategory.GREY_HAT, "Nuclei templates, Subfinder, HTTPX and Katana recon updates", "Weekly", "Nuclei"),
        FeedCatalogItem("gh-07", "Detectify Labs", "https://blog.detectify.com/feed/", "https://blog.detectify.com", FeedDomainCategory.GREY_HAT, "Crowdsourced vulnerability discovery findings by white hat hackers", "Bi-weekly", "Detectify"),
        FeedCatalogItem("gh-08", "PentesterLand Writeups", "https://pentester.land/feed.xml", "https://pentester.land", FeedDomainCategory.GREY_HAT, "Weekly curation of all public bug bounty writeups and techniques", "Weekly", "Writeups"),
        FeedCatalogItem("gh-09", "YesWeHack Vulnerability Blog", "https://blog.yeswehack.com/feed/", "https://blog.yeswehack.com", FeedDomainCategory.GREY_HAT, "Global ethical hacking community writeups and coordinated releases", "Weekly", "YesWeHack"),
        FeedCatalogItem("gh-10", "AppSec Ezine", "https://appsecezine.com/feed.xml", "https://appsecezine.com", FeedDomainCategory.GREY_HAT, "Curated security newsletter on modern web architecture and flaws", "Weekly", "Newsletter"),
        FeedCatalogItem("gh-11", "SecOps Weekly Bug Bounty", "https://secopsweekly.com/feed.xml", "https://secopsweekly.com", FeedDomainCategory.GREY_HAT, "SSRF, IDOR, SQLi and GraphQL vulnerability case studies", "Weekly", "IDOR"),
        FeedCatalogItem("gh-12", "HackenProof Bounty News", "https://hackenproof.com/feed", "https://hackenproof.com", FeedDomainCategory.GREY_HAT, "Web3 and traditional application bug bounty program advisories", "Weekly", "Web3"),
        FeedCatalogItem("gh-13", "Synack Red Team (SRT)", "https://synack.com/blog/feed/", "https://synack.com", FeedDomainCategory.GREY_HAT, "Vetted ethical hackers performing crowd-sourced security assessments", "Weekly", "Synack"),
        FeedCatalogItem("gh-14", "Frans Rosén Security", "https://blog.detectify.com/author/frans-rosen/feed/", "https://twitter.com/frabor", FeedDomainCategory.GREY_HAT, "Renowned bounty hunter revealing advanced DOM XSS and DNS flaws", "Monthly", "XSS"),
        FeedCatalogItem("gh-15", "James Kettle (albinowax)", "https://portswigger.net/research/james-kettle/rss", "https://albinowax.me", FeedDomainCategory.GREY_HAT, "HTTP request smuggling and web cache poisoning discovery author", "Monthly", "Smuggling"),
        FeedCatalogItem("gh-16", "Cobalt.io Pentest Blog", "https://www.cobalt.io/blog/rss.xml", "https://www.cobalt.io", FeedDomainCategory.GREY_HAT, "Pentest as a Service (PaaS) and rapid remediation benchmarks", "Weekly", "PaaS"),
        FeedCatalogItem("gh-17", "Shubs (Subdomain Recon)", "https://blog.shubs.io/rss/", "https://shubs.io", FeedDomainCategory.GREY_HAT, "Large-scale internet scanning and external attack surface discovery", "Monthly", "AssetDiscovery"),
        FeedCatalogItem("gh-18", "Security Idiots Bounty Tips", "https://securityidiots.com/feed.xml", "https://securityidiots.com", FeedDomainCategory.GREY_HAT, "Practical step-by-step guides for finding security vulnerabilities", "Monthly", "Tutorials"),
        FeedCatalogItem("gh-19", "Immunefi Web3 Bounties", "https://medium.com/feed/immunefi", "https://immunefi.com", FeedDomainCategory.GREY_HAT, "Leading bug bounty platform for smart contracts and blockchain systems", "Daily", "DeFi"),
        FeedCatalogItem("gh-20", "OpenBugBounty Advisories", "https://www.openbugbounty.org/rss/all/", "https://www.openbugbounty.org", FeedDomainCategory.GREY_HAT, "Non-profit open coordinated vulnerability disclosure platform", "Daily", "Disclosure"),

        // ==================== 7. BLACK HAT & THREAT INTEL (20 Feeds) ====================
        FeedCatalogItem("bh-01", "BleepingComputer Threat Intel", "https://www.bleepingcomputer.com/feed/", "https://www.bleepingcomputer.com", FeedDomainCategory.BLACK_HAT, "Breaking ransomware attacks, cyber extortion and threat actor updates", "Real-time", "Ransomware"),
        FeedCatalogItem("bh-02", "Krebs on Security", "https://krebsonsecurity.com/feed/", "https://krebsonsecurity.com", FeedDomainCategory.BLACK_HAT, "Investigative cybercrime journalism on underground forums and botnets", "Weekly", "Cybercrime"),
        FeedCatalogItem("bh-03", "Recorded Future News", "https://therecord.media/feed", "https://therecord.media", FeedDomainCategory.BLACK_HAT, "State-sponsored espionage, critical infrastructure attacks and leaks", "Daily", "TheRecord"),
        FeedCatalogItem("bh-04", "vx-underground Bulletins", "https://vx-underground.org/feed.xml", "https://vx-underground.org", FeedDomainCategory.BLACK_HAT, "Collection of malware source code, cartel communiques and papers", "Daily", "Underground"),
        FeedCatalogItem("bh-05", "Ransomware.live Feeds", "https://www.ransomware.live/rss.xml", "https://www.ransomware.live", FeedDomainCategory.BLACK_HAT, "Live monitoring of active ransomware dark web leak sites and victims", "Real-time", "DarkWeb"),
        FeedCatalogItem("bh-06", "Dark Reading News", "https://www.darkreading.com/rss.xml", "https://www.darkreading.com", FeedDomainCategory.BLACK_HAT, "Enterprise attacks, APT group indicators and breach disclosure data", "Daily", "Enterprise"),
        FeedCatalogItem("bh-07", "Malwarebytes Threat Labs", "https://www.malwarebytes.com/blog/feed/index.xml", "https://www.malwarebytes.com/blog", FeedDomainCategory.BLACK_HAT, "Info-stealers, trojans, ad fraud and banking malware analysis", "Weekly", "Malware"),
        FeedCatalogItem("bh-08", "Flashpoint Cyber Threat Intel", "https://flashpoint.io/blog/feed/", "https://flashpoint.io", FeedDomainCategory.BLACK_HAT, "Illicit dark web forums, telegram channel monitoring and cartels", "Weekly", "DarkWebIntel"),
        FeedCatalogItem("bh-09", "Kela Cyber Threat Intelligence", "https://ke-la.com/feed/", "https://ke-la.com", FeedDomainCategory.BLACK_HAT, "Automated darknet telemetry, initial access brokers and stolen credentials", "Weekly", "AccessBrokers"),
        FeedCatalogItem("bh-10", "Intel 471 Threat Research", "https://intel471.com/blog/feed", "https://intel471.com", FeedDomainCategory.BLACK_HAT, "Cyber underground intelligence tracking Eastern European syndicates", "Weekly", "Syndicates"),
        FeedCatalogItem("bh-11", "SecurityWeek Threat Intel", "https://www.securityweek.com/feed/", "https://www.securityweek.com", FeedDomainCategory.BLACK_HAT, "ICS/SCADA vulnerabilities, global espionage and defense technology", "Daily", "ICS"),
        FeedCatalogItem("bh-12", "Threatpost Archive", "https://threatpost.com/feed/", "https://threatpost.com", FeedDomainCategory.BLACK_HAT, "First-hand investigative reports on malware strains and data heists", "Daily", "Breaches"),
        FeedCatalogItem("bh-13", "Cyble Cyber Crime Research", "https://cyble.com/blog/feed/", "https://cyble.com", FeedDomainCategory.BLACK_HAT, "Dark web sales of stolen corporate databases and PII records", "Daily", "DataLeaks"),
        FeedCatalogItem("bh-14", "RedSense Threat Briefings", "https://redsense.io/feed.xml", "https://redsense.io", FeedDomainCategory.BLACK_HAT, "Adversary tradecraft and high-severity extortion negotiations", "Weekly", "Extortion"),
        FeedCatalogItem("bh-15", "Group-IB Threat Intel", "https://www.group-ib.com/blog/rss.xml", "https://www.group-ib.com", FeedDomainCategory.BLACK_HAT, "Global investigations into high-tech cybercrime and financial fraud", "Weekly", "Fraud"),
        FeedCatalogItem("bh-16", "Curated Threat Intel by CTI", "https://cti-league.com/feed/", "https://cti-league.com", FeedDomainCategory.BLACK_HAT, "Emergency medical and critical infrastructure cyber threat coalition", "Monthly", "CTILeague"),
        FeedCatalogItem("bh-17", "Censys Threat Research", "https://censys.com/feed/", "https://censys.com/blog", FeedDomainCategory.BLACK_HAT, "Tracking command and control infrastructure across public IP space", "Weekly", "C2Tracking"),
        FeedCatalogItem("bh-18", "Shodan Security Bulletins", "https://blog.shodan.io/rss/", "https://blog.shodan.io", FeedDomainCategory.BLACK_HAT, "Internet-wide surveys of exposed SCADA, industrial devices and webcams", "Monthly", "Exposures"),
        FeedCatalogItem("bh-19", "GreyNoise Threat Observations", "https://www.greynoise.io/blog/rss.xml", "https://greynoise.io", FeedDomainCategory.BLACK_HAT, "Separating internet background scan noise from targeted adversary attacks", "Weekly", "NoiseFilter"),
        FeedCatalogItem("bh-20", "Shadowserver Foundation", "https://www.shadowserver.org/feed/", "https://www.shadowserver.org", FeedDomainCategory.BLACK_HAT, "Global daily reporting on infected botnet victims and server telemetry", "Daily", "Botnets"),

        // ==================== 8. CYBER INTELLIGENCE & NEWS (20 Feeds) ====================
        FeedCatalogItem("cn-01", "The Hacker News", "https://feeds.feedburner.com/TheHackersNews", "https://thehackernews.com", FeedDomainCategory.CYBER_NEWS, "World's most read cybersecurity news platform with real-time updates", "Real-time", "Breaking"),
        FeedCatalogItem("cn-02", "Security Magazine", "https://www.securitymagazine.com/rss", "https://www.securitymagazine.com", FeedDomainCategory.CYBER_NEWS, "Strategic leadership, CISO guidelines and risk management", "Daily", "CISO"),
        FeedCatalogItem("cn-03", "InfoSecurity Magazine", "https://www.infosecurity-magazine.com/rss/news/", "https://www.infosecurity-magazine.com", FeedDomainCategory.CYBER_NEWS, "Information security trends, European privacy and data breaches", "Daily", "GDPR"),
        FeedCatalogItem("cn-04", "CSO Online", "https://www.csoonline.com/feed/", "https://www.csoonline.com", FeedDomainCategory.CYBER_NEWS, "Executive analysis on cybersecurity strategy, budgets and governance", "Daily", "Enterprise"),
        FeedCatalogItem("cn-05", "Help Net Security", "https://www.helpnetsecurity.com/feed/", "https://www.helpnetsecurity.com", FeedDomainCategory.CYBER_NEWS, "Industry news, executive interviews and product release overviews", "Daily", "Industry"),
        FeedCatalogItem("cn-06", "Darknet News", "https://www.darknet.org.uk/feed/", "https://www.darknet.org.uk", FeedDomainCategory.CYBER_NEWS, "Ethical hacking tools, privacy tutorials and security software", "Weekly", "Tools"),
        FeedCatalogItem("cn-07", "Security Boulevard", "https://securityboulevard.com/feed/", "https://securityboulevard.com", FeedDomainCategory.CYBER_NEWS, "Aggregated perspectives from hundreds of independent security bloggers", "Daily", "BloggerNet"),
        FeedCatalogItem("cn-08", "Cyberscoop", "https://cyberscoop.com/feed/", "https://cyberscoop.com", FeedDomainCategory.CYBER_NEWS, "US Federal cybersecurity policy, geopolitical state hacking news", "Daily", "Policy"),
        FeedCatalogItem("cn-09", "SC Media", "https://www.scmagazine.com/feed", "https://www.scmagazine.com", FeedDomainCategory.CYBER_NEWS, "In-depth reporting for cybersecurity decision-makers and technologists", "Daily", "SCMedia"),
        FeedCatalogItem("cn-10", "ZDNet Zero Day", "https://www.zdnet.com/topic/security/rss.xml", "https://www.zdnet.com/topic/security/", FeedDomainCategory.CYBER_NEWS, "Enterprise tech security reviews, malware advisories and IT news", "Daily", "ZDNet"),
        FeedCatalogItem("cn-11", "Ars Technica Security", "https://feeds.arstechnica.com/arstechnica/security", "https://arstechnica.com/security/", FeedDomainCategory.CYBER_NEWS, "Technical journalism examining cryptographic weaknesses and attacks", "Daily", "ArsTechnica"),
        FeedCatalogItem("cn-12", "Wired Security News", "https://www.wired.com/feed/category/security/latest/rss", "https://www.wired.com/category/security/", FeedDomainCategory.CYBER_NEWS, "Narrative investigative features on cyber conflicts and espionage", "Daily", "Wired"),
        FeedCatalogItem("cn-13", "The Register - Security", "https://www.theregister.com/security/headlines.atom", "https://www.theregister.com/security/", FeedDomainCategory.CYBER_NEWS, "Candid British tech publication covering security disasters and fixes", "Daily", "TheRegister"),
        FeedCatalogItem("cn-14", "SiliconANGLE Security", "https://siliconangle.com/category/security/feed/", "https://siliconangle.com/category/security/", FeedDomainCategory.CYBER_NEWS, "Cloud security market funding, acquisitions and enterprise innovations", "Daily", "Fintech"),
        FeedCatalogItem("cn-15", "VentureBeat Security", "https://venturebeat.com/category/security/feed/", "https://venturebeat.com/category/security/", FeedDomainCategory.CYBER_NEWS, "AI in cybersecurity, generative defense and startup developments", "Daily", "AI-Sec"),
        FeedCatalogItem("cn-16", "BetaNews Security", "https://betanews.com/category/security/feed/", "https://betanews.com", FeedDomainCategory.CYBER_NEWS, "Enterprise endpoint solutions, compliance news and data security", "Daily", "BetaNews"),
        FeedCatalogItem("cn-17", "Graham Cluley Blog", "https://grahamcluley.com/feed/", "https://grahamcluley.com", FeedDomainCategory.CYBER_NEWS, "Veteran computer security analyst blogging on scams and identity theft", "Daily", "Scams"),
        FeedCatalogItem("cn-18", "Schneier on Security", "https://www.schneier.com/feed/atom/", "https://www.schneier.com", FeedDomainCategory.CYBER_NEWS, "Bruce Schneier's iconic security and cryptography policy blog", "Weekly", "Philosophy"),
        FeedCatalogItem("cn-19", "Troy Hunt (Have I Been Pwned)", "https://www.troyhunt.com/rss/", "https://www.troyhunt.com", FeedDomainCategory.CYBER_NEWS, "Creator of HIBP documenting data breach dumps and credential hygiene", "Weekly", "HIBP"),
        FeedCatalogItem("cn-20", "Troy Hunt Data Breaches", "https://feeds.feedburner.com/HaveIBeenPwnedLatestBreaches", "https://haveibeenpwned.com", FeedDomainCategory.CYBER_NEWS, "Immediate RSS notifications whenever newly breached databases are verified", "Real-time", "PwnedDB"),

        // ==================== 9. CLOUD SECURITY & KUBERNETES (15 Feeds) ====================
        FeedCatalogItem("cs-01", "AWS Security Bulletins", "https://aws.amazon.com/security/security-bulletins/rss/", "https://aws.amazon.com/security", FeedDomainCategory.CLOUD_SECURITY, "Official vulnerability alerts and IAM guidance for Amazon Web Services", "Weekly", "AWS"),
        FeedCatalogItem("cs-02", "Google Cloud Security News", "https://cloud.google.com/blog/topics/security/rss/", "https://cloud.google.com/blog/topics/security", FeedDomainCategory.CLOUD_SECURITY, "Zero trust, BeyondCorp and Google Cloud platform defenses", "Weekly", "GCP"),
        FeedCatalogItem("cs-03", "Microsoft Azure Security", "https://azure.microsoft.com/en-us/blog/topics/security/feed/", "https://azure.microsoft.com", FeedDomainCategory.CLOUD_SECURITY, "Defender for Cloud, Sentinel integrations and Azure policy rules", "Weekly", "Azure"),
        FeedCatalogItem("cs-04", "Wiz Security Blog", "https://www.wiz.io/blog/rss.xml", "https://www.wiz.io/blog", FeedDomainCategory.CLOUD_SECURITY, "Groundbreaking research on cross-tenant cloud isolation escapes", "Weekly", "CSPM"),
        FeedCatalogItem("cs-05", "Orca Security Intel", "https://orca.security/resources/blog/feed/", "https://orca.security", FeedDomainCategory.CLOUD_SECURITY, "SideScanning technology detecting cloud assets and misconfigurations", "Weekly", "SideScanning"),
        FeedCatalogItem("cs-06", "Sysdig Security Research", "https://sysdig.com/blog/feed/", "https://sysdig.com/blog", FeedDomainCategory.CLOUD_SECURITY, "eBPF Falco runtime rules, cloud attack matrix and cryptomining", "Weekly", "Falco"),
        FeedCatalogItem("cs-07", "Prisma Cloud by Palo Alto", "https://www.paloaltonetworks.com/blog/prisma-cloud/feed/", "https://www.paloaltonetworks.com/prisma/cloud", FeedDomainCategory.CLOUD_SECURITY, "Comprehensive CNAPP, microsegmentation and IAM security", "Weekly", "CNAPP"),
        FeedCatalogItem("cs-08", "Lacework Labs (Fortinet)", "https://www.lacework.com/feed/", "https://www.lacework.com", FeedDomainCategory.CLOUD_SECURITY, "Cloud behavioral anomaly analysis and adversary cloud infrastructure", "Bi-weekly", "Anomalies"),
        FeedCatalogItem("cs-09", "Datadog Cloud Sec", "https://www.datadoghq.com/blog/rss.xml", "https://www.datadoghq.com/blog", FeedDomainCategory.CLOUD_SECURITY, "Cloud SIEM, observability metrics and workload protection rules", "Weekly", "Datadog"),
        FeedCatalogItem("cs-10", "Prowler Cloud Security", "https://github.com/prowler-cloud/prowler/releases.atom", "https://prowler.com", FeedDomainCategory.CLOUD_SECURITY, "Open source AWS, Azure and GCP security assessment tool releases", "Weekly", "Prowler"),
        FeedCatalogItem("cs-11", "ScoutSuite Releases", "https://github.com/nccgroup/ScoutSuite/releases.atom", "https://github.com/nccgroup/ScoutSuite", FeedDomainCategory.CLOUD_SECURITY, "Multi-cloud security auditing tool by NCC Group", "Monthly", "ScoutSuite"),
        FeedCatalogItem("cs-12", "Falco Project News", "https://falco.org/blog/index.xml", "https://falco.org", FeedDomainCategory.CLOUD_SECURITY, "Cloud native runtime security engine detecting unexpected behavior", "Monthly", "Runtime"),
        FeedCatalogItem("cs-13", "Kyverno Policy Engine", "https://kyverno.io/blog/index.xml", "https://kyverno.io", FeedDomainCategory.CLOUD_SECURITY, "Kubernetes native policy management for validation, mutation and generate", "Monthly", "Kyverno"),
        FeedCatalogItem("cs-14", "Open Policy Agent (OPA)", "https://blog.openpolicyagent.org/feed", "https://openpolicyagent.org", FeedDomainCategory.CLOUD_SECURITY, "Unified policy enforcement across cloud stacks using Rego language", "Monthly", "Rego"),
        FeedCatalogItem("cs-15", "Teleport Zero Trust", "https://goteleport.com/blog/feed.xml", "https://goteleport.com", FeedDomainCategory.CLOUD_SECURITY, "Identity-aware multi-protocol access proxy for SSH, K8s, databases and apps", "Bi-weekly", "ZeroTrust"),

        // ==================== 10. MALWARE & REVERSE ENGINEERING (12 Feeds) ====================
        FeedCatalogItem("ma-01", "MalwareUnicorn RE Tutorials", "https://malwareunicorn.org/feed.xml", "https://malwareunicorn.org", FeedDomainCategory.MALWARE_ANALYSIS, "Reverse engineering malware workshops and x86 disassembly guides", "Monthly", "Disassembly"),
        FeedCatalogItem("ma-02", "Any.run Sandbox Reports", "https://any.run/cybersecurity-blog/feed/", "https://any.run", FeedDomainCategory.MALWARE_ANALYSIS, "Interactive malware hunting sandbox with live network traffic dumps", "Daily", "Sandbox"),
        FeedCatalogItem("ma-03", "Hybrid Analysis Bulletins", "https://www.hybrid-analysis.com/rss", "https://www.hybrid-analysis.com", FeedDomainCategory.MALWARE_ANALYSIS, "Community automated malware detonations powered by Falcon Sandbox", "Real-time", "Hybrid"),
        FeedCatalogItem("ma-04", "Objective-See macOS Sec", "https://objective-see.org/rss.xml", "https://objective-see.org", FeedDomainCategory.MALWARE_ANALYSIS, "Patrick Wardle's open source macOS security tools and mac malware analysis", "Monthly", "macOS"),
        FeedCatalogItem("ma-05", "Hasherezade's Malware Blog", "https://hshrzd.wordpress.com/feed/", "https://hshrzd.wordpress.com", FeedDomainCategory.MALWARE_ANALYSIS, "Creator of PE-sieve and hollows_hunter unpacking in-memory payloads", "Monthly", "Unpacking"),
        FeedCatalogItem("ma-06", "Malwarebytes Labs Deep Dives", "https://www.malwarebytes.com/blog/threat-intelligence/feed", "https://www.malwarebytes.com", FeedDomainCategory.MALWARE_ANALYSIS, "Dissecting novel obfuscation techniques, polymorphic code and crypters", "Weekly", "Obfuscation"),
        FeedCatalogItem("ma-07", "Ghidra Community Releases", "https://github.com/NationalSecurityAgency/ghidra/releases.atom", "https://ghidra-sre.org", FeedDomainCategory.MALWARE_ANALYSIS, "NSA's open source software reverse engineering framework updates", "Monthly", "Ghidra"),
        FeedCatalogItem("ma-08", "Volatility Memory Forensics", "https://volatilityfoundation.org/blog/feed/", "https://volatilityfoundation.org", FeedDomainCategory.MALWARE_ANALYSIS, "World's most widely used memory forensics framework research", "Monthly", "Memory"),
        FeedCatalogItem("ma-09", "YARA Rules Project", "https://github.com/Yara-Rules/rules/releases.atom", "https://github.com/Yara-Rules/rules", FeedDomainCategory.MALWARE_ANALYSIS, "Public repository of curated YARA signatures for malware families", "Weekly", "Signatures"),
        FeedCatalogItem("ma-10", "Malpedia Research", "https://malpedia.caad.fkie.fraunhofer.de/rss", "https://malpedia.caad.fkie.fraunhofer.de", FeedDomainCategory.MALWARE_ANALYSIS, "Fraunhofer FKIE curated encyclopedia of malware families and actors", "Daily", "Encyclopedia"),
        FeedCatalogItem("ma-11", "Broadcom Symantec Threat Labs", "https://symantec-enterprise-blogs.security.com/blogs/threat-intelligence/rss.xml", "https://symantec.com", FeedDomainCategory.MALWARE_ANALYSIS, "Deep telemetry on targeted espionage campaigns and worms", "Weekly", "Symantec"),
        FeedCatalogItem("ma-12", "VirusTotal Community", "https://blog.virustotal.com/feeds/posts/default?alt=rss", "https://virustotal.com", FeedDomainCategory.MALWARE_ANALYSIS, "Multiscanner insights, code similarity analysis and enterprise VT tools", "Monthly", "VirusTotal"),

        // ==================== 11. OSINT & RECONNAISSANCE (10 Feeds) ====================
        FeedCatalogItem("os-01", "Bellingcat Investigations", "https://www.bellingcat.com/feed/", "https://www.bellingcat.com", FeedDomainCategory.OSINT, "Pioneering open source intelligence investigations and geolocation", "Weekly", "Bellingcat"),
        FeedCatalogItem("os-02", "IntelTechniques by Michael Bazzell", "https://inteltechniques.com/blog/feed/", "https://inteltechniques.com", FeedDomainCategory.OSINT, "OSINT search tools, digital privacy strategies and identity masking", "Weekly", "IntelTechniques"),
        FeedCatalogItem("os-03", "Sector035 - Week in OSINT", "https://sector035.nl/feed/", "https://sector035.nl", FeedDomainCategory.OSINT, "Weekly compilation of the best OSINT workflows, tips and challenges", "Weekly", "WeekInOSINT"),
        FeedCatalogItem("os-04", "OSINTCurious Project", "https://osintcurio.us/feed/", "https://osintcurio.us", FeedDomainCategory.OSINT, "Community tutorials on satellite imagery, social media analysis and recon", "Monthly", "ReconTips"),
        FeedCatalogItem("os-05", "DNSDumpster Domain Recon", "https://dnsdumpster.com/feed.xml", "https://dnsdumpster.com", FeedDomainCategory.OSINT, "DNS research tool for finding subdomains and mapped IP blocks", "Monthly", "DNS"),
        FeedCatalogItem("os-06", "SecurityTrails Attack Surface", "https://securitytrails.com/blog/feed", "https://securitytrails.com", FeedDomainCategory.OSINT, "Historical DNS, WHOIS records and external attack surface management", "Weekly", "AttackSurface"),
        FeedCatalogItem("os-07", "Nixintel OSINT Blog", "https://nixintel.info/feed/", "https://nixintel.info", FeedDomainCategory.OSINT, "Practical investigative tips for tracing cryptocurrency and domains", "Monthly", "CryptoTracing"),
        FeedCatalogItem("os-08", "Aware Online Academy", "https://www.aware-online.com/en/feed/", "https://www.aware-online.com", FeedDomainCategory.OSINT, "Methods for open source internet investigations and verification", "Monthly", "Verification"),
        FeedCatalogItem("os-09", "DomainTools Research", "https://www.domaintools.com/feed/", "https://www.domaintools.com/resources/blog/", FeedDomainCategory.OSINT, "Proactive threat intelligence leveraging domain registrations and WHOIS", "Weekly", "WHOIS"),
        FeedCatalogItem("os-10", "Spiderfoot OSINT Automation", "https://github.com/smicallef/spiderfoot/releases.atom", "https://spiderfoot.net", FeedDomainCategory.OSINT, "Automated open source reconnaissance collection engine updates", "Monthly", "Spiderfoot"),

        // ==================== 12. GOVERNMENT CERTS & ADVISORIES (15 Feeds) ====================
        FeedCatalogItem("gc-01", "CISA Advisories (US)", "https://www.cisa.gov/cybersecurity-advisories/all.xml", "https://www.cisa.gov", FeedDomainCategory.GOV_CERTS, "Cybersecurity and Infrastructure Security Agency urgent directives", "Real-time", "CISA"),
        FeedCatalogItem("gc-02", "NIST National Vulnerability Database", "https://nvd.nist.gov/feeds/xml/cve/misc/nvd-rss.xml", "https://nvd.nist.gov", FeedDomainCategory.GOV_CERTS, "Official US government repository of standards-based vulnerability data", "Real-time", "NVD"),
        FeedCatalogItem("gc-03", "NCSC UK Bulletins", "https://www.ncsc.gov.uk/api/1/services/v1/report-rss-feed.xml", "https://www.ncsc.gov.uk", FeedDomainCategory.GOV_CERTS, "UK National Cyber Security Centre technical advisories and incident notes", "Daily", "NCSC-UK"),
        FeedCatalogItem("gc-04", "CERT-EU Alerts", "https://cert.europa.eu/feed/", "https://cert.europa.eu", FeedDomainCategory.GOV_CERTS, "Computer Emergency Response Team for EU institutions, bodies and agencies", "Weekly", "CERT-EU"),
        FeedCatalogItem("gc-05", "Canadian Centre for Cyber Security", "https://cyber.gc.ca/en/rss/alerts-and-advisories.xml", "https://cyber.gc.ca", FeedDomainCategory.GOV_CERTS, "Alerts on commercial software flaws affecting Canadian organizations", "Daily", "CCCS"),
        FeedCatalogItem("gc-06", "Australian Cyber Security Centre (ACSC)", "https://www.cyber.gov.au/rss.xml", "https://www.cyber.gov.au", FeedDomainCategory.GOV_CERTS, "Essential Eight maturity model guidelines and national vulnerability alerts", "Weekly", "ACSC"),
        FeedCatalogItem("gc-07", "JPCERT/CC (Japan)", "https://www.jpcert.or.jp/rss/jpcert.rdf", "https://www.jpcert.or.jp", FeedDomainCategory.GOV_CERTS, "Japan Computer Emergency Response Team security advisories and stats", "Daily", "JPCERT"),
        FeedCatalogItem("gc-08", "BSI Germany Security (Allianz)", "https://www.bsi.bund.de/SiteGlobals/Functions/RSS/RSS_News.xml", "https://www.bsi.bund.de", FeedDomainCategory.GOV_CERTS, "Federal Office for Information Security cyber directives for critical infra", "Daily", "BSI"),
        FeedCatalogItem("gc-09", "ANSSI France Cyber Directives", "https://www.cert.ssi.gouv.fr/feed/", "https://www.cert.ssi.gouv.fr", FeedDomainCategory.GOV_CERTS, "French National Cybersecurity Agency security advisories and warnings", "Daily", "ANSSI"),
        FeedCatalogItem("gc-10", "SingCERT (Singapore)", "https://www.csa.gov.sg/alerts/rss", "https://www.csa.gov.sg/singcert", FeedDomainCategory.GOV_CERTS, "Singapore Cyber Emergency Response Team security alerts and patches", "Daily", "SingCERT"),
        FeedCatalogItem("gc-11", "GovCERT.ch (Switzerland)", "https://www.govcert.ch/feed/", "https://www.govcert.ch", FeedDomainCategory.GOV_CERTS, "Swiss national computer emergency team technical incident summaries", "Weekly", "GovCERT"),
        FeedCatalogItem("gc-12", "ENISA European Cyber Agency", "https://www.enisa.europa.eu/news/enisa-news/RSS", "https://www.enisa.europa.eu", FeedDomainCategory.GOV_CERTS, "European Union Agency for Cybersecurity threat landscape analysis", "Weekly", "ENISA"),
        FeedCatalogItem("gc-13", "US-CERT Archived Alerts", "https://www.cisa.gov/uscert/ncas/alerts.xml", "https://www.cisa.gov", FeedDomainCategory.GOV_CERTS, "Historical technical alert stream for critical IT infrastructure", "Daily", "US-CERT"),
        FeedCatalogItem("gc-14", "CERT-NZ (New Zealand)", "https://www.cert.govt.nz/feed/", "https://www.cert.govt.nz", FeedDomainCategory.GOV_CERTS, "National cyber threat alerts and guidance for New Zealand enterprises", "Daily", "CERT-NZ"),
        FeedCatalogItem("gc-15", "CISA Known Exploited Vulns (KEV)", "https://www.cisa.gov/sites/default/files/feeds/known_exploited_vulnerabilities.json", "https://www.cisa.gov/known-exploited-vulnerabilities-catalog", FeedDomainCategory.GOV_CERTS, "Mandatory patching deadlines for vulnerabilities actively weaponized in wild", "Real-time", "KEV"),

        // ==================== 13. CRYPTOGRAPHY & PRIVACY (10 Feeds) ====================
        FeedCatalogItem("cp-01", "Electronic Frontier Foundation (EFF)", "https://www.eff.org/rss/updates.xml", "https://www.eff.org", FeedDomainCategory.CRYPTOGRAPHY_PRIVACY, "Defending digital rights, encrypted communications and user privacy", "Daily", "EFF"),
        FeedCatalogItem("cp-02", "Tor Project News", "https://blog.torproject.org/rss.xml", "https://blog.torproject.org", FeedDomainCategory.CRYPTOGRAPHY_PRIVACY, "Onion routing protocol improvements, censorship circumvention and relay ops", "Weekly", "Tor"),
        FeedCatalogItem("cp-03", "Signal Messenger Technology Blog", "https://signal.org/blog/rss.xml", "https://signal.org/blog", FeedDomainCategory.CRYPTOGRAPHY_PRIVACY, "End-to-end encryption protocols, Post-Quantum PQXDH and secure enclaves", "Monthly", "Signal"),
        FeedCatalogItem("cp-04", "Cryptology ePrint Archive", "https://eprint.iacr.org/rss/rss.xml", "https://eprint.iacr.org", FeedDomainCategory.CRYPTOGRAPHY_PRIVACY, "International Association for Cryptologic Research preprint papers", "Daily", "IACR"),
        FeedCatalogItem("cp-05", "Let's Encrypt Updates", "https://letsencrypt.org/feed.xml", "https://letsencrypt.org", FeedDomainCategory.CRYPTOGRAPHY_PRIVACY, "Automated certificate authority, TLS protocol deprecations and ACME", "Monthly", "TLS"),
        FeedCatalogItem("cp-06", "Privacy International", "https://privacyinternational.org/rss.xml", "https://privacyinternational.org", FeedDomainCategory.CRYPTOGRAPHY_PRIVACY, "Global advocacy against unlawful surveillance and spyware vendors", "Weekly", "Privacy"),
        FeedCatalogItem("cp-07", "Matthew Green's Crypto Blog", "https://blog.cryptographyengineering.com/feed/", "https://blog.cryptographyengineering.com", FeedDomainCategory.CRYPTOGRAPHY_PRIVACY, "Johns Hopkins cryptographer breaking down zero-knowledge and TLS", "Monthly", "ZeroKnowledge"),
        FeedCatalogItem("cp-08", "OpenSSL Project Releases", "https://www.openssl.org/news/news.xml", "https://www.openssl.org", FeedDomainCategory.CRYPTOGRAPHY_PRIVACY, "Core TLS/SSL cryptographic library releases and CVE patches", "Monthly", "OpenSSL"),
        FeedCatalogItem("cp-09", "Proton Privacy Blog", "https://proton.me/blog/feed", "https://proton.me/blog", FeedDomainCategory.CRYPTOGRAPHY_PRIVACY, "Zero-access encryption, PGP email and privacy legislation updates", "Weekly", "Proton"),
        FeedCatalogItem("cp-10", "Freedom of the Press Foundation", "https://freedom.press/feed/", "https://freedom.press", FeedDomainCategory.CRYPTOGRAPHY_PRIVACY, "SecureDrop whistleblower platform and journalist digital protection", "Weekly", "SecureDrop")
    )

    fun filterFeeds(
        query: String = "",
        category: FeedDomainCategory? = null
    ): List<FeedCatalogItem> {
        val q = query.trim().lowercase()
        return allFeeds.filter { feed ->
            val matchesQuery = q.isEmpty() ||
                    feed.name.lowercase().contains(q) ||
                    feed.description.lowercase().contains(q) ||
                    feed.tag.lowercase().contains(q) ||
                    feed.feedUrl.lowercase().contains(q)

            val matchesCategory = category == null || feed.category == category

            matchesQuery && matchesCategory
        }
    }
}
