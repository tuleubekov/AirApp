package com.berg.airapp.domain.repository

data class ReasoningResult(
    val direct: String,
    val stepByStep: String,
    val meta: String,
    val experts: String
)

interface ReasoningRepository {
    suspend fun solve(task: String): ReasoningResult
}
