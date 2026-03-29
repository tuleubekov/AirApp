package com.berg.airapp.presentation.reasoning

const val DEFAULT_TASK =
    "У Анны вдвое больше яблок, чем у Бориса. Борис отдал 3 яблока Анне. " +
    "Теперь у Анны 14 яблок. Сколько яблок было у каждого изначально?"

data class ReasoningUiState(
    val task: String = DEFAULT_TASK,
    val isLoading: Boolean = false,
    val selectedTab: Int = 0,
    val directResponse: String = "",
    val stepByStepResponse: String = "",
    val metaResponse: String = "",
    val expertsResponse: String = "",
    val error: String? = null
)
