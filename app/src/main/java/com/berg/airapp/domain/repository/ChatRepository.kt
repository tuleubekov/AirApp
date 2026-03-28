package com.berg.airapp.domain.repository

import com.berg.airapp.domain.model.Message

interface ChatRepository {
    suspend fun sendMessage(messages: List<Message>): Result<Message>
}
