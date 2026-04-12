package com.berg.airapp.presentation.models

const val DEFAULT_MODELS_PROMPT =
    "Объясни квантовую запутанность простыми словами"

data class ModelTabState(
    val isLoading: Boolean = false,
    val text: String = "",
    val inputTokens: Int = 0,
    val outputTokens: Int = 0,
    val durationMs: Long = 0L,
    val costUsd: Double = 0.0,
    val error: String? = null
) {
    val hasResult: Boolean get() = text.isNotEmpty()
    val hasError: Boolean get() = error != null
}

data class ModelsUiState(
    val prompt: String = DEFAULT_MODELS_PROMPT,
    val selectedTab: Int = 0,
    val haiku: ModelTabState = ModelTabState(),
    val sonnet: ModelTabState = ModelTabState(),
    val opus: ModelTabState = ModelTabState()
) {
    val isAnyLoading: Boolean get() = haiku.isLoading || sonnet.isLoading || opus.isLoading
}
