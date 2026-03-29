package com.berg.airapp.presentation.temperature

import androidx.lifecycle.viewModelScope
import com.berg.airapp.domain.repository.TemperatureRepository
import com.berg.airapp.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class TemperatureViewModel(
    private val repository: TemperatureRepository
) : BaseViewModel<TemperatureUiState>(TemperatureUiState()) {

    fun onPromptChanged(text: String) = updateState { it.copy(prompt = text) }

    fun onTabSelected(index: Int) = updateState { it.copy(selectedTab = index) }

    fun compare() {
        val prompt = uiState.value.prompt.trim()
        if (prompt.isBlank() || uiState.value.isLoading) return

        updateState {
            it.copy(
                isLoading = true,
                coldResponse = "",
                mediumResponse = "",
                hotResponse = "",
                error = null
            )
        }

        viewModelScope.launch {
            runCatching { repository.compare(prompt) }
                .onSuccess { result ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            coldResponse = result.cold,
                            mediumResponse = result.medium,
                            hotResponse = result.hot
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
