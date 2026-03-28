package com.berg.airapp.presentation.chat

import androidx.lifecycle.viewModelScope
import com.berg.airapp.domain.model.Message
import com.berg.airapp.domain.model.MessageRole
import com.berg.airapp.domain.repository.ChatRepository
import com.berg.airapp.presentation.base.BaseViewModel
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(
    private val repository: ChatRepository
) : BaseViewModel<ChatUiState>(ChatUiState()) {

    fun onInputChanged(text: String) = updateState { it.copy(inputText = text) }

    fun sendMessage() {
        val text = uiState.value.inputText.trim()
        if (text.isBlank() || uiState.value.isLoading) return

        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = text
        )

        updateState {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            repository.sendMessage(uiState.value.messages)
                .onSuccess { response ->
                    updateState {
                        it.copy(
                            messages = it.messages + response,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    updateState { it.copy(isLoading = false, error = error.toMessage()) }
                }
        }
    }

    fun dismissError() = updateState { it.copy(error = null) }
}
