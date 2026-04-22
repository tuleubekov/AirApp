package com.berg.airapp.di

import com.berg.airapp.BuildConfig
import com.berg.airapp.day1.di.chatModule
import com.berg.airapp.day2.di.comparisonModule
import com.berg.airapp.day3.di.reasoningModule
import com.berg.airapp.day4.di.temperatureModule
import com.berg.airapp.day5.di.modelsModule
import com.berg.airapp.day6.di.agentModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single<HttpClient> {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(Logging) {
                level = LogLevel.BODY
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 120_000
            }
        }
    }
}

val allModules = listOf(
    appModule,
    chatModule(BuildConfig.ANTHROPIC_API_KEY),
    comparisonModule(BuildConfig.ANTHROPIC_API_KEY),
    reasoningModule(BuildConfig.ANTHROPIC_API_KEY),
    temperatureModule(BuildConfig.ANTHROPIC_API_KEY),
    modelsModule(BuildConfig.ANTHROPIC_API_KEY),
    agentModule(BuildConfig.ANTHROPIC_API_KEY)
)
