package com.berg.airapp.di

import com.berg.airapp.data.remote.api.AnthropicApi
import com.berg.airapp.data.repository.ChatRepositoryImpl
import com.berg.airapp.data.repository.ComparisonRepositoryImpl
import com.berg.airapp.data.repository.ReasoningRepositoryImpl
import com.berg.airapp.domain.repository.ChatRepository
import com.berg.airapp.domain.repository.ComparisonRepository
import com.berg.airapp.domain.repository.ReasoningRepository
import com.berg.airapp.presentation.chat.ChatViewModel
import com.berg.airapp.presentation.comparison.ComparisonViewModel
import com.berg.airapp.presentation.reasoning.ReasoningViewModel
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
                requestTimeoutMillis = 120_000  // 2 минуты на весь запрос
                connectTimeoutMillis = 15_000   // 15 сек на коннект
                socketTimeoutMillis = 120_000   // 2 минуты на чтение ответа
            }
        }
    }

    single { AnthropicApi(get(), get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    single<ComparisonRepository> { ComparisonRepositoryImpl(get()) }
    single<ReasoningRepository> { ReasoningRepositoryImpl(get()) }
    viewModel { ChatViewModel(get()) }
    viewModel { ComparisonViewModel(get()) }
    viewModel { ReasoningViewModel(get()) }
}
