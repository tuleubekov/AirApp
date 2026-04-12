package com.berg.airapp.day2.di

import com.berg.airapp.day2.api.FormatApi
import com.berg.airapp.day2.data.ComparisonRepositoryImpl
import com.berg.airapp.day2.domain.ComparisonRepository
import com.berg.airapp.day2.presentation.ComparisonViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun comparisonModule(apiKey: String) = module {
    single { FormatApi(get(), apiKey) }
    single<ComparisonRepository> { ComparisonRepositoryImpl(get()) }
    viewModel { ComparisonViewModel(get()) }
}
