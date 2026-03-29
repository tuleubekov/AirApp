package com.berg.airapp.data.repository

import com.berg.airapp.data.remote.api.AnthropicApi
import com.berg.airapp.data.remote.dto.AnthropicRequest
import com.berg.airapp.data.remote.dto.MessageDto
import com.berg.airapp.domain.model.Message
import com.berg.airapp.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl(
    private val api: AnthropicApi
) : ChatRepository {

    override fun streamMessage(messages: List<Message>): Flow<String> {
        return api.streamMessage(
            AnthropicRequest(
                model = "claude-sonnet-4-6",
                maxTokens = 1024,
                messages = messages.map {
                    MessageDto(
                        role = it.role.name.lowercase(),
                        content = it.content
                    )
                },
                stream = true
            )
        )
    }
}
