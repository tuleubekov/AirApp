package com.berg.airapp.presentation.reasoning

import androidx.lifecycle.viewModelScope
import com.berg.airapp.domain.repository.ReasoningRepository
import com.berg.airapp.presentation.base.BaseViewModel
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
                            expertsResponse = result.experts
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
