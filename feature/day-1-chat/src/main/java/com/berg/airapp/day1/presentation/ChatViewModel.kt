package com.berg.airapp.day1.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.berg.airapp.core.presentation.BaseViewModel
import com.berg.airapp.day1.domain.ChatRepository
import com.berg.airapp.day1.domain.Message
import com.berg.airapp.day1.domain.MessageRole
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
            Log.d("ChatViewModel", "▶ Запускаем стриминг, сообщений: ${uiState.value.messages.size}")
            repository.streamMessage(uiState.value.messages)
                .onEach { chunk ->
                    Log.d("ChatViewModel", "📦 Получен чанк: $chunk")
                    updateState {
                        it.copy(
                            streamingMessage = it.streamingMessage + chunk,
                            isLoading = false
                        )
                    }
                }
                .onCompletion { cause ->
                    Log.d("ChatViewModel", "✅ onCompletion, cause=$cause")
                    val finalText = uiState.value.streamingMessage
                    if (cause == null && finalText.isNotEmpty()) {
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
                    } else {
                        updateState { it.copy(streamingMessage = "", isLoading = false) }
                    }
                }
                .catch { error ->
                    Log.e("ChatViewModel", "❌ Ошибка: ${error.message}", error)
                    updateState { it.copy(error = error.toMessage(), isLoading = false) }
                }
                .collect {}
        }
    }

    fun dismissError() = updateState { it.copy(error = null) }
}
