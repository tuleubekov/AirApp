package com.berg.airapp.day1.presentation

import com.berg.airapp.day1.domain.Message

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val streamingMessage: String = "",
    val error: String? = null
)
