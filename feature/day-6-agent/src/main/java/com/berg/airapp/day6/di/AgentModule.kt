package com.berg.airapp.day6.di

import com.berg.airapp.day6.agent.SimpleAgent
import com.berg.airapp.day6.api.AgentApi
import com.berg.airapp.day6.presentation.AgentViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun agentModule(apiKey: String) = module {
    single { AgentApi(get(), get(), apiKey) }
    single { SimpleAgent(get()) }
    viewModel { AgentViewModel(get()) }
}
