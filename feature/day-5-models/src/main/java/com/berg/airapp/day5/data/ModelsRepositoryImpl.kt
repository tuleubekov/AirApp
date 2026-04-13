package com.berg.airapp.day5.data

import com.berg.airapp.day5.api.ModelsApi
import com.berg.airapp.day5.api.dto.ModelsMessageDto
import com.berg.airapp.day5.api.dto.ModelsRequest
import com.berg.airapp.day5.domain.ModelCompareResult
import com.berg.airapp.day5.domain.ModelTier
import com.berg.airapp.day5.domain.ModelsRepository

class ModelsRepositoryImpl(
    private val api: ModelsApi
) : ModelsRepository {

    override suspend fun send(tier: ModelTier, prompt: String): ModelCompareResult {
        val config = MODEL_CONFIGS[tier]!!
        val startMs = System.currentTimeMillis()

        val response = api.sendMessage(
            ModelsRequest(
                model = config.modelId,
                maxTokens = 1024,
                messages = listOf(ModelsMessageDto(role = "user", content = prompt))
            )
        )

        val durationMs = System.currentTimeMillis() - startMs
        val inputTokens = response.usage?.inputTokens ?: 0
        val outputTokens = response.usage?.outputTokens ?: 0
        val costUsd = (inputTokens * config.inputPricePer1M + outputTokens * config.outputPricePer1M) / 1_000_000.0

        return ModelCompareResult(
            text = response.content.firstOrNull()?.text ?: "",
            inputTokens = inputTokens,
            outputTokens = outputTokens,
            durationMs = durationMs,
            costUsd = costUsd
        )
    }

    private data class ModelConfig(
        val modelId: String,
        val inputPricePer1M: Double,
        val outputPricePer1M: Double
    )

    companion object {
        // Цены в USD за 1 млн токенов
        private val MODEL_CONFIGS = mapOf(
            ModelTier.HAIKU  to ModelConfig("claude-haiku-4-5",  inputPricePer1M = 0.80,  outputPricePer1M = 4.00),
            ModelTier.SONNET to ModelConfig("claude-sonnet-4-5", inputPricePer1M = 3.00,  outputPricePer1M = 15.00),
            ModelTier.OPUS   to ModelConfig("claude-opus-4-5",   inputPricePer1M = 15.00, outputPricePer1M = 75.00)
        )
    }
}
