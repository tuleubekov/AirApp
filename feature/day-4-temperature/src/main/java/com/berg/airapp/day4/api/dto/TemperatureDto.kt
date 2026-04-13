package com.berg.airapp.day4.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TemperatureRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val messages: List<TemperatureMessageDto>,
    val temperature: Double? = null
)

@Serializable
data class TemperatureMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class TemperatureResponse(
    val content: List<TemperatureContentBlock>
)

@Serializable
data class TemperatureContentBlock(
    val type: String,
    val text: String = ""
)
