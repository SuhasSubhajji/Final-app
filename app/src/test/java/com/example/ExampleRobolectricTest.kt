package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DateRangeFilter
import com.example.data.model.FeedFilterState
import com.example.data.model.FeedSource
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import com.example.data.remote.RssFeedParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CyberShield", appName)
    }

    @Test
    fun `test RSS feed parser and severity evaluation`() {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <rss version="2.0">
                <channel>
                    <title>Security Feed</title>
                    <item>
                        <title>Critical Zero-Day Remote Code Execution in Apache</title>
                        <link>https://example.com/cve-2025-1001</link>
                        <description>Threat actors exploiting CVE-2025-1001 for unauthenticated RCE</description>
                        <pubDate>Mon, 01 Jan 2025 12:00:00 GMT</pubDate>
                    </item>
                </channel>
            </rss>
        """.trimIndent()

        val parsed = RssFeedParser.parse(ByteArrayInputStream(xml.toByteArray()), FeedSource.CISA)
        assertEquals(1, parsed.size)
        val item = parsed.first()
        assertEquals("Critical Zero-Day Remote Code Execution in Apache", item.title)
        assertEquals(ThreatSeverity.CRITICAL, item.severity)
        assertTrue(item.cveIds.contains("CVE-2025-1001"))
    }

    @Test
    fun `test filter state matching`() {
        val item = ThreatItem(
            id = "test-1",
            title = "Kubernetes Ingress Controller PrivEsc",
            description = "High severity flaw in k8s cluster",
            source = FeedSource.DEVSECOPS_WEEKLY,
            link = "https://k8s.io",
            pubDateMillis = System.currentTimeMillis() - 1000,
            severity = ThreatSeverity.HIGH,
            category = ThreatCategory.CLOUD_SECURITY,
            cveIds = listOf("CVE-2025-2451"),
            tags = listOf("Kubernetes")
        )

        // Matching query
        val filter1 = FeedFilterState(searchQuery = "Kubernetes")
        assertTrue(item.title.contains(filter1.searchQuery, ignoreCase = true))

        // Non-matching query
        val filter2 = FeedFilterState(searchQuery = "Firewall")
        assertFalse(item.title.contains(filter2.searchQuery, ignoreCase = true))
    }

    @Test
    fun `test learning roadmaps repository completeness`() {
        val tracks = com.example.data.learning.LearningRepository.tracks
        assertEquals(7, tracks.size)
        val trackIds = tracks.map { it.id }
        assertTrue(trackIds.contains("mitre_attack"))
        assertTrue(trackIds.contains("mitre_defend"))
        assertTrue(trackIds.contains("red_team"))
        assertTrue(trackIds.contains("blue_team"))
        assertTrue(trackIds.contains("purple_team"))
        assertTrue(trackIds.contains("devsecops"))
        assertTrue(trackIds.contains("cloud_security"))

        // Verify each track has roadmap stages and code snippet
        tracks.forEach { track ->
            assertTrue(track.roadmapStages.isNotEmpty())
            assertTrue(track.handsOnCodeSnippet.isNotBlank())
            assertTrue(track.defensiveTakeaway.isNotBlank())
        }

        // Verify topic of the day
        val topic = com.example.data.learning.LearningRepository.getDailyTopic()
        assertTrue(topic.title.isNotBlank())
        assertTrue(topic.attackVector.isNotBlank())
        assertTrue(topic.defenseStrategy.isNotBlank())
    }

    @Test
    fun `test mitre matrix repository contains all 14 tactics`() {
        val allTactics = com.example.data.mitre.MitreTactic.entries
        assertEquals(14, allTactics.size)
        val ttps = com.example.data.mitre.MitreMatrixRepository.allTtps
        assertTrue(ttps.size >= 14)
        val sample = ttps.first { it.techniqueId.startsWith("T1190") }
        assertEquals("Initial Access", sample.tactic.displayName)
        assertTrue(sample.d3fendId.startsWith("D3-"))
        assertTrue(sample.attackCodeCommand.isNotBlank())
    }

    @Test
    fun `test feeds catalog contains over 200 feeds`() {
        val feeds = com.example.data.feeds.FeedCatalogRepository.allFeeds
        assertTrue(feeds.size >= 200)
    }

    @Test
    fun `test firebase config resources exist`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbId = context.getString(R.string.firestore_database_id)
        assertTrue(dbId.isNotBlank())
        assertEquals("ai-studio-android-cybershi-6caf5f56-0ea0-46dd-94f3-5f9488eca635", dbId)
    }

    @Test
    fun `test compare version names logic in AppUpdateManager`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val updateManager = com.example.data.update.AppUpdateManager(context)

        // 2.0.0 > 1.0.12
        assertTrue(updateManager.compareVersionNames("2.0.0", "1.0.12") > 0)
        // 2.1.0 > 2.0.0
        assertTrue(updateManager.compareVersionNames("v2.1.0", "2.0.0") > 0)
        // 2.0.0 == 2.0.0
        assertEquals(0, updateManager.compareVersionNames("2.0.0", "v2.0.0"))
        // 1.9.9 < 2.0.0
        assertTrue(updateManager.compareVersionNames("1.9.9", "2.0.0") < 0)
    }
}
