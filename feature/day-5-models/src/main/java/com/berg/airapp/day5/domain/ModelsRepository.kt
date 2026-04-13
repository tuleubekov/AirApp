package com.berg.airapp.day5.domain

data class ModelCompareResult(
    val text: String,
    val inputTokens: Int,
    val outputTokens: Int,
    val durationMs: Long,
    val costUsd: Double
)

enum class ModelTier {
    HAIKU, SONNET, OPUS
}

interface ModelsRepository {
    suspend fun send(tier: ModelTier, prompt: String): ModelCompareResult
}
