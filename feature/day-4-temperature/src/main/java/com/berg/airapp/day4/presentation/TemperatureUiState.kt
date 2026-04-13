package com.berg.airapp.day4.presentation

const val DEFAULT_TEMPERATURE_PROMPT =
    "Придумай название для кофейни в стиле киберпанк"

data class TemperatureUiState(
    val prompt: String = DEFAULT_TEMPERATURE_PROMPT,
    val isLoading: Boolean = false,
    val selectedTab: Int = 0,
    val coldResponse: String = "",
    val mediumResponse: String = "",
    val hotResponse: String = "",
    val error: String? = null
)
