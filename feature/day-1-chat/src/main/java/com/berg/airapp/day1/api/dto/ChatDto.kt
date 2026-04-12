package com.berg.airapp.day1.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val messages: List<ChatMessageDto>,
    val stream: Boolean
)

@Serializable
data class ChatMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class StreamEvent(
    val type: String,
    val delta: StreamDelta? = null
)

@Serializable
data class StreamDelta(
    val type: String,
    val text: String? = null
)
