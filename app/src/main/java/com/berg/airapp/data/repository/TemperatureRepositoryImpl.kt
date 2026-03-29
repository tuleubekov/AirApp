package com.berg.airapp.data.repository

import com.berg.airapp.data.remote.api.AnthropicApi
import com.berg.airapp.data.remote.dto.AnthropicRequest
import com.berg.airapp.data.remote.dto.MessageDto
import com.berg.airapp.domain.repository.TemperatureRepository
import com.berg.airapp.domain.repository.TemperatureResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class TemperatureRepositoryImpl(
    private val api: AnthropicApi
) : TemperatureRepository {

    override suspend fun compare(prompt: String): TemperatureResult = coroutineScope {
        val coldJob = async {
            api.sendMessage(
                AnthropicRequest(
                    model = MODEL,
                    maxTokens = 1024,
                    messages = listOf(MessageDto(role = "user", content = prompt)),
                    temperature = 0.0
                )
            )
        }
        val mediumJob = async {
            api.sendMessage(
                AnthropicRequest(
                    model = MODEL,
                    maxTokens = 1024,
                    messages = listOf(MessageDto(role = "user", content = prompt)),
                    temperature = 0.5
                )
            )
        }
        val hotJob = async {
            api.sendMessage(
                AnthropicRequest(
                    model = MODEL,
                    maxTokens = 1024,
                    messages = listOf(MessageDto(role = "user", content = prompt)),
                    temperature = 1.0
                )
            )
        }
        TemperatureResult(
            cold = coldJob.await(),
            medium = mediumJob.await(),
            hot = hotJob.await()
        )
    }

    companion object {
        private const val MODEL = "claude-sonnet-4-6"
    }
}
