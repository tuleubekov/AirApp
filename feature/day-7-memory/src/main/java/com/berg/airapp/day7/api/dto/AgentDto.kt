package com.berg.airapp.day7.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgentRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val system: String,
    val messages: List<AgentMessageDto>,
    val stream: Boolean
)

@Serializable
data class AgentMessageDto(
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
