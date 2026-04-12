package com.berg.airapp.presentation.models

import androidx.lifecycle.viewModelScope
import com.berg.airapp.domain.repository.ModelTier
import com.berg.airapp.domain.repository.ModelsRepository
import com.berg.airapp.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class ModelsViewModel(
    private val repository: ModelsRepository
) : BaseViewModel<ModelsUiState>(ModelsUiState()) {

    fun onPromptChanged(text: String) = updateState { it.copy(prompt = text) }

    fun onTabSelected(index: Int) = updateState { it.copy(selectedTab = index) }

    fun compare() {
        val prompt = uiState.value.prompt.trim()
        if (prompt.isBlank() || uiState.value.isAnyLoading) return

        // Сбрасываем все вкладки в состояние загрузки
        updateState {
            it.copy(
                haiku = ModelTabState(isLoading = true),
                sonnet = ModelTabState(isLoading = true),
                opus = ModelTabState(isLoading = true)
            )
        }

        // Каждая модель грузится независимо — результат появляется по мере готовности
        viewModelScope.launch {
            launch { fetchModel(ModelTier.HAIKU, prompt) }
            launch { fetchModel(ModelTier.SONNET, prompt) }
            launch { fetchModel(ModelTier.OPUS, prompt) }
        }
    }

    private suspend fun fetchModel(tier: ModelTier, prompt: String) {
        runCatching { repository.send(tier, prompt) }
            .onSuccess { result ->
                val tabState = ModelTabState(
                    text = result.text,
                    inputTokens = result.inputTokens,
                    outputTokens = result.outputTokens,
                    durationMs = result.durationMs,
                    costUsd = result.costUsd
                )
                updateState {
                    when (tier) {
                        ModelTier.HAIKU  -> it.copy(haiku = tabState)
                        ModelTier.SONNET -> it.copy(sonnet = tabState)
                        ModelTier.OPUS   -> it.copy(opus = tabState)
                    }
                }
            }
            .onFailure { error ->
                val tabState = ModelTabState(error = error.toMessage())
                updateState {
                    when (tier) {
                        ModelTier.HAIKU  -> it.copy(haiku = tabState)
                        ModelTier.SONNET -> it.copy(sonnet = tabState)
                        ModelTier.OPUS   -> it.copy(opus = tabState)
                    }
                }
            }
    }
}
