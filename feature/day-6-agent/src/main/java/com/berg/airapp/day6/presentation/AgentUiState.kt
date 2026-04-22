package com.berg.airapp.day6.presentation

import com.berg.airapp.day6.agent.AgentMessage

enum class MessageRole { USER, ASSISTANT }

data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val content: String
)

data class AgentUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val streamingMessage: String = "",
    val error: String? = null
) {
    fun toAgentMessages(): List<AgentMessage> = messages.map {
        AgentMessage(
            role = if (it.role == MessageRole.USER) "user" else "assistant",
            content = it.content
        )
    }
}
