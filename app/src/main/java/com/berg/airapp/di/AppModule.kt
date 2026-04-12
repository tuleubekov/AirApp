package com.berg.airapp.di

import com.berg.airapp.BuildConfig
import com.berg.airapp.data.remote.api.AnthropicApi
import com.berg.airapp.data.repository.ComparisonRepositoryImpl
import com.berg.airapp.data.repository.ModelsRepositoryImpl
import com.berg.airapp.data.repository.ReasoningRepositoryImpl
import com.berg.airapp.data.repository.TemperatureRepositoryImpl
import com.berg.airapp.day1.di.chatModule
import com.berg.airapp.domain.repository.ComparisonRepository
import com.berg.airapp.domain.repository.ModelsRepository
import com.berg.airapp.domain.repository.ReasoningRepository
import com.berg.airapp.domain.repository.TemperatureRepository
import com.berg.airapp.presentation.comparison.ComparisonViewModel
import com.berg.airapp.presentation.models.ModelsViewModel
import com.berg.airapp.presentation.reasoning.ReasoningViewModel
import com.berg.airapp.presentation.temperature.TemperatureViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
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

    single { AnthropicApi(get(), get()) }
    single<ComparisonRepository> { ComparisonRepositoryImpl(get()) }
    single<ReasoningRepository> { ReasoningRepositoryImpl(get()) }
    single<TemperatureRepository> { TemperatureRepositoryImpl(get()) }
    single<ModelsRepository> { ModelsRepositoryImpl(get()) }
    viewModel { ComparisonViewModel(get()) }
    viewModel { ReasoningViewModel(get()) }
    viewModel { TemperatureViewModel(get()) }
    viewModel { ModelsViewModel(get()) }
}

val allModules = listOf(
    appModule,
    chatModule(BuildConfig.ANTHROPIC_API_KEY)
)
