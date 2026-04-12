package com.berg.airapp.day1.di

import com.berg.airapp.day1.api.ChatApi
import com.berg.airapp.day1.data.ChatRepositoryImpl
import com.berg.airapp.day1.domain.ChatRepository
import com.berg.airapp.day1.presentation.ChatViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun chatModule(apiKey: String) = module {
    // HttpClient и Json берём из appModule — они уже настроены там
    single { ChatApi(get(), get(), apiKey) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    viewModel { ChatViewModel(get()) }
}
