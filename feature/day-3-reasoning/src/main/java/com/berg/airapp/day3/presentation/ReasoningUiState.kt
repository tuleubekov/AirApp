package com.berg.airapp.day3.presentation

const val DEFAULT_TASK =
    "У Анны вдвое больше яблок, чем у Бориса. Борис отдал 3 яблока Анне. " +
    "Теперь у Анны 14 яблок. Сколько яблок было у каждого изначально?"

data class ReasoningUiState(
    val task: String = DEFAULT_TASK,
    val isLoading: Boolean = false,
    val selectedTab: Int = 0,
    // responses
    val directResponse: String = "",
    val stepByStepResponse: String = "",
    val metaResponse: String = "",
    val expertsResponse: String = "",
    // prompts
    val stepByStepInstruction: String = "",
    val metaGeneratedPrompt: String = "",
    val expertsInstruction: String = "",
    val error: String? = null
)
