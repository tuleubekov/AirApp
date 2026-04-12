package com.berg.airapp.day2.data

import com.berg.airapp.day2.api.FormatApi
import com.berg.airapp.day2.api.dto.FormatMessageDto
import com.berg.airapp.day2.api.dto.FormatRequest
import com.berg.airapp.day2.domain.ComparisonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ComparisonRepositoryImpl(
    private val api: FormatApi
) : ComparisonRepository {

    override suspend fun compareResponses(prompt: String): Pair<String, String> = coroutineScope {
        val withoutJob = async {
            api.sendMessage(
                FormatRequest(
                    model = "claude-sonnet-4-6",
                    maxTokens = 1024,
                    messages = listOf(FormatMessageDto(role = "user", content = prompt))
                )
            )
        }
        val withJob = async {
            api.sendMessage(
                FormatRequest(
                    model = "claude-sonnet-4-6",
                    maxTokens = 100,
                    messages = listOf(FormatMessageDto(role = "user", content = prompt)),
                    system = "Ответь строго в формате JSON. Используй только поле \"answer\". Когда закончишь — напиши ###",
                    stopSequences = listOf("###")
                )
            )
        }
        Pair(withoutJob.await(), withJob.await())
    }
}
