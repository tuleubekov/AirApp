package com.berg.airapp.day1.data

import com.berg.airapp.day1.api.ChatApi
import com.berg.airapp.day1.api.dto.ChatMessageDto
import com.berg.airapp.day1.api.dto.ChatRequest
import com.berg.airapp.day1.domain.ChatRepository
import com.berg.airapp.day1.domain.Message
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl(
    private val api: ChatApi
) : ChatRepository {

    override fun streamMessage(messages: List<Message>): Flow<String> {
        return api.streamMessage(
            ChatRequest(
                model = "claude-sonnet-4-6",
                maxTokens = 1024,
                messages = messages.map {
                    ChatMessageDto(
                        role = it.role.name.lowercase(),
                        content = it.content
                    )
                },
                stream = true
            )
        )
    }
}
