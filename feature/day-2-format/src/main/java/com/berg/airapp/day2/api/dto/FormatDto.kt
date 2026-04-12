package com.berg.airapp.day2.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FormatRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val messages: List<FormatMessageDto>,
    val system: String? = null,
    @SerialName("stop_sequences") val stopSequences: List<String>? = null
)

@Serializable
data class FormatMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class FormatResponse(
    val content: List<FormatContentBlock>
)

@Serializable
data class FormatContentBlock(
    val type: String,
    val text: String
)
