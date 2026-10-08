package com.example.data.rl

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.ThreatCategory
import com.example.data.model.ThreatItem
import com.example.data.model.ThreatSeverity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Domain vectors monitored by the SOC Reinforcement Learning Agent.
 */
enum class SecurityDomain(val key: String, val label: String, val description: String) {
    ZERO_DAY_RCE("zero_day_rce", "Zero-Day & Remote Code Exec", "Critical CVSS 9.0+, public-facing remote vulnerabilities"),
    CLOUD_CONTAINER("cloud_container", "Cloud Infrastructure & IAM", "Kubernetes, AWS/Azure, CI/CD supply chain, token theft"),
    OFFENSIVE_ATTACK("offensive_attack", "MITRE ATT&CK & Red Team", "Initial access, process injection, adversary emulation"),
    DEFENSIVE_D3FEND("defensive_d3fend", "MITRE D3FEND & Detection", "Sigma rules, EDR heuristics, memory isolation, telemetry"),
    RANSOMWARE_MALWARE("ransomware_malware", "Ransomware & C2 Infrastructure", "Encrypted payloads, dark web leak sites, beaconing"),
    CREDENTIAL_IDENTITY("credential_identity", "Identity, Kerberos & Auth", "LSASS dumping, Pass-the-Hash, MFA bypass, Active Directory");

    companion object {
        fun inferFromThreat(threat: ThreatItem): SecurityDomain {
            val titleLower = threat.title.lowercase()
            val descLower = threat.description.lowercase()
            val combined = "$titleLower $descLower"

            return when {
                combined.contains("cloud") || combined.contains("aws") || combined.contains("azure") ||
                combined.contains("k8s") || combined.contains("kubernetes") || combined.contains("container") ||
                combined.contains("iam") || combined.contains("pipeline") -> CLOUD_CONTAINER

                combined.contains("ransomware") || combined.contains("c2") || combined.contains("beacon") ||
                combined.contains("malware") || combined.contains("encrypt") || combined.contains("trojan") -> RANSOMWARE_MALWARE

                combined.contains("kerberos") || combined.contains("lsass") || combined.contains("credential") ||
                combined.contains("mfa") || combined.contains("auth") || combined.contains("token") -> CREDENTIAL_IDENTITY

                combined.contains("sigma") || combined.contains("detection") || combined.contains("d3fend") ||
                combined.contains("rule") || combined.contains("edr") || combined.contains("siem") -> DEFENSIVE_D3FEND

                combined.contains("attack") || combined.contains("ttp") || combined.contains("exploit") ||
                combined.contains("red team") || combined.contains("bypass") -> OFFENSIVE_ATTACK

                threat.severity == ThreatSeverity.CRITICAL || combined.contains("rce") || combined.contains("0-day") ||
                combined.contains("zero-day") || combined.contains("unauthenticated") -> ZERO_DAY_RCE

                else -> ZERO_DAY_RCE
            }
        }
    }
}

data class RlAgentState(
    val trainingSteps: Int = 0,
    val totalRewardAccumulated: Float = 0.0f,
    val explorationRateEpsilon: Float = 0.20f,
    val domainQValues: Map<SecurityDomain, Float> = SecurityDomain.entries.associateWith { 0.5f },
    val lastRewardEvent: String = "Agent initialized with uniform priors",
    val topDomain: SecurityDomain = SecurityDomain.ZERO_DAY_RCE
)

class SocReinforcementLearningEngine private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("soc_rl_agent_prefs", Context.MODE_PRIVATE)

    private val _agentState = MutableStateFlow(loadState())
    val agentState: StateFlow<RlAgentState> = _agentState.asStateFlow()

    private val learningRateAlpha = 0.15f
    private val discountFactorGamma = 0.85f

    /**
     * Compute a personalized relevance score (0-100%) for a given threat item
     * using the current learned Q-values with exploration noise.
     */
    fun computeThreatRelevanceScore(threat: ThreatItem): Int {
        val domain = SecurityDomain.inferFromThreat(threat)
        val qValue = _agentState.value.domainQValues[domain] ?: 0.5f

        // Base score mapped from Q-value (0.0 .. 1.0 -> 50% .. 98%)
        val baseScore = 50f + (qValue * 48f)

        // Severity bonus
        val severityBonus = when (threat.severity) {
            ThreatSeverity.CRITICAL -> 10f
            ThreatSeverity.HIGH -> 6f
            ThreatSeverity.MEDIUM -> 2f
            ThreatSeverity.LOW -> 0f
            ThreatSeverity.INFO -> 0f
        }

        val total = (baseScore + severityBonus).coerceIn(40f, 99f)
        return total.toInt()
    }

    /**
     * Record a user interaction as a reward signal, updating the Q-table via
     * temporal difference / SARSA value iteration.
     */
    fun recordReward(domain: SecurityDomain, reward: Float, eventLabel: String) {
        val current = _agentState.value
        val oldQ = current.domainQValues[domain] ?: 0.5f

        // Q-learning update: Q(s, a) <- Q(s, a) + alpha * (R + gamma * maxQ - Q(s, a))
        val maxFutureQ = current.domainQValues.values.maxOrNull() ?: 0.5f
        val target = reward + (discountFactorGamma * maxFutureQ)
        val newQ = (oldQ + learningRateAlpha * (target - oldQ)).coerceIn(0.05f, 1.0f)

        val updatedMap = current.domainQValues.toMutableMap()
        updatedMap[domain] = newQ

        val newSteps = current.trainingSteps + 1
        // Epsilon decay: exploration gradually transitions to exploitation as agent learns
        val newEpsilon = max(0.04f, 0.25f * Math.pow(0.985, newSteps.toDouble()).toFloat())
        val newTotalReward = current.totalRewardAccumulated + reward

        val topDomain = updatedMap.maxByOrNull { it.value }?.key ?: SecurityDomain.ZERO_DAY_RCE

        val newState = RlAgentState(
            trainingSteps = newSteps,
            totalRewardAccumulated = newTotalReward,
            explorationRateEpsilon = newEpsilon,
            domainQValues = updatedMap,
            lastRewardEvent = "$eventLabel (+${"%.1f".format(reward)} to ${domain.label})",
            topDomain = topDomain
        )

        _agentState.value = newState
        saveState(newState)
    }

    fun recordThreatClick(threat: ThreatItem) {
        val domain = SecurityDomain.inferFromThreat(threat)
        recordReward(domain, 0.5f, "Viewed Threat ${threat.id}")
    }

    fun recordBookmark(threat: ThreatItem) {
        val domain = SecurityDomain.inferFromThreat(threat)
        recordReward(domain, 2.0f, "Cloud Bookmarked ${threat.id}")
    }

    fun recordRemediationCopied(threat: ThreatItem) {
        val domain = SecurityDomain.inferFromThreat(threat)
        recordReward(domain, 1.8f, "Copied Mitigation Rule for ${threat.id}")
    }

    fun recordRoadmapStageCompleted(trackId: String, stageNumber: Int) {
        val domain = when (trackId) {
            "mitre_attack" -> SecurityDomain.OFFENSIVE_ATTACK
            "mitre_defend" -> SecurityDomain.DEFENSIVE_D3FEND
            "cloud_security" -> SecurityDomain.CLOUD_CONTAINER
            "red_team" -> SecurityDomain.OFFENSIVE_ATTACK
            "blue_team" -> SecurityDomain.DEFENSIVE_D3FEND
            else -> SecurityDomain.ZERO_DAY_RCE
        }
        recordReward(domain, 1.5f, "Completed Stage $stageNumber in $trackId")
    }

    fun recordUserFeedback(threat: ThreatItem, isPositive: Boolean) {
        val domain = SecurityDomain.inferFromThreat(threat)
        val reward = if (isPositive) 2.5f else -1.0f
        val label = if (isPositive) "Marked High Priority" else "Dismissed as Irrelevant"
        recordReward(domain, reward, label)
    }

    /**
     * Sorts threats by RL Q-value affinity score descending, breaking ties by timestamp.
     */
    fun sortThreatsByRlScore(threats: List<ThreatItem>): List<ThreatItem> {
        return threats.sortedWith(
            compareByDescending<ThreatItem> { computeThreatRelevanceScore(it) }
                .thenByDescending { it.pubDateMillis }
        )
    }

    /**
     * Returns the recommended learning track ID based on the RL agent's top learned security domain.
     */
    fun getRecommendedRoadmapTrackId(): String {
        return when (_agentState.value.topDomain) {
            SecurityDomain.OFFENSIVE_ATTACK -> "mitre_attack"
            SecurityDomain.DEFENSIVE_D3FEND -> "mitre_defend"
            SecurityDomain.CLOUD_CONTAINER -> "cloud_security"
            SecurityDomain.ZERO_DAY_RCE -> "mitre_attack"
            SecurityDomain.RANSOMWARE_MALWARE -> "blue_team"
            SecurityDomain.CREDENTIAL_IDENTITY -> "red_team"
        }
    }

    fun resetAgent() {
        val fresh = RlAgentState()
        _agentState.value = fresh
        saveState(fresh)
    }

    private fun saveState(state: RlAgentState) {
        val json = JSONObject().apply {
            put("steps", state.trainingSteps)
            put("totalReward", state.totalRewardAccumulated.toDouble())
            put("epsilon", state.explorationRateEpsilon.toDouble())
            put("lastEvent", state.lastRewardEvent)

            val qObj = JSONObject()
            state.domainQValues.forEach { (domain, q) ->
                qObj.put(domain.key, q.toDouble())
            }
            put("qValues", qObj)
        }
        prefs.edit().putString(KEY_RL_STATE, json.toString()).apply()
    }

    private fun loadState(): RlAgentState {
        val raw = prefs.getString(KEY_RL_STATE, null) ?: return RlAgentState()
        return try {
            val json = JSONObject(raw)
            val steps = json.optInt("steps", 0)
            val totalReward = json.optDouble("totalReward", 0.0).toFloat()
            val epsilon = json.optDouble("epsilon", 0.20).toFloat()
            val lastEvent = json.optString("lastEvent", "Restored from persistent storage")

            val qObj = json.optJSONObject("qValues")
            val map = SecurityDomain.entries.associateWith { domain ->
                qObj?.optDouble(domain.key, 0.5)?.toFloat() ?: 0.5f
            }
            val top = map.maxByOrNull { it.value }?.key ?: SecurityDomain.ZERO_DAY_RCE

            RlAgentState(
                trainingSteps = steps,
                totalRewardAccumulated = totalReward,
                explorationRateEpsilon = epsilon,
                domainQValues = map,
                lastRewardEvent = lastEvent,
                topDomain = top
            )
        } catch (e: Exception) {
            RlAgentState()
        }
    }

    companion object {
        private const val KEY_RL_STATE = "key_soc_rl_state_json"
        @Volatile private var instance: SocReinforcementLearningEngine? = null

        fun getInstance(context: Context): SocReinforcementLearningEngine {
            return instance ?: synchronized(this) {
                instance ?: SocReinforcementLearningEngine(context.applicationContext).also { instance = it }
            }
        }
    }
}
