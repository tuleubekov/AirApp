package com.berg.airapp.data.repository

import com.berg.airapp.data.remote.api.AnthropicApi
import com.berg.airapp.data.remote.dto.AnthropicRequest
import com.berg.airapp.data.remote.dto.MessageDto
import com.berg.airapp.domain.repository.ReasoningRepository
import com.berg.airapp.domain.repository.ReasoningResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ReasoningRepositoryImpl(
    private val api: AnthropicApi
) : ReasoningRepository {

    override suspend fun solve(task: String): ReasoningResult = coroutineScope {
        val directJob = async {
            api.sendMessage(
                AnthropicRequest(
                    model = MODEL,
                    maxTokens = 1024,
                    messages = listOf(MessageDto(role = "user", content = task))
                )
            )
        }

        val stepByStepInstruction = "Решай пошагово, объясняя каждый шаг."
        val stepByStepJob = async {
            api.sendMessage(
                AnthropicRequest(
                    model = MODEL,
                    maxTokens = 1024,
                    messages = listOf(
                        MessageDto(
                            role = "user",
                            content = "$task\n\n$stepByStepInstruction"
                        )
                    )
                )
            )
        }

        val metaJob = async {
            val generatedPrompt = api.sendMessage(
                AnthropicRequest(
                    model = MODEL,
                    maxTokens = 512,
                    messages = listOf(
                        MessageDto(
                            role = "user",
                            content = "Составь промпт для решения следующей задачи. Верни только промпт, без пояснений:\n\n$task"
                        )
                    )
                )
            )
            val answer = api.sendMessage(
                AnthropicRequest(
                    model = MODEL,
                    maxTokens = 1024,
                    messages = listOf(MessageDto(role = "user", content = generatedPrompt))
                )
            )
            Pair(generatedPrompt, answer)
        }

        val expertsInstruction = """
            Реши эту задачу от лица трёх экспертов. Каждый эксперт даёт своё решение:

            🔍 Аналитик — разбирает условие и строит уравнение
            ⚙️ Инженер — решает практично и напрямую
            🎯 Критик — проверяет ответ и указывает на возможные ошибки

            Каждый эксперт выступает отдельно.
        """.trimIndent()

        val expertsJob = async {
            api.sendMessage(
                AnthropicRequest(
                    model = MODEL,
                    maxTokens = 1024,
                    messages = listOf(
                        MessageDto(
                            role = "user",
                            content = "Задача: $task\n\n$expertsInstruction"
                        )
                    )
                )
            )
        }

        val (metaGeneratedPrompt, metaAnswer) = metaJob.await()

        ReasoningResult(
            direct = directJob.await(),
            stepByStep = stepByStepJob.await(),
            stepByStepInstruction = stepByStepInstruction,
            meta = metaAnswer,
            metaGeneratedPrompt = metaGeneratedPrompt,
            experts = expertsJob.await(),
            expertsInstruction = expertsInstruction
        )
    }

    companion object {
        private const val MODEL = "claude-sonnet-4-6"
    }
}
