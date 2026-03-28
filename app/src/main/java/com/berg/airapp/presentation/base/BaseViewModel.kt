package com.berg.airapp.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S>(initialState: S) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    protected fun updateState(update: (S) -> S) {
        _uiState.update(update)
    }

    protected fun Throwable.toMessage(): String = message ?: "Unknown error"
}
