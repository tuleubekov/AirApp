package com.berg.airapp.day3.domain

data class ReasoningResult(
    val direct: String,
    val stepByStep: String,
    val stepByStepInstruction: String,
    val meta: String,
    val metaGeneratedPrompt: String,
    val experts: String,
    val expertsInstruction: String
)

interface ReasoningRepository {
    suspend fun solve(task: String): ReasoningResult
}
