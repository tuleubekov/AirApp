package com.berg.airapp.day7.presentation

import androidx.lifecycle.viewModelScope
import com.berg.airapp.core.presentation.BaseViewModel
import com.berg.airapp.day7.agent.AgentMessage
import com.berg.airapp.day7.agent.SimpleAgent
import com.berg.airapp.day7.db.MessageStorage
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID

class MemoryViewModel(
    private val agent: SimpleAgent,
    private val storage: MessageStorage
) : BaseViewModel<MemoryUiState>(MemoryUiState()) {

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val history = storage.loadAll()
            updateState {
                it.copy(
                    messages = history.map { msg ->
                        ChatMessage(
                            id = UUID.randomUUID().toString(),
                            role = if (msg.role == "user") MessageRole.USER else MessageRole.ASSISTANT,
                            content = msg.content
                        )
                    }
                )
            }
        }
    }

    fun onInputChanged(text: String) = updateState { it.copy(inputText = text) }

    fun sendMessage() {
        val text = uiState.value.inputText.trim()
        if (text.isBlank() || uiState.value.isLoading) return

        val userMessage = ChatMessage(
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
            storage.save(AgentMessage(role = "user", content = text))

            val history = uiState.value.toAgentMessages()

            agent.run(history)
                .onEach { chunk ->
                    updateState {
                        it.copy(
                            streamingMessage = it.streamingMessage + chunk,
                            isLoading = false
                        )
                    }
                }
                .onCompletion { cause ->
                    val finalText = uiState.value.streamingMessage
                    if (cause == null && finalText.isNotEmpty()) {
                        storage.save(AgentMessage(role = "assistant", content = finalText))
                        updateState {
                            it.copy(
                                messages = it.messages + ChatMessage(
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
                    updateState { it.copy(error = error.toMessage(), isLoading = false) }
                }
                .collect {}
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            storage.clearAll()
            updateState { it.copy(messages = emptyList()) }
        }
    }

    fun dismissError() = updateState { it.copy(error = null) }
}
