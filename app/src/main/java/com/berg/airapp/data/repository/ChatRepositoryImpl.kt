package com.berg.airapp.data.repository

import com.berg.airapp.data.remote.api.AnthropicApi
import com.berg.airapp.data.remote.dto.AnthropicRequest
import com.berg.airapp.data.remote.dto.MessageDto
import com.berg.airapp.domain.model.Message
import com.berg.airapp.domain.model.MessageRole
import com.berg.airapp.domain.repository.ChatRepository
import java.util.UUID

class ChatRepositoryImpl(
    private val api: AnthropicApi
) : ChatRepository {

    override suspend fun sendMessage(messages: List<Message>): Result<Message> {
        return runCatching {
            val response = api.sendMessage(
                AnthropicRequest(
                    model = "claude-sonnet-4-6",
                    maxTokens = 1024,
                    messages = messages.map {
                        MessageDto(
                            role = it.role.name.lowercase(),
                            content = it.content
                        )
                    }
                )
            )
            Message(
                id = UUID.randomUUID().toString(),
                role = MessageRole.ASSISTANT,
                content = response.content.first().text
            )
        }
    }
}
