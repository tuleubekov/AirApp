package com.berg.airapp.day5.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModelsRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val messages: List<ModelsMessageDto>
)

@Serializable
data class ModelsMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class ModelsResponse(
    val content: List<ModelsContentBlock>,
    val usage: UsageDto? = null
)

@Serializable
data class ModelsContentBlock(
    val type: String,
    val text: String = ""
)

@Serializable
data class UsageDto(
    @SerialName("input_tokens") val inputTokens: Int = 0,
    @SerialName("output_tokens") val outputTokens: Int = 0
)
