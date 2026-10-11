package com.example.data.learning

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SecurityAlertExample(
    val id: String,
    val title: String,
    val severity: String,
    val source: String,
    val description: String,
    val detectionLogic: String,
    val investigationSteps: List<String>,
    val remediationSteps: List<String>,
    val mitreTactic: String? = null,
    val mitreTechniqueId: String? = null,
    val logsNeeded: List<String> = emptyList()
)
