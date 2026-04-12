package com.berg.airapp.day2.presentation

import androidx.lifecycle.viewModelScope
import com.berg.airapp.core.presentation.BaseViewModel
import com.berg.airapp.day2.domain.ComparisonRepository
import kotlinx.coroutines.launch

class ComparisonViewModel(
    private val repository: ComparisonRepository
) : BaseViewModel<ComparisonUiState>(ComparisonUiState()) {

    fun onPromptChanged(text: String) = updateState { it.copy(prompt = text) }

    fun compare() {
        val prompt = uiState.value.prompt.trim()
        if (prompt.isBlank() || uiState.value.isLoading) return

        updateState {
            it.copy(isLoading = true, responseWithout = "", responseWith = "", error = null)
        }

        viewModelScope.launch {
            runCatching { repository.compareResponses(prompt) }
                .onSuccess { (without, with) ->
                    updateState {
                        it.copy(isLoading = false, responseWithout = without, responseWith = with)
                    }
                }
                .onFailure { error ->
                    updateState { it.copy(isLoading = false, error = error.toMessage()) }
                }
        }
    }

    fun dismissError() = updateState { it.copy(error = null) }
}
