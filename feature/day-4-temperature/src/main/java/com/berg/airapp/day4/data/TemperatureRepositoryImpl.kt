package com.berg.airapp.day4.data

import com.berg.airapp.day4.api.TemperatureApi
import com.berg.airapp.day4.api.dto.TemperatureMessageDto
import com.berg.airapp.day4.api.dto.TemperatureRequest
import com.berg.airapp.day4.domain.TemperatureRepository
import com.berg.airapp.day4.domain.TemperatureResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class TemperatureRepositoryImpl(
    private val api: TemperatureApi
) : TemperatureRepository {

    override suspend fun compare(prompt: String): TemperatureResult = coroutineScope {
        val coldJob = async {
            api.sendMessage(
                TemperatureRequest(
                    model = MODEL,
                    maxTokens = 1024,
                    messages = listOf(TemperatureMessageDto(role = "user", content = prompt)),
                    temperature = 0.0
                )
            )
        }
        val mediumJob = async {
            api.sendMessage(
                TemperatureRequest(
                    model = MODEL,
                    maxTokens = 1024,
                    messages = listOf(TemperatureMessageDto(role = "user", content = prompt)),
                    temperature = 0.5
                )
            )
        }
        val hotJob = async {
            api.sendMessage(
                TemperatureRequest(
                    model = MODEL,
                    maxTokens = 1024,
                    messages = listOf(TemperatureMessageDto(role = "user", content = prompt)),
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
