package com.berg.airapp.day7.di

import androidx.room.Room
import com.berg.airapp.day7.agent.SimpleAgent
import com.berg.airapp.day7.api.AgentApi
import com.berg.airapp.day7.db.AppDatabase
import com.berg.airapp.day7.db.MessageStorage
import com.berg.airapp.day7.db.MessageStorageImpl
import com.berg.airapp.day7.presentation.MemoryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun memoryModule(apiKey: String) = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "day7_memory.db"
        ).build()
    }

    single { get<AppDatabase>().messageDao() }
    single<MessageStorage> { MessageStorageImpl(get()) }
    single { AgentApi(get(), get(), apiKey) }
    single { SimpleAgent(get()) }
    viewModel { MemoryViewModel(get(), get()) }
}
