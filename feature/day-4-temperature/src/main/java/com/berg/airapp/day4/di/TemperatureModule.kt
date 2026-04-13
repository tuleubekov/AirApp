package com.berg.airapp.day4.di

import com.berg.airapp.day4.api.TemperatureApi
import com.berg.airapp.day4.data.TemperatureRepositoryImpl
import com.berg.airapp.day4.domain.TemperatureRepository
import com.berg.airapp.day4.presentation.TemperatureViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun temperatureModule(apiKey: String) = module {
    single { TemperatureApi(get(), apiKey) }
    single<TemperatureRepository> { TemperatureRepositoryImpl(get()) }
    viewModel { TemperatureViewModel(get()) }
}
