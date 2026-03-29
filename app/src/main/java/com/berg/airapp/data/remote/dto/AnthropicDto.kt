package com.berg.airapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnthropicRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val messages: List<MessageDto>,
    val stream: Boolean = false,
    val system: String? = null,
    @SerialName("stop_sequences") val stopSequences: List<String>? = null
)

@Serializable
data class MessageDto(
    val role: String,
    val content: String
)

@Serializable
data class AnthropicResponse(
    val id: String,
    val content: List<ContentBlock>,
    val role: String
)

@Serializable
data class ContentBlock(
    val type: String,
    val text: String
)

// Streaming DTOs
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
