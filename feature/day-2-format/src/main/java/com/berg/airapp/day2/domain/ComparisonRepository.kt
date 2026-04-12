package com.berg.airapp.day2.domain

interface ComparisonRepository {
    suspend fun compareResponses(prompt: String): Pair<String, String>
}
