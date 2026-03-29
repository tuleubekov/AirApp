package com.berg.airapp.data.repository

import com.berg.airapp.data.remote.api.AnthropicApi
import com.berg.airapp.data.remote.dto.AnthropicRequest
import com.berg.airapp.data.remote.dto.MessageDto
import com.berg.airapp.domain.repository.ComparisonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ComparisonRepositoryImpl(
    private val api: AnthropicApi
) : ComparisonRepository {

    override suspend fun compareResponses(prompt: String): Pair<String, String> = coroutineScope {
        val withoutJob = async {
            api.sendMessage(
                AnthropicRequest(
                    model = "claude-sonnet-4-6",
                    maxTokens = 1024,
                    messages = listOf(MessageDto(role = "user", content = prompt))
                )
            )
        }
        val withJob = async {
            api.sendMessage(
                AnthropicRequest(
                    model = "claude-sonnet-4-6",
                    maxTokens = 100,
                    messages = listOf(MessageDto(role = "user", content = prompt)),
                    system = "Ответь строго в формате JSON. Используй только поле \"answer\". Когда закончишь — напиши ###",
                    stopSequences = listOf("###")
                )
            )
        }
        Pair(withoutJob.await(), withJob.await())
    }
}
