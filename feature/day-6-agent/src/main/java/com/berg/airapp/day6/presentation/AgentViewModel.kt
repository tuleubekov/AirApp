package com.berg.airapp.day6.presentation

import androidx.lifecycle.viewModelScope
import com.berg.airapp.core.presentation.BaseViewModel
import com.berg.airapp.day6.agent.AgentMessage
import com.berg.airapp.day6.agent.SimpleAgent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID

class AgentViewModel(
    private val agent: SimpleAgent
) : BaseViewModel<AgentUiState>(AgentUiState()) {

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

        val history: List<AgentMessage> = uiState.value.toAgentMessages()

        viewModelScope.launch {
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

    fun dismissError() = updateState { it.copy(error = null) }
}
