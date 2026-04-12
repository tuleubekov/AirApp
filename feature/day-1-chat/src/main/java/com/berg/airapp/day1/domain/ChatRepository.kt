package com.berg.airapp.day1.domain

import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun streamMessage(messages: List<Message>): Flow<String>
}
