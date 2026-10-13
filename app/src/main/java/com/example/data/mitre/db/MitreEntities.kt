package com.example.data.mitre.db

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "mitre_tactics")
data class TacticEntity(
    @PrimaryKey val id: String,
    val name: String,
    val shortname: String,
    val domain: String, // enterprise, mobile, ics
    val description: String
)

@Entity(tableName = "mitre_techniques")
data class TechniqueEntity(
    @PrimaryKey val id: String, // T1134 or T1134.001
    val name: String,
    val domain: String, // enterprise, mobile, ics
    val isSubtechnique: Boolean,
    val parentTechniqueId: String?,
    val platforms: String, // comma separated
    val dataSources: String, // comma separated
    val description: String,
    val url: String
)

@Entity(tableName = "mitre_technique_tactic_cross_ref", primaryKeys = ["techniqueId", "tacticShortname"])
data class TechniqueTacticCrossRef(
    val techniqueId: String,
    val tacticShortname: String
)

@Entity(tableName = "mitre_procedures")
data class ProcedureEntity(
    @PrimaryKey val id: String,
    val techniqueId: String,
    val sourceType: String, // group, software, campaign
    val sourceId: String,
    val sourceName: String,
    val description: String
)

@Entity(tableName = "mitre_mitigations")
data class MitigationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val techniqueIds: String // comma separated
)

@Entity(tableName = "mitre_detections")
data class DetectionEntity(
    @PrimaryKey val id: String,
    val techniqueId: String,
    val strategy: String,
    val analytics: String, // json or pipe separated
    val dataComponents: String
)

@Entity(tableName = "mitre_d3fend_tactics")
data class D3fendTacticEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String
)

@Entity(tableName = "mitre_d3fend_techniques")
data class D3fendTechniqueEntity(
    @PrimaryKey val id: String,
    val name: String,
    val tacticId: String,
    val tacticName: String,
    val description: String,
    val artifacts: String, // comma separated
    val counteredAttackIds: String, // comma separated
    val ruleSnippet: String
)

@Entity(tableName = "mitre_redirects")
data class RedirectEntity(
    @PrimaryKey val oldId: String,
    val targetId: String
)

@Fts4(contentEntity = TechniqueEntity::class)
@Entity(tableName = "mitre_techniques_fts")
data class TechniqueFtsEntity(
    val id: String,
    val name: String,
    val description: String,
    val platforms: String
)
