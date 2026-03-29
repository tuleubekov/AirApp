package com.berg.airapp.domain.repository

interface ComparisonRepository {
    suspend fun compareResponses(prompt: String): Pair<String, String>
}
