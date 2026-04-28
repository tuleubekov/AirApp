package com.berg.airapp.day7.agent

import com.berg.airapp.day7.api.AgentApi
import com.berg.airapp.day7.api.dto.AgentMessageDto
import kotlinx.coroutines.flow.Flow

data class AgentMessage(
    val role: String,
    val content: String
)

class SimpleAgent(private val api: AgentApi) {

    private val systemPrompt = """
        Ты — Aria, умный и дружелюбный ассистент.
        Отвечай чётко и по делу. Используй простой язык.
        Если не знаешь ответа — честно скажи об этом.
    """.trimIndent()

    private val model = "claude-haiku-4-5"
    private val maxTokens = 1024

    fun run(messages: List<AgentMessage>): Flow<String> = api.stream(
        systemPrompt = systemPrompt,
        messages = messages.map { AgentMessageDto(role = it.role, content = it.content) },
        model = model,
        maxTokens = maxTokens
    )
}
