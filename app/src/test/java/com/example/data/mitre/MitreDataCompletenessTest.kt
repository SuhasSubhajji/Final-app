package com.example.data.mitre

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MitreDataCompletenessTest {

    @Test
    fun testMitreAssetCompletenessAndCounts() {
        val assetFile = File("src/main/assets/mitre_attack_d3fend.json")
        assertTrue("Asset file mitre_attack_d3fend.json should exist!", assetFile.exists())

        val jsonString = assetFile.readText()
        val root = JSONObject(jsonString)

        // 1. Metadata check
        val meta = root.getJSONObject("metadata")
        assertNotNull("Version should be present in metadata", meta.getString("version"))

        // 2. Tactics count check
        val tacticsArr = root.getJSONArray("tactics")
        val enterpriseTactics = (0 until tacticsArr.length())
            .map { tacticsArr.getJSONObject(it) }
            .filter { it.optString("domain") == "enterprise" }

        assertTrue(
            "Enterprise tactics count (${enterpriseTactics.size}) must be at least 15 for ATT&CK v19+",
            enterpriseTactics.size >= 15
        )

        val d3fendTacticsArr = root.getJSONArray("d3fendTactics")
        assertEquals(
            "D3FEND tactics count must equal 7",
            7,
            d3fendTacticsArr.length()
        )

        // 3. Technique multi-tactic and non-empty tactic checks
        val techniquesArr = root.getJSONArray("techniques")
        var foundMultiTacticTechnique = false

        for (i in 0 until techniquesArr.length()) {
            val tech = techniquesArr.getJSONObject(i)
            val techId = tech.getString("id")
            val tactics = tech.getJSONArray("tactics")
            assertTrue("Technique $techId must have at least one tactic mapped!", tactics.length() > 0)

            if (tactics.length() > 1) {
                foundMultiTacticTechnique = true
            }
        }

        assertTrue("Dataset must include multi-tactic techniques (e.g. T1134 sitting under Stealth & Privilege Escalation)", foundMultiTacticTechnique)

        // 4. Redirect table check
        val redirects = root.getJSONObject("redirects")
        assertTrue("Redirect table must contain mapping for revoked/deprecated ID T1562", redirects.has("T1562"))
    }
}
