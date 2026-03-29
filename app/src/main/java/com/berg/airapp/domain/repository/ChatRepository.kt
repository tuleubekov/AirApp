package com.berg.airapp.domain.repository

import com.berg.airapp.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun streamMessage(messages: List<Message>): Flow<String>
}
