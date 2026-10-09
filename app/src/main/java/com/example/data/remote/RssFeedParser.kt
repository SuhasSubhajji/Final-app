package com.example.data.remote

import android.util.Xml
import com.example.data.model.FeedSource
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.regex.Pattern

object RssFeedParser {

    private val cvePattern = Pattern.compile("CVE-\\d{4}-\\d{4,7}", Pattern.CASE_INSENSITIVE)

    fun parse(inputStream: InputStream, source: FeedSource): List<ThreatItem> {
        val items = mutableListOf<ThreatItem>()
        try {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var isInsideItem = false
            var isInsideEntry = false

            var currentTitle: String? = null
            var currentLink: String? = null
            var currentDescription: String? = null
            var currentPubDate: String? = null
            var currentGuid: String? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name

                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (tagName.lowercase(Locale.ROOT)) {
                            "item" -> {
                                isInsideItem = true
                                currentTitle = null
                                currentLink = null
                                currentDescription = null
                                currentPubDate = null
                                currentGuid = null
                            }
                            "entry" -> {
                                isInsideEntry = true
                                currentTitle = null
                                currentLink = null
                                currentDescription = null
                                currentPubDate = null
                                currentGuid = null
                            }
                            "title" -> {
                                if (isInsideItem || isInsideEntry) {
                                    currentTitle = parser.nextText()
                                }
                            }
                            "link" -> {
                                if (isInsideItem || isInsideEntry) {
                                    val href = parser.getAttributeValue(null, "href")
                                    currentLink = href ?: parser.nextText()
                                }
                            }
                            "description", "summary", "content" -> {
                                if (isInsideItem || isInsideEntry) {
                                    if (currentDescription == null) {
                                        currentDescription = parser.nextText()
                                    }
                                }
                            }
                            "pubdate", "published", "updated" -> {
                                if (isInsideItem || isInsideEntry) {
                                    currentPubDate = parser.nextText()
                                }
                            }
                            "guid", "id" -> {
                                if (isInsideItem || isInsideEntry) {
                                    currentGuid = parser.nextText()
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (tagName.equals("item", ignoreCase = true) || tagName.equals("entry", ignoreCase = true)) {
                            if (!currentTitle.isNullOrBlank()) {
                                val cleanDesc = cleanHtml(currentDescription ?: "")
                                val rawText = "$currentTitle $cleanDesc"
                                val cves = extractCves(rawText)
                                val severity = evaluateSeverity(rawText, source)
                                val category = evaluateCategory(rawText, source)
                                val tags = extractTags(rawText, category)
                                val pubMillis = parsePubDate(currentPubDate)

                                val rawKey = currentGuid?.takeIf { it.isNotBlank() }
                                    ?: currentLink?.takeIf { it.isNotBlank() }
                                    ?: "$currentTitle-$pubMillis"

                                val safeId = "${source.name.lowercase().replace("[^a-z0-9]".toRegex(), "")}_${kotlin.math.abs(rawKey.hashCode())}"

                                items.add(
                                    ThreatItem(
                                        id = safeId,
                                        title = cleanHtml(currentTitle),
                                        description = cleanDesc.take(280).ifEmpty { "No description provided." },
                                        fullContent = cleanDesc,
                                        source = source,
                                        link = currentLink ?: source.websiteUrl,
                                        pubDateMillis = pubMillis,
                                        severity = severity,
                                        category = category,
                                        cveIds = cves,
                                        tags = tags
                                    )
                                )
                            }
                            isInsideItem = false
                            isInsideEntry = false
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return items
    }

    private fun cleanHtml(html: String): String {
        return html
            .replace(Regex("<[^>]*>"), " ")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun extractCves(text: String): List<String> {
        val matches = mutableSetOf<String>()
        val matcher = cvePattern.matcher(text)
        while (matcher.find()) {
            matches.add(matcher.group().uppercase(Locale.ROOT))
        }
        return matches.toList()
    }

    fun evaluateSeverity(text: String, source: FeedSource): ThreatSeverity {
        val lower = text.lowercase(Locale.ROOT)
        return when {
            lower.contains("critical") ||
            lower.contains("remote code execution") ||
            lower.contains("rce") ||
            lower.contains("zero-day") ||
            lower.contains("0-day") ||
            lower.contains("exploited in the wild") ||
            lower.contains("cvss 9") ||
            lower.contains("cvss 10") ||
            lower.contains("unauthenticated rce") ||
            lower.contains("active exploitation") -> ThreatSeverity.CRITICAL

            lower.contains("high severity") ||
            lower.contains("cvss 8") ||
            lower.contains("cvss 7") ||
            lower.contains("privilege escalation") ||
            lower.contains("ransomware") ||
            lower.contains("supply chain attack") ||
            lower.contains("arbitrary code") ||
            lower.contains("sql injection") ||
            lower.contains("ssrf") ||
            lower.contains("auth bypass") -> ThreatSeverity.HIGH

            lower.contains("medium") ||
            lower.contains("cross-site scripting") ||
            lower.contains("xss") ||
            lower.contains("denial of service") ||
            lower.contains("dos") ||
            lower.contains("information disclosure") ||
            lower.contains("directory traversal") -> ThreatSeverity.MEDIUM

            lower.contains("low") ||
            lower.contains("phishing") ||
            lower.contains("minor bug") ||
            lower.contains("deprecation") -> ThreatSeverity.LOW

            else -> if (source == FeedSource.CISA || source == FeedSource.NIST_NVD) ThreatSeverity.HIGH else ThreatSeverity.INFO
        }
    }

    fun evaluateCategory(text: String, source: FeedSource): ThreatCategory {
        val lower = text.lowercase(Locale.ROOT)
        return when {
            lower.contains("ci/cd") ||
            lower.contains("devsecops") ||
            lower.contains("pipeline") ||
            lower.contains("github actions") ||
            lower.contains("gitlab") ||
            lower.contains("jenkins") ||
            lower.contains("sast") ||
            lower.contains("dast") -> ThreatCategory.DEVSECOPS

            lower.contains("zero-day") ||
            lower.contains("0-day") ||
            lower.contains("under active attack") -> ThreatCategory.ZERO_DAY

            lower.contains("ransomware") ||
            lower.contains("lockbit") ||
            lower.contains("blackcat") ||
            lower.contains("trojan") ||
            lower.contains("infostealer") -> ThreatCategory.RANSOMWARE

            lower.contains("kubernetes") ||
            lower.contains("k8s") ||
            lower.contains("docker") ||
            lower.contains("container") ||
            lower.contains("aws") ||
            lower.contains("azure") ||
            lower.contains("gcp") ||
            lower.contains("cloud") ||
            lower.contains("s3") -> ThreatCategory.CLOUD_SECURITY

            lower.contains("npm") ||
            lower.contains("pypi") ||
            lower.contains("dependency") ||
            lower.contains("supply chain") ||
            lower.contains("backdoor") ||
            lower.contains("maven") ||
            lower.contains("sbom") -> ThreatCategory.SUPPLY_CHAIN

            lower.contains("api") ||
            lower.contains("web") ||
            lower.contains("graphql") ||
            lower.contains("rest") ||
            lower.contains("owasp") -> ThreatCategory.WEB_API

            else -> ThreatCategory.NETWORK
        }
    }

    private fun extractTags(text: String, category: ThreatCategory): List<String> {
        val lower = text.lowercase(Locale.ROOT)
        val tags = mutableSetOf(category.displayName)

        val keywords = listOf(
            "CVE", "Kubernetes", "Docker", "CI/CD", "GitHub Actions",
            "Zero-Day", "Ransomware", "RCE", "Supply Chain", "AWS",
            "Python", "NodeJS", "Go", "Terraform", "Privilege Escalation",
            "Linux", "Windows", "Firewall", "OpenSSL"
        )
        for (kw in keywords) {
            if (lower.contains(kw.lowercase(Locale.ROOT))) {
                tags.add(kw)
            }
        }
        return tags.take(5).toList()
    }

    private fun parsePubDate(dateStr: String?): Long {
        if (dateStr.isNullOrBlank()) return System.currentTimeMillis()
        val formats = listOf(
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd"
        )
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.US)
                val date = sdf.parse(dateStr.trim())
                if (date != null) return date.time
            } catch (e: Exception) {
            }
        }
        return System.currentTimeMillis()
    }
}
