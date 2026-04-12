package com.berg.airapp.day3.di

import com.berg.airapp.day3.api.ReasoningApi
import com.berg.airapp.day3.data.ReasoningRepositoryImpl
import com.berg.airapp.day3.domain.ReasoningRepository
import com.berg.airapp.day3.presentation.ReasoningViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun reasoningModule(apiKey: String) = module {
    single { ReasoningApi(get(), apiKey) }
    single<ReasoningRepository> { ReasoningRepositoryImpl(get()) }
    viewModel { ReasoningViewModel(get()) }
}
