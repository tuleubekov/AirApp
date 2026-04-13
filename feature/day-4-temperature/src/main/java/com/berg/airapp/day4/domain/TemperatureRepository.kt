package com.berg.airapp.day4.domain

data class TemperatureResult(
    val cold: String,      // temperature = 0.0
    val medium: String,    // temperature = 0.5
    val hot: String        // temperature = 1.0
)

interface TemperatureRepository {
    suspend fun compare(prompt: String): TemperatureResult
}
