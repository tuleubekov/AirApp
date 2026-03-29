package com.berg.airapp.presentation.comparison

data class ComparisonUiState(
    val prompt: String = "",
    val isLoading: Boolean = false,
    val responseWithout: String = "",
    val responseWith: String = "",
    val error: String? = null
)
