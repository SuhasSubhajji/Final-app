package com.example.data.learning

import androidx.compose.ui.graphics.Color

enum class FundamentalCategory(val displayName: String, val badge: String, val color: Color) {
    CYBERSECURITY("Cybersecurity", "Core Principles", Color(0xFF10B981)),
    NETWORKING("Networking", "Protocols & Layers", Color(0xFF38BDF8)),
    CRYPTOGRAPHY("Cryptography", "Algorithms & Math", Color(0xFFA855F7))
}

data class AlgorithmStep(
    val stepNumber: Int,
    val title: String,
    val formulaOrCommand: String?,
    val explanation: String
)

data class FundamentalTopic(
    val id: String,
    val title: String,
    val category: FundamentalCategory,
    val tag: String,
    val summary: String,
    val coreConcept: String,
    val technicalBreakdown: String,
    val mathematicalFormula: String? = null,
    val algorithmSteps: List<AlgorithmStep> = emptyList(),
    val practicalCodeSnippet: String? = null,
    val attackVectorOrVulnerability: String,
    val defensiveTakeaway: String
)

object FundamentalsData {

    val topics: List<FundamentalTopic> = listOf(
        // =========================================================================
        // 1. CYBERSECURITY FUNDAMENTALS
        // =========================================================================
        FundamentalTopic(
            id = "sec_cia_triad",
            title = "CIA Triad & Parkerian Hexad",
            category = FundamentalCategory.CYBERSECURITY,
            tag = "Pillars of Security",
            summary = "The baseline model for information security: Confidentiality, Integrity, and Availability, extended by the Parkerian Hexad to include Authenticity, Possession, and Utility.",
            coreConcept = "Every security policy, threat model, and control is designed to preserve or measure one or more of these foundational security attributes against malicious compromise or accidental destruction.",
            technicalBreakdown = """
1. Confidentiality: Preventing unauthorized disclosure of data. Enforced via AES-256 encryption, access control lists (ACLs), and TLS.
2. Integrity: Guarding against unauthorized modification or tampering. Enforced via SHA-256 hashing, digital signatures, and database checksums.
3. Availability: Ensuring timely, reliable access to resources. Enforced via load balancing, DDoS mitigation, failover clusters, and immutable backups.
4. Authenticity (Hexad): Validating the true identity of an author or sender (e.g., X.509 PKI certificates).
5. Possession / Control (Hexad): The physical or logical custody of the asset, even if data is encrypted.
6. Utility (Hexad): The usefulness of the data (e.g., encrypted data without the key retains confidentiality but loses utility).
            """.trimIndent(),
            mathematicalFormula = "Security Risk = Threat × Vulnerability × Asset Impact",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Asset Identification", "Identify critical data assets and classification tier", "Catalog PII, intellectual property, and credentials."),
                AlgorithmStep(2, "Threat Mapping", "Map threats to CIA dimensions", "Determine if asset is subject to disclosure (C), tampering (I), or outage (A)."),
                AlgorithmStep(3, "Control Implementation", "Deploy preventative, detective, and corrective controls", "Apply encryption at rest, integrity hashing, and multi-region replication.")
            ),
            practicalCodeSnippet = """
# Python: Verifying File Integrity via SHA-256 (Integrity Assurance)
import hashlib

def verify_file_integrity(filepath: str, expected_hash: str) -> bool:
    hasher = hashlib.sha256()
    with open(filepath, 'rb') as f:
        while chunk := f.read(65536):
            hasher.update(chunk)
    computed_hash = hasher.hexdigest()
    return computed_hash.lower() == expected_hash.lower()
            """.trimIndent(),
            attackVectorOrVulnerability = "Ransomware targets Availability by encrypting disks; Man-in-the-Middle targets Confidentiality and Integrity; BOLA/IDOR flaws target Confidentiality.",
            defensiveTakeaway = "Balance security controls across all six dimensions rather than hyper-focusing solely on confidentiality."
        ),

        FundamentalTopic(
            id = "sec_access_control",
            title = "Access Control Models (DAC, MAC, RBAC, ABAC)",
            category = FundamentalCategory.CYBERSECURITY,
            tag = "Identity & Authorization",
            summary = "Formal security architectures governing how subjects (users, services) are granted rights to execute operations on objects (files, databases, endpoints).",
            coreConcept = "Access control enforces the Principle of Least Privilege (PoLP) and Separation of Duties (SoD) to contain blast radius and prevent unauthorized lateral movement.",
            technicalBreakdown = """
1. DAC (Discretionary Access Control): Object owners determine permissions (e.g., standard Unix file chmod/chown). Flexible but prone to accidental over-permissioning.
2. MAC (Mandatory Access Control): Operating system enforces centralized security labels based on classification clearances (e.g., SELinux, Bell-LaPadula model 'no read-up, no write-down', Biba integrity model).
3. RBAC (Role-Based Access Control): Permissions are assigned to roles (e.g., 'SecOps Tier 1', 'Billing Admin'), and users are assigned to roles. Highly scalable for enterprise IAM.
4. ABAC (Attribute-Based Access Control): Dynamic policies evaluated at runtime based on Subject attributes (role, department), Resource attributes (sensitivity, project), Action (GET, DELETE), and Environment context (device health, IP geolocation, time of day).
            """.trimIndent(),
            mathematicalFormula = "Decision = f(SubjectAttrs, ObjectAttrs, Action, Context) -> {ALLOW, DENY}",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Subject Context Evaluation", "Collect user attributes and device posture", "Verify user is in Engineering group, device is compliant, and connecting from corp VPN."),
                AlgorithmStep(2, "Policy Engine Query", "Evaluate XACML/Cedar/OPA Rego rules", "Check whether the combined attributes fulfill the attribute rule constraints."),
                AlgorithmStep(3, "Enforcement", "PEP (Policy Enforcement Point) allows or denies request", "Gateway permits HTTP request or returns 403 Forbidden.")
            ),
            practicalCodeSnippet = """
# OPA / Rego ABAC Policy Definition
package authz

default allow = false

# Allow access only during working hours from corporate managed endpoints
allow {
    input.action == "read"
    input.resource.type == "financial_report"
    input.user.role == "finance_auditor"
    input.device.is_compliant == true
    input.request.time_hour >= 8
    input.request.time_hour <= 18
}
            """.trimIndent(),
            attackVectorOrVulnerability = "Privilege escalation via role accumulation, stale admin accounts, and insecure direct object reference (IDOR/BOLA) where authorization checks are omitted.",
            defensiveTakeaway = "Adopt ABAC and Just-In-Time (JIT) access policies through tools like OPA (Open Policy Agent) to eliminate persistent over-privileged accounts."
        ),

        FundamentalTopic(
            id = "sec_threat_modeling",
            title = "Threat Modeling (STRIDE, DREAD, Attack Trees)",
            category = FundamentalCategory.CYBERSECURITY,
            tag = "Architecture Defense",
            summary = "A structured engineering methodology to identify, enumerate, and prioritize system vulnerabilities and threat scenarios before software is deployed.",
            coreConcept = "Threat modeling transforms security from reactive firefighting to proactive architectural defense by analyzing data flow diagrams (DFD) and trust boundaries.",
            technicalBreakdown = """
STRIDE Taxonomy:
- Spoofing (Identity): Impersonating an authenticated user or service -> Defense: FIDO2 MFA, mTLS.
- Tampering (Data): Altering data in transit or at rest -> Defense: Digital signatures, HMAC, SHA-256.
- Repudiation: Denying performing an action without verifiable logs -> Defense: Tamper-proof audit logging, SIEM.
- Information Disclosure: Exposing confidential data to unauthorized parties -> Defense: TLS 1.3, AES-GCM.
- Denial of Service: Exhausting system compute, memory, or bandwidth -> Defense: Rate limiting, WAF, autoscaling.
- Elevation of Privilege: Gaining unauthorized administrative control -> Defense: Least privilege, RBAC, input sanitization.

DREAD Scoring:
Rating each threat from 1-10 on Damage, Reproducibility, Exploitability, Affected Users, and Discoverability:
Risk Score = (D + R + E + A + D) / 5
            """.trimIndent(),
            mathematicalFormula = "DREAD Risk Score = (Damage + Reproducibility + Exploitability + AffectedUsers + Discoverability) / 5",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Deconstruct Architecture", "Draw Data Flow Diagram (DFD)", "Identify processes, data stores, external entities, and trust boundary crossings."),
                AlgorithmStep(2, "Apply STRIDE per Element", "Evaluate each element against 6 threat categories", "For every network link, evaluate Tampering and Information Disclosure."),
                AlgorithmStep(3, "Score with DREAD", "Compute quantitative risk severity", "Score potential damage and exploitability to prioritize remediation."),
                AlgorithmStep(4, "Mitigate & Verify", "Implement security controls and re-evaluate DFD", "Confirm threat residual risk falls within acceptable organizational tolerances.")
            ),
            practicalCodeSnippet = """
# Python: Automated DREAD Risk Rating Calculator
def calculate_dread(damage: int, repro: int, exploit: int, affected: int, discover: int) -> dict:
    score = (damage + repro + exploit + affected + discover) / 5.0
    severity = "CRITICAL" if score >= 8.0 else "HIGH" if score >= 6.0 else "MEDIUM" if score >= 4.0 else "LOW"
    return {"score": round(score, 2), "severity": severity}

# Example: SQL Injection on unauthenticated payment endpoint
payment_sqli = calculate_dread(damage=10, repro=9, exploit=8, affected=9, discover=8)
# Result: {'score': 8.8, 'severity': 'CRITICAL'}
            """.trimIndent(),
            attackVectorOrVulnerability = "Architectural blind spots where microservices cross trust boundaries without mutual authentication, or where internal APIs assume implicit trust.",
            defensiveTakeaway = "Integrate threat modeling into early sprint designs before writing production code to fix architectural flaws at zero development cost."
        ),

        // =========================================================================
        // 2. NETWORKING FUNDAMENTALS & PROTOCOLS
        // =========================================================================
        FundamentalTopic(
            id = "net_osi_model",
            title = "OSI 7-Layer vs TCP/IP Protocol Stack",
            category = FundamentalCategory.NETWORKING,
            tag = "Network Architecture",
            summary = "The canonical architectural models defining how data is formatted, addressed, routed, and transported across computer networks.",
            coreConcept = "Data moves down the stack via encapsulation (adding headers/trailers) on the sending host, and up the stack via decapsulation on the receiving host.",
            technicalBreakdown = """
OSI 7 Layers:
- Layer 7 (Application): HTTP, DNS, SSH, SMTP (PDU: Data) -> Threat: SQLi, XSS, DDoS HTTP floods.
- Layer 6 (Presentation): TLS/SSL, ASCII, JPEG, JSON (PDU: Data) -> Threat: Malformed payloads, encoding evasion.
- Layer 5 (Session): NetBIOS, RPC, SOCKS (PDU: Data) -> Threat: Session hijacking, token theft.
- Layer 4 (Transport): TCP, UDP (PDU: Segment/Datagram) -> Threat: SYN Flood, UDP reflection, port scanning.
- Layer 3 (Network): IPv4, IPv6, ICMP, IPsec (PDU: Packet) -> Threat: IP spoofing, BGP hijacking, Ping of Death.
- Layer 2 (Data Link): Ethernet, 802.11 Wi-Fi, ARP, MAC (PDU: Frame) -> Threat: ARP poisoning, MAC flooding, VLAN hopping.
- Layer 1 (Physical): Cables, Fiber, Radio Frequencies (PDU: Bits) -> Threat: Cable tapping, RF jamming, physical intrusion.

TCP/IP 4-Layer Mapping:
1. Application Layer (OSI 7, 6, 5)
2. Transport Layer (OSI 4)
3. Internet Layer (OSI 3)
4. Link / Network Access Layer (OSI 2, 1)
            """.trimIndent(),
            mathematicalFormula = "Total Frame Size = Data + L4_Header(20B) + L3_Header(20B) + L2_Header(14B) + FCS(4B)",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Application Generation", "App creates payload", "Browser creates HTTP GET request string."),
                AlgorithmStep(2, "Transport Encapsulation", "TCP adds source/dest ports", "Adds Sequence/ACK numbers and TCP flags (SYN/ACK)."),
                AlgorithmStep(3, "Network Encapsulation", "IP adds source/dest IP addresses", "Adds TTL, protocol identifier (6 for TCP), and packet checksum."),
                AlgorithmStep(4, "Data Link Framing", "Ethernet adds source/dest MAC addresses", "Resolves destination MAC via ARP and computes CRC32 frame check sequence (FCS)."),
                AlgorithmStep(5, "Physical Transmission", "NIC converts frame to physical signals", "Transmits bits across copper wire, optical fiber, or RF waves.")
            ),
            practicalCodeSnippet = """
# Python: Inspecting Packet Encapsulation Layers using Scapy
from scapy.all import IP, TCP, Raw

# Construct an encapsulated TCP packet
packet = IP(src="192.168.1.50", dst="10.0.0.1") / \
         TCP(sport=49152, dport=443, flags="S", seq=1000) / \
         Raw(load="TLS ClientHello")

print(f"Layer 3 (Network): {packet[IP].src} -> {packet[IP].dst}")
print(f"Layer 4 (Transport): Port {packet[TCP].sport} -> {packet[TCP].dport} [Flags: {packet[TCP].flags}]")
print(f"Payload Size: {len(packet[Raw].load)} bytes")
            """.trimIndent(),
            attackVectorOrVulnerability = "Adversaries manipulate protocol fields across layers: ARP spoofing at L2, IP spoofing at L3, SYN floods at L4, and HTTP smuggling at L7.",
            defensiveTakeaway = "Enforce defense-in-depth security controls mapped to each specific OSI layer (Dynamic ARP Inspection at L2, Firewalls at L3/L4, WAF at L7)."
        ),

        FundamentalTopic(
            id = "net_tcp_handshake",
            title = "TCP 3-Way Handshake & Connection State Machine",
            category = FundamentalCategory.NETWORKING,
            tag = "Transport Protocol",
            summary = "The connection-oriented synchronization protocol establishing reliable, ordered, full-duplex byte streams between two endpoints.",
            coreConcept = "Guarantees packet delivery, packet reordering, flow control (windowing), and congestion management over unreliable IP networks.",
            technicalBreakdown = """
3-Way Handshake (Connection Establishment):
1. Client -> Server: [SYN], Seq = ISN_c (Initial Sequence Number Client). Client enters SYN-SENT.
2. Server -> Client: [SYN, ACK], Seq = ISN_s, Ack = ISN_c + 1. Server enters SYN-RECEIVED.
3. Client -> Server: [ACK], Seq = ISN_c + 1, Ack = ISN_s + 1. Both endpoints enter ESTABLISHED.

4-Way Teardown (Connection Termination):
1. Client -> Server: [FIN], Seq = u. Client enters FIN-WAIT-1.
2. Server -> Client: [ACK], Ack = u + 1. Server enters CLOSE-WAIT, Client enters FIN-WAIT-2.
3. Server -> Client: [FIN], Seq = v. Server enters LAST-ACK.
4. Client -> Server: [ACK], Ack = v + 1. Client enters TIME-WAIT (waits 2*MSL = 120s), Server closes.

TCP Header Flags:
- SYN (Synchronize sequence numbers)
- ACK (Acknowledgment field valid)
- FIN (No more data from sender)
- RST (Reset the connection abruptly)
- PSH (Push buffered data immediately to app)
- URG (Urgent pointer field significant)
            """.trimIndent(),
            mathematicalFormula = "Next_Ack = Received_Seq + Payload_Bytes (+1 if SYN or FIN flag present)",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Client Initiates", "Send SYN with random ISN", "Client chooses random 32-bit Initial Sequence Number to prevent blind spoofing."),
                AlgorithmStep(2, "Server Allocates", "Allocate TCB and reply with SYN-ACK", "Server creates Transmission Control Block in kernel memory and increments client sequence."),
                AlgorithmStep(3, "Client Finalizes", "Send ACK to confirm server sequence", "Connection enters ESTABLISHED and application data transmission begins.")
            ),
            practicalCodeSnippet = """
# Python: Raw Socket Simulation of TCP Handshake Verification
import socket

def test_tcp_connection(host: str, port: int, timeout: float = 3.0) -> bool:
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.settimeout(timeout)
    try:
        # Connect initiates the 3-Way Handshake: SYN -> SYN-ACK -> ACK
        sock.connect((host, port))
        sock.shutdown(socket.SHUT_RDWR)
        return True
    except (socket.timeout, ConnectionRefusedError):
        return False
    finally:
        sock.close()
            """.trimIndent(),
            attackVectorOrVulnerability = "SYN Flood Attack: Adversaries send millions of SYN packets with spoofed IPs, exhausting the server's SYN backlog queue (half-open connection table).",
            defensiveTakeaway = "Enable SYN Cookies (RFC 4987) on servers to encode connection state in the sequence number without allocating memory until the final ACK arrives."
        ),

        FundamentalTopic(
            id = "net_subnetting_cidr",
            title = "IP Addressing, CIDR & Subnetting Algorithms",
            category = FundamentalCategory.NETWORKING,
            tag = "Network Routing",
            summary = "The algorithmic partitioning of IPv4/IPv6 address spaces into isolated sub-networks to control routing domains, optimize broadcast traffic, and enforce security perimeters.",
            coreConcept = "An IP address consists of a Network portion and a Host portion, delineated mathematically by a bitwise AND operation with the Subnet Mask.",
            technicalBreakdown = """
CIDR (Classless Inter-Domain Routing):
Written as IP/prefix (e.g., 192.168.1.0/24). The prefix indicates how many leading bits belong to the network identifier.

Subnet Math Rules:
- Number of Host Bits: h = 32 - PrefixLength
- Total Addresses: 2^h
- Usable Host Addresses: 2^h - 2 (subtract Network ID and Broadcast Address)
- Network Address: IP & SubnetMask (bitwise AND)
- Broadcast Address: Network Address | (~SubnetMask) (bitwise OR with inverted mask)

Common Subnet Masks:
/24 -> 255.255.255.0 -> 256 IPs, 254 usable hosts
/26 -> 255.255.255.192 -> 64 IPs, 62 usable hosts
/28 -> 255.255.255.240 -> 16 IPs, 14 usable hosts
/30 -> 255.255.255.252 -> 4 IPs, 2 usable hosts (point-to-point links)
/32 -> 255.255.255.255 -> 1 IP (single host route)
            """.trimIndent(),
            mathematicalFormula = "Usable Hosts = 2^(32 - Prefix) - 2; Network_ID = IP & Subnet_Mask",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Identify Prefix Length", "Determine required hosts per subnet", "If a subnet needs 50 hosts, find smallest power of 2: 2^6 = 64. Host bits = 6. Prefix = 32 - 6 = /26."),
                AlgorithmStep(2, "Calculate Subnet Mask", "Set leading prefix bits to 1", "/26 has 26 ones: 11111111.11111111.11111111.11000000 = 255.255.255.192."),
                AlgorithmStep(3, "Derive Network & Broadcast", "Apply bitwise operations", "For IP 192.168.1.130/26: Network ID is 192.168.1.128, Broadcast is 192.168.1.191.")
            ),
            practicalCodeSnippet = """
# Python: IP Subnet Calculator Algorithm
import ipaddress

def calculate_subnet_details(cidr: str):
    net = ipaddress.ip_network(cidr, strict=False)
    return {
        "network_address": str(net.network_address),
        "broadcast_address": str(net.broadcast_address),
        "netmask": str(net.netmask),
        "total_hosts": net.num_addresses,
        "usable_hosts": max(0, net.num_addresses - 2),
        "first_usable_ip": str(list(net.hosts())[0]) if net.num_addresses > 2 else "N/A",
        "last_usable_ip": str(list(net.hosts())[-1]) if net.num_addresses > 2 else "N/A"
    }

# Example: calculate_subnet_details("10.0.4.15/22")
# Returns: Network 10.0.4.0, Broadcast 10.0.7.255, 1022 usable hosts
            """.trimIndent(),
            attackVectorOrVulnerability = "Flat networks without subnet isolation allow an adversary compromising a single low-privilege workstation to broadcast and sniff traffic across entire databases.",
            defensiveTakeaway = "Enforce micro-segmentation using /28 or /29 subnets with strict Access Control Lists (ACLs) and stateful firewalls between tiers (DMZ, App, DB)."
        ),

        // =========================================================================
        // 3. CRYPTOGRAPHY & DETAILED ALGORITHMS
        // =========================================================================
        FundamentalTopic(
            id = "crypto_aes",
            title = "AES (Advanced Encryption Standard / Rijndael)",
            category = FundamentalCategory.CRYPTOGRAPHY,
            tag = "Symmetric Block Cipher",
            summary = "The globally adopted symmetric block cipher approved by NIST (FIPS 197), processing 128-bit blocks using 128, 192, or 256-bit keys.",
            coreConcept = "Operates on a 4x4 matrix of bytes called the State through repeated rounds of non-linear substitution, byte permutation, and finite field matrix arithmetic.",
            technicalBreakdown = """
AES Structure & Rounds:
- AES-128: 10 rounds, Key Expansion creates 11 round keys (44 words).
- AES-192: 12 rounds, Key Expansion creates 13 round keys (52 words).
- AES-256: 14 rounds, Key Expansion creates 15 round keys (60 words).

Core Round Operations:
1. SubBytes: Non-linear byte substitution using an 8-bit S-box constructed from multiplicative inverses in Galois Field GF(2^8) with irreducible polynomial m(x) = x^8 + x^4 + x^3 + x + 1, followed by an affine mapping.
2. ShiftRows: Transposition step cyclically shifting the 4 rows of the State matrix: Row 0 shifted 0 bytes, Row 1 shifted 1 byte left, Row 2 shifted 2 bytes left, Row 3 shifted 3 bytes left.
3. MixColumns: Linear transformation multiplying each column of the State with a fixed invertible polynomial c(x) = {03}x^3 + {01}x^2 + {01}x + {02} modulo x^4 + 1 over GF(2^8). (Omitted in final round).
4. AddRoundKey: Bitwise XOR between the 128-bit State matrix and the corresponding 128-bit subkey generated from the Key Schedule.

Modes of Operation:
- GCM (Galois/Counter Mode): Recommended AEAD mode. Combines CTR encryption with GHASH authenticator in GF(2^128). Provides Confidentiality + Integrity Tag.
- CBC (Cipher Block Chaining): Requires random IV and PKCS#7 padding. Vulnerable to padding oracle attacks if error responses leak.
            """.trimIndent(),
            mathematicalFormula = "State_Round = AddRoundKey(MixColumns(ShiftRows(SubBytes(State))))",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Key Expansion", "Generate round keys using Rijndael Key Schedule", "Uses word rotations, S-box substitution (SubWord), and round constants (Rcon)."),
                AlgorithmStep(2, "Initial Round", "AddRoundKey(State, Key_0)", "XOR plaintext 16-byte block with initial round key."),
                AlgorithmStep(3, "Main Rounds (1 to Nr-1)", "Execute SubBytes -> ShiftRows -> MixColumns -> AddRoundKey", "Repeats for 9, 11, or 13 rounds depending on key length."),
                AlgorithmStep(4, "Final Round (Nr)", "Execute SubBytes -> ShiftRows -> AddRoundKey", "MixColumns is omitted to ensure symmetry between encryption and decryption."),
                AlgorithmStep(5, "Ciphertext Output", "Extract 16-byte encrypted block", "For GCM, compute GHASH over ciphertext and additional authenticated data (AAD).")
            ),
            practicalCodeSnippet = """
# Python / Cryptography: AES-256-GCM Authenticated Encryption
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
import os

# 1. Generate secure 256-bit key and 96-bit nonce
key = AESGCM.generate_key(bit_length=256)
nonce = os.urandom(12) # 96-bit nonce mandatory for GCM

# 2. Encrypt plaintext with optional Additional Authenticated Data (AAD)
aesgcm = AESGCM(key)
plaintext = b"Confidential Cyber Intelligence Report"
associated_data = b"header-timestamp:2026-10-16"

ciphertext = aesgcm.encrypt(nonce, plaintext, associated_data)

# 3. Decrypt and verify authentication tag (Integrity verified automatically)
decrypted = aesgcm.decrypt(nonce, ciphertext, associated_data)
assert decrypted == plaintext
            """.trimIndent(),
            attackVectorOrVulnerability = "Reusing the same (Key, Nonce) pair in AES-GCM allows adversaries to recover the auth key and decrypt messages; Padding Oracle attacks against AES-CBC.",
            defensiveTakeaway = "Always use AES-GCM or ChaCha20-Poly1305 (AEAD); never reuse nonces with the same key, and avoid AES-ECB under all circumstances."
        ),

        FundamentalTopic(
            id = "crypto_rsa",
            title = "RSA Public-Key Cryptosystem & Math",
            category = FundamentalCategory.CRYPTOGRAPHY,
            tag = "Asymmetric Cryptography",
            summary = "The foundational public-key cryptosystem based on the mathematical difficulty of factoring the product of two large prime numbers.",
            coreConcept = "Enables secure key exchange, encryption, and digital signatures without requiring parties to share a secret key in advance.",
            technicalBreakdown = """
Mathematical Foundations:
- Euler's Totient Function: For prime p, phi(p) = p - 1. For n = p * q, phi(n) = (p - 1)(q - 1).
- Euler's Theorem: If gcd(m, n) = 1, then m^phi(n) = 1 (mod n).
- Modular Multiplicative Inverse: d * e = 1 (mod phi(n)), found using Extended Euclidean Algorithm.

RSA Algorithm Steps:
1. Key Generation:
   - Select two large distinct primes p and q (typically 1024 or 2048 bits each).
   - Compute modulus: n = p * q. (Length of n in bits is the key size).
   - Compute totient: phi(n) = (p - 1) * (q - 1).
   - Choose public exponent e such that 1 < e < phi(n) and gcd(e, phi(n)) = 1. (Standard choice: e = 65537 = 2^16 + 1).
   - Compute private exponent d: d = e^(-1) (mod phi(n)).
   - Public Key: (e, n). Private Key: (d, n).

2. Encryption:
   - Ciphertext: c = m^e (mod n) where message 0 <= m < n.

3. Decryption:
   - Plaintext: m = c^d (mod n)
   - Proof: c^d = (m^e)^d = m^(e*d) = m^(k*phi(n) + 1) = (m^phi(n))^k * m = 1^k * m = m (mod n).

Padding Schemes:
Raw RSA ('textbook RSA') is insecure due to determinism (m^e mod n is always the same). Production RSA requires OAEP (Optimal Asymmetric Encryption Padding) for encryption and PSS (Probabilistic Signature Scheme) for signing.
            """.trimIndent(),
            mathematicalFormula = "c ≡ m^e (mod n); m ≡ c^d (mod n); e·d ≡ 1 (mod φ(n))",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Prime Generation", "Generate primes p and q via Miller-Rabin test", "Ensure primes are distinct, random, and cryptographically secure."),
                AlgorithmStep(2, "Compute Modulus & Totient", "n = p * q, phi = (p-1)*(q-1)", "Keep p, q, and phi strictly confidential, then discard."),
                AlgorithmStep(3, "Select Public Exponent", "Set e = 65537", "Verify gcd(65537, phi) == 1."),
                AlgorithmStep(4, "Derive Private Exponent", "d = modInverse(e, phi)", "Use Extended Euclidean Algorithm: e*d + phi*y = 1."),
                AlgorithmStep(5, "Encrypt / Decrypt", "Apply modular exponentiation", "c = pow(m, e, n) for encryption; m = pow(c, d, n) for decryption.")
            ),
            practicalCodeSnippet = """
# Python: Textbook RSA Algorithm Math Demonstration
def extended_gcd(a: int, b: int):
    if a == 0:
        return b, 0, 1
    gcd, x1, y1 = extended_gcd(b % a, a)
    x = y1 - (b // a) * x1
    y = x1
    return gcd, x, y

def mod_inverse(e: int, phi: int) -> int:
    gcd, x, _ = extended_gcd(e, phi)
    if gcd != 1:
        raise ValueError("Modular inverse does not exist")
    return (x % phi + phi) % phi

# Sample Small Primes for Math Verification
p, q = 61, 53
n = p * q           # 3233
phi = (p - 1) * (q - 1) # 3120
e = 17              # coprime to 3120
d = mod_inverse(e, phi) # 2753

message = 65
ciphertext = pow(message, e, n)      # 65^17 mod 3233 = 2790
decrypted = pow(ciphertext, d, n)    # 2790^2753 mod 3233 = 65
assert message == decrypted
            """.trimIndent(),
            attackVectorOrVulnerability = "Factoring modulus n if primes are generated with low entropy, or Bleichenbacher padding oracle attacks on PKCS#1 v1.5 padding.",
            defensiveTakeaway = "Use minimum 3072-bit or 4096-bit RSA keys with RSA-OAEP padding; plan migration to Post-Quantum Cryptography (ML-KEM / Kyber)."
        ),

        FundamentalTopic(
            id = "crypto_diffie_hellman",
            title = "Diffie-Hellman & Elliptic Curve Key Exchange (ECDH)",
            category = FundamentalCategory.CRYPTOGRAPHY,
            tag = "Key Exchange Protocol",
            summary = "The cryptographic method enabling two parties to establish a shared secret over an insecure channel without an eavesdropper being able to deduce it.",
            coreConcept = "Relies on the computational hardness of the Discrete Logarithm Problem (DLP) in finite groups, or Elliptic Curve DLP (ECDLP).",
            technicalBreakdown = """
Classical Diffie-Hellman (DH):
1. Alice and Bob agree on public parameters: large prime p and generator g of group Z_p*.
2. Alice picks private integer a, computes public key A = g^a mod p.
3. Bob picks private integer b, computes public key B = g^b mod p.
4. Alice and Bob exchange A and B publicly.
5. Alice computes shared secret: s = B^a mod p = (g^b)^a mod p.
6. Bob computes shared secret: s = A^b mod p = (g^a)^b mod p.
An eavesdropper knowing g, p, A, B cannot compute g^(ab) mod p without solving the discrete logarithm problem.

Elliptic Curve Diffie-Hellman (ECDH):
Instead of modular exponentiation, operations are performed on points of an elliptic curve:
y^2 = x^3 + ax + b (mod p)
- Alice picks private scalar d_A, computes public point Q_A = d_A * G (where G is the base point).
- Bob picks private scalar d_B, computes public point Q_B = d_B * G.
- Shared Secret Point: S = d_A * Q_B = d_A * (d_B * G) = d_B * (d_A * G) = d_B * Q_A.
- Resulting x-coordinate of S is hashed to produce the symmetric AES session key.
- Security Advantage: 256-bit ECC provides equivalent security to 3072-bit RSA with 10x smaller key size and much lower latency.
            """.trimIndent(),
            mathematicalFormula = "Shared Secret: s = (g^b)^a ≡ (g^a)^b (mod p); S = d_A · Q_B = d_B · Q_A",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Curve Selection", "Select standardized curve parameters", "Common curves: NIST P-256 (secp256r1) or Curve25519 (X25519)."),
                AlgorithmStep(2, "Ephemeral Keypair Generation", "Generate private scalar and public point", "Alice generates random d_A, computes Q_A = d_A * G."),
                AlgorithmStep(3, "Exchange Public Points", "Transmit public points across untrusted network", "Alice sends Q_A to Bob, Bob sends Q_B to Alice."),
                AlgorithmStep(4, "Compute Scalar Multiplication", "Derive shared point S", "Alice executes S = d_A * Q_B; Bob executes S = d_B * Q_A."),
                AlgorithmStep(5, "Key Derivation Function (KDF)", "Derive symmetric key using HKDF", "Pass x-coordinate of S through HKDF-SHA256 to generate AES-256 session keys.")
            ),
            practicalCodeSnippet = """
# Python / Cryptography: X25519 Elliptic Curve Key Exchange
from cryptography.hazmat.primitives.asymmetric import x25519
from cryptography.hazmat.primitives.kdf.hkdf import HKDF
from cryptography.hazmat.primitives import hashes

# 1. Alice and Bob generate ephemeral private keys
alice_private = x25519.X25519PrivateKey.generate()
alice_public = alice_private.public_key()

bob_private = x25519.X25519PrivateKey.generate()
bob_public = bob_private.public_key()

# 2. Both parties compute shared secret from other's public key
alice_shared = alice_private.exchange(bob_public)
bob_shared = bob_private.exchange(alice_public)
assert alice_shared == bob_shared

# 3. Derive symmetric key using HKDF-SHA256
derived_key = HKDF(
    algorithm=hashes.SHA256(),
    length=32,
    salt=None,
    info=b"TLS 1.3 Key Exchange"
).derive(alice_shared)
            """.trimIndent(),
            attackVectorOrVulnerability = "Man-in-the-Middle (MitM) if public keys are not authenticated; Logjam attack if weak export-grade 512-bit DH prime groups are used.",
            defensiveTakeaway = "Always use Ephemeral Diffie-Hellman (DHE / ECDHE) authenticated with digital signatures (TLS 1.3) to provide Perfect Forward Secrecy (PFS)."
        ),

        FundamentalTopic(
            id = "crypto_sha256",
            title = "SHA-256 & Cryptographic Hashing Algorithms",
            category = FundamentalCategory.CRYPTOGRAPHY,
            tag = "Hash Functions & Integrity",
            summary = "One-way cryptographic hash functions converting arbitrary-length messages into a fixed 256-bit digest, engineered to resist collisions and preimages.",
            coreConcept = "Possesses three vital properties: Preimage Resistance (one-way), Second Preimage Resistance (weak collision resistance), and Collision Resistance (strong collision resistance).",
            technicalBreakdown = """
SHA-256 Architecture (Merkle-Damgard Construction):
1. Message Padding:
   - Append single '1' bit (0x80).
   - Pad with zeros until message length = 448 (mod 512).
   - Append original 64-bit message length in big-endian format. Total length is a multiple of 512 bits.

2. State Initialization:
   - Eight 32-bit state registers H0 to H7 initialized to the fractional parts of the square roots of the first 8 prime numbers (2 to 19).

3. Message Schedule (64 words W_0 to W_63):
   - First 16 words W_0..W_15 are the 16 32-bit words of the current block.
   - Words 16 to 63 computed recursively:
     W_t = sigma1(W_t-2) + W_t-7 + sigma0(W_t-15) + W_t-16 (mod 2^32)
   - sigma0(x) = ROTR^7(x) ^ ROTR^18(x) ^ SHR^3(x)
   - sigma1(x) = ROTR^17(x) ^ ROTR^19(x) ^ SHR^10(x)

4. 64 Compression Rounds:
   - For t = 0 to 63:
     T1 = h + Sigma1(e) + Ch(e, f, g) + K_t + W_t
     T2 = Sigma0(a) + Maj(a, b, c)
     h = g; g = f; f = e; e = d + T1; d = c; c = b; b = a; a = T1 + T2
   - Functions:
     Ch(e, f, g) = (e & f) ^ (~e & g)
     Maj(a, b, c) = (a & b) ^ (a & c) ^ (b & c)
     Sigma0(x) = ROTR^2(x) ^ ROTR^13(x) ^ ROTR^22(x)
     Sigma1(x) = ROTR^6(x) ^ ROTR^11(x) ^ ROTR^25(x)
   - K_t are 64 constants derived from the cube roots of the first 64 primes.

5. Final State Addition:
   - H0 = H0 + a; H1 = H1 + b; ... H7 = H7 + h (mod 2^32).
   - Concatenate H0..H7 to form the 256-bit hash.
            """.trimIndent(),
            mathematicalFormula = "Digest = H0 || H1 || H2 || H3 || H4 || H5 || H6 || H7 (256 bits)",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Bit Padding", "Pad message to length ≡ 448 mod 512", "Append bit '1' followed by k zeros."),
                AlgorithmStep(2, "Length Append", "Append 64-bit original bit length", "Guarantees different message lengths never collide."),
                AlgorithmStep(3, "Expand Schedule", "Compute 64 words W_t from 16 words", "Applies bitwise rotations and right shifts."),
                AlgorithmStep(4, "Run 64 Rounds", "Mix working variables a-h using Ch, Maj, Sigma", "Calculates temporary values T1 and T2."),
                AlgorithmStep(5, "Add to State", "Sum output variables with prior hash state", "Outputs final 64-character hexadecimal digest.")
            ),
            practicalCodeSnippet = """
# Python: Bitwise SHA-256 Core Compression Logic Demonstration
def rotr(x: int, n: int) -> int:
    return ((x >> n) | (x << (32 - n))) & 0xFFFFFFFF

def ch(e: int, f: int, g: int) -> int:
    return (e & f) ^ (~e & g)

def maj(a: int, b: int, c: int) -> int:
    return (a & b) ^ (a & c) ^ (b & c)

def sigma0(x: int) -> int:
    return rotr(x, 2) ^ rotr(x, 13) ^ rotr(x, 22)

def sigma1(x: int) -> int:
    return rotr(x, 6) ^ rotr(x, 11) ^ rotr(x, 25)

# Notice how each round irreversibly diffuses and avalanches input bits
            """.trimIndent(),
            attackVectorOrVulnerability = "Length Extension Attack: If SHA-256(secret || message) is used as a naive MAC, an attacker can append data without knowing the secret.",
            defensiveTakeaway = "Never use naive concatenation for message authentication; always use HMAC-SHA256 (RFC 2104) or SHA-3 (Keccak) to prevent length extension attacks."
        ),

        FundamentalTopic(
            id = "crypto_pqc",
            title = "Post-Quantum Cryptography (ML-KEM / Kyber & ML-DSA)",
            category = FundamentalCategory.CRYPTOGRAPHY,
            tag = "Next-Gen Cryptography",
            summary = "Cryptographic algorithms designed to withstand attacks by quantum computers running Shor's and Grover's algorithms, standardized by NIST (FIPS 203/204).",
            coreConcept = "Replaces number-theoretic problems (factoring, discrete logs) that Shor's algorithm solves in polynomial time with hard high-dimensional lattice math.",
            technicalBreakdown = """
The Quantum Threat:
- Shor's Algorithm: Solves prime factorization (RSA) and discrete logarithms (DH, ECDH, ECDSA) in polynomial time O((log N)^3), completely breaking all existing public-key infrastructure.
- Grover's Algorithm: Provides quadratic speedup against symmetric ciphers and hashing O(sqrt(N)), effectively halving security (AES-128 becomes 64-bit; AES-256 remains safe at 128-bit quantum security).

NIST PQC Standards (August 2024):
1. ML-KEM (Module-Lattice-Based Key-Encapsulation Mechanism, formerly CRYSTALS-Kyber) - FIPS 203:
   - Primary standard for general encryption and key exchange.
   - Based on Module Learning With Errors (M-LWE) problem: Given (A, A*s + e), finding secret vector s is computationally intractable even for quantum computers.
   - Parameter sets: ML-KEM-512 (Security Cat 1), ML-KEM-768 (Cat 3, recommended default), ML-KEM-1024 (Cat 5).

2. ML-DSA (Module-Lattice-Based Digital Signature Algorithm, formerly CRYSTALS-Dilithium) - FIPS 204:
   - Primary standard for general digital signatures.
   - Based on Module Learning With Errors and Short Integer Solution (M-SIS) problems on module lattices.

3. SLH-DSA (Stateless Hash-Based Digital Signature Algorithm, formerly SPHINCS+) - FIPS 205:
   - Backup signature standard based purely on secure hash functions (SHA-256/SHAKE256), immune to any lattice breaks.
            """.trimIndent(),
            mathematicalFormula = "M-LWE: b = A·s + e (mod q) where e is small Gaussian noise vector",
            algorithmSteps = listOf(
                AlgorithmStep(1, "Lattice Matrix Setup", "Generate polynomial matrix A over ring R_q", "R_q = Z_q[X] / (X^n + 1) with degree n = 256 and prime modulus q = 3329."),
                AlgorithmStep(2, "Secret Key Generation", "Sample secret vector s and error e from small noise distribution", "The noise makes finding s mathematically intractable."),
                AlgorithmStep(3, "Public Key Encapsulation", "Compute t = A*s + e and publish public key", "Transmitted to peer in TLS 1.3 hybrid post-quantum key exchange."),
                AlgorithmStep(4, "Decapsulation", "Peer uses secret s to remove noise and derive shared secret", "Yields quantum-resistant symmetric session key.")
            ),
            practicalCodeSnippet = """
# Conceptual Python: Hybrid Post-Quantum Key Exchange Flow in TLS 1.3
# Combines classical X25519 with Post-Quantum ML-KEM-768 (X25519MLKEM768)
def hybrid_key_exchange(x25519_secret: bytes, mlkem_secret: bytes) -> bytes:
    import hashlib
    # Both secrets concatenated: If either system remains secure, the session key is unbreakable
    hybrid_input = x25519_secret + mlkem_secret
    quantum_safe_session_key = hashlib.sha256(hybrid_input).digest()
    return quantum_safe_session_key
            """.trimIndent(),
            attackVectorOrVulnerability = "'Harvest Now, Decrypt Later' (HNDL): Adversaries intercept and store encrypted TLS traffic today to decrypt it once cryptanalytically relevant quantum computers (CRQC) emerge.",
            defensiveTakeaway = "Deploy hybrid classical + post-quantum key exchanges (X25519 + ML-KEM-768) in production TLS configurations immediately to protect long-lived sensitive data."
        )
    )
}
