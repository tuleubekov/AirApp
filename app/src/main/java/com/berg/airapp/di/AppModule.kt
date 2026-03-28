package com.berg.airapp.di

import com.berg.airapp.data.remote.api.AnthropicApi
import com.berg.airapp.data.repository.ChatRepositoryImpl
import com.berg.airapp.domain.repository.ChatRepository
import com.berg.airapp.presentation.chat.ChatViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<HttpClient> {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }

    single { AnthropicApi(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    viewModel { ChatViewModel(get()) }
}
