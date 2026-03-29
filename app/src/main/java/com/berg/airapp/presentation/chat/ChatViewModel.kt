package com.berg.airapp.presentation.chat

import androidx.lifecycle.viewModelScope
import com.berg.airapp.domain.model.Message
import com.berg.airapp.domain.model.MessageRole
import com.berg.airapp.domain.repository.ChatRepository
import com.berg.airapp.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
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
            repository.streamMessage(uiState.value.messages)
                .onEach { chunk ->
                    updateState {
                        it.copy(
                            streamingMessage = it.streamingMessage + chunk,
                            isLoading = false
                        )
                    }
                }
                .onCompletion { cause ->
                    if (cause == null) {
                        val finalText = uiState.value.streamingMessage
                        if (finalText.isNotEmpty()) {
                            updateState {
                                it.copy(
                                    messages = it.messages + Message(
                                        id = UUID.randomUUID().toString(),
                                        role = MessageRole.ASSISTANT,
                                        content = finalText
                                    ),
                                    streamingMessage = "",
                                    isLoading = false
                                )
                            }
                        }
                    } else {
                        updateState { it.copy(streamingMessage = "", isLoading = false) }
                    }
                }
                .catch { error ->
                    updateState { it.copy(error = error.toMessage()) }
                }
                .collect {}
        }
    }

    fun dismissError() = updateState { it.copy(error = null) }
}
