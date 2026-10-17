package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mitre.db.D3fendTacticEntity
import com.example.data.mitre.db.D3fendTechniqueEntity
import com.example.data.mitre.db.DetectionEntity
import com.example.data.mitre.db.MitigationEntity
import com.example.data.mitre.db.MitreRoomRepository
import com.example.data.mitre.db.ProcedureEntity
import com.example.data.mitre.db.TacticEntity
import com.example.data.mitre.db.TechniqueEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TechniqueDetailState(
    val technique: TechniqueEntity,
    val tactics: List<TacticEntity> = emptyList(),
    val subtechniques: List<TechniqueEntity> = emptyList(),
    val procedures: List<ProcedureEntity> = emptyList(),
    val mitigations: List<MitigationEntity> = emptyList(),
    val detections: List<DetectionEntity> = emptyList(),
    val d3fendCountermeasures: List<D3fendTechniqueEntity> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class MitreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MitreRoomRepository(application)

    val selectedDomain = MutableStateFlow("enterprise") // enterprise, mobile, ics, d3fend
    val searchQuery = MutableStateFlow("")
    val selectedPlatformFilter = MutableStateFlow("ALL") // ALL, Windows, Linux, macOS, Cloud, Network
    val selectedTacticFilter = MutableStateFlow<String?>(null) // null = all tactics

    // Sub-technique expanded state map: techniqueId -> Boolean
    val expandedTechniqueIds = MutableStateFlow<Set<String>>(emptySet())

    val selectedTechniqueDetail = MutableStateFlow<TechniqueDetailState?>(null)
    val isDetailLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.initializeIfNeeded()
        }
    }

    fun setDomain(domain: String) {
        selectedDomain.value = domain
        selectedTacticFilter.value = null
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setPlatformFilter(platform: String) {
        selectedPlatformFilter.value = platform
    }

    fun setTacticFilter(tacticShortname: String?) {
        selectedTacticFilter.value = tacticShortname
    }

    fun toggleSubtechniqueExpanded(techniqueId: String) {
        val current = expandedTechniqueIds.value
        expandedTechniqueIds.value = if (current.contains(techniqueId)) current - techniqueId else current + techniqueId
    }

    val tactics: StateFlow<List<TacticEntity>> = selectedDomain.flatMapLatest { domain ->
        repository.getTacticsForDomain(if (domain == "d3fend") "enterprise" else domain)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTechniques: StateFlow<List<TechniqueEntity>> = repository.getAllTechniques().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allD3fendTactics: StateFlow<List<D3fendTacticEntity>> = repository.getD3fendTactics().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allD3fendTechniques: StateFlow<List<D3fendTechniqueEntity>> = repository.getAllD3fendTechniques().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun openTechniqueDetail(techniqueId: String) {
        viewModelScope.launch {
            isDetailLoading.value = true
            try {
                val tech = repository.getTechniqueById(techniqueId)
                if (tech != null) {
                    val realId = tech.id
                    combine(
                        repository.getTacticsForTechnique(realId),
                        repository.getSubtechniques(realId),
                        repository.getProceduresForTechnique(realId),
                        repository.getMitigationsForTechnique(realId),
                        repository.getDetectionsForTechnique(realId),
                        repository.getD3fendCountermeasuresForAttack(realId)
                    ) { flows: Array<Any> ->
                        @Suppress("UNCHECKED_CAST")
                        TechniqueDetailState(
                            technique = tech,
                            tactics = flows[0] as List<TacticEntity>,
                            subtechniques = flows[1] as List<TechniqueEntity>,
                            procedures = flows[2] as List<ProcedureEntity>,
                            mitigations = flows[3] as List<MitigationEntity>,
                            detections = flows[4] as List<DetectionEntity>,
                            d3fendCountermeasures = flows[5] as List<D3fendTechniqueEntity>
                        )
                    }.collect { state ->
                        selectedTechniqueDetail.value = state
                    }
                }
            } finally {
                isDetailLoading.value = false
            }
        }
    }

    fun closeTechniqueDetail() {
        selectedTechniqueDetail.value = null
    }

    fun getTechniquesForTactic(tacticShortname: String): kotlinx.coroutines.flow.Flow<List<TechniqueEntity>> {
        return repository.getTechniquesForTactic(tacticShortname)
    }

    fun getSubtechniques(parentId: String): kotlinx.coroutines.flow.Flow<List<TechniqueEntity>> {
        return repository.getSubtechniques(parentId)
    }
}
