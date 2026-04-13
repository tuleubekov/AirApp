package com.berg.airapp.day5.di

import com.berg.airapp.day5.api.ModelsApi
import com.berg.airapp.day5.data.ModelsRepositoryImpl
import com.berg.airapp.day5.domain.ModelsRepository
import com.berg.airapp.day5.presentation.ModelsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun modelsModule(apiKey: String) = module {
    single { ModelsApi(get(), apiKey) }
    single<ModelsRepository> { ModelsRepositoryImpl(get()) }
    viewModel { ModelsViewModel(get()) }
}
