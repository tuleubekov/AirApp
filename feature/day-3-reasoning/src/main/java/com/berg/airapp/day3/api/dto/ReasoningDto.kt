package com.berg.airapp.day3.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReasoningRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val messages: List<ReasoningMessageDto>
)

@Serializable
data class ReasoningMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class ReasoningResponse(
    val content: List<ReasoningContentBlock>
)

@Serializable
data class ReasoningContentBlock(
    val type: String,
    val text: String = ""
)
