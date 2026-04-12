package com.berg.airapp.day3.presentation

import androidx.lifecycle.viewModelScope
import com.berg.airapp.core.presentation.BaseViewModel
import com.berg.airapp.day3.domain.ReasoningRepository
import kotlinx.coroutines.launch

class ReasoningViewModel(
    private val repository: ReasoningRepository
) : BaseViewModel<ReasoningUiState>(ReasoningUiState()) {

    fun onTaskChanged(text: String) = updateState { it.copy(task = text) }

    fun onTabSelected(index: Int) = updateState { it.copy(selectedTab = index) }

    fun solve() {
        val task = uiState.value.task.trim()
        if (task.isBlank() || uiState.value.isLoading) return

        updateState {
            it.copy(
                isLoading = true,
                directResponse = "",
                stepByStepResponse = "",
                metaResponse = "",
                expertsResponse = "",
                stepByStepInstruction = "",
                metaGeneratedPrompt = "",
                expertsInstruction = "",
                error = null
            )
        }

        viewModelScope.launch {
            runCatching { repository.solve(task) }
                .onSuccess { result ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            directResponse = result.direct,
                            stepByStepResponse = result.stepByStep,
                            metaResponse = result.meta,
                            expertsResponse = result.experts,
                            stepByStepInstruction = result.stepByStepInstruction,
                            metaGeneratedPrompt = result.metaGeneratedPrompt,
                            expertsInstruction = result.expertsInstruction
                        )
                    }
                }
                .onFailure { error ->
                    updateState { it.copy(isLoading = false, error = error.toMessage()) }
                }
        }
    }

    fun dismissError() = updateState { it.copy(error = null) }
}
