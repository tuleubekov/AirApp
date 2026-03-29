package com.berg.airapp.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.berg.airapp.domain.model.Message
import com.berg.airapp.domain.model.MessageRole
import com.berg.airapp.presentation.components.ErrorSnackbarEffect
import com.berg.airapp.presentation.components.ErrorSnackbarHost
import com.berg.airapp.ui.theme.AirAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    ChatScreenContent(
        uiState = uiState,
        onInputChanged = viewModel::onInputChanged,
        onSend = viewModel::sendMessage,
        onErrorDismissed = viewModel::dismissError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(
    uiState: ChatUiState,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
    onErrorDismissed: () -> Unit
) {
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    ErrorSnackbarEffect(
        error = uiState.error,
        snackbarHostState = snackbarHostState,
        onDismiss = onErrorDismissed
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("AirApp") }) },
        snackbarHost = { ErrorSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages) { message ->
                    MessageBubble(message = message)
                }
                if (uiState.streamingMessage.isNotEmpty()) {
                    item {
                        MessageBubble(
                            message = Message(
                                id = "streaming",
                                role = MessageRole.ASSISTANT,
                                content = uiState.streamingMessage
                            )
                        )
                    }
                }
                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }

            InputBar(
                text = uiState.inputText,
                onTextChange = onInputChanged,
                onSend = onSend,
                enabled = !uiState.isLoading
            )
        }
    }
}

@Composable
private fun MessageBubble(message: Message) {
    val isUser = message.role == MessageRole.USER
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun InputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Введите сообщение...") },
            shape = RoundedCornerShape(24.dp),
            maxLines = 4
        )
        IconButton(
            onClick = onSend,
            enabled = enabled && text.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Отправить"
            )
        }
    }
}

@Preview(showBackground = true, name = "Chat — empty")
@Composable
private fun ChatScreenEmptyPreview() {
    AirAppTheme {
        ChatScreenContent(
            uiState = ChatUiState(),
            onInputChanged = {},
            onSend = {},
            onErrorDismissed = {}
        )
    }
}

@Preview(showBackground = true, name = "Chat — with messages")
@Composable
private fun ChatScreenWithMessagesPreview() {
    AirAppTheme {
        ChatScreenContent(
            uiState = ChatUiState(
                messages = listOf(
                    Message(id = "1", role = MessageRole.USER, content = "Привет! Как дела?"),
                    Message(id = "2", role = MessageRole.ASSISTANT, content = "Привет! Всё отлично, готов помочь."),
                    Message(id = "3", role = MessageRole.USER, content = "Напиши Hello World на Kotlin"),
                ),
                inputText = "Расскажи подробнее..."
            ),
            onInputChanged = {},
            onSend = {},
            onErrorDismissed = {}
        )
    }
}

@Preview(showBackground = true, name = "Chat — loading")
@Composable
private fun ChatScreenLoadingPreview() {
    AirAppTheme {
        ChatScreenContent(
            uiState = ChatUiState(
                messages = listOf(
                    Message(id = "1", role = MessageRole.USER, content = "Что такое coroutines?")
                ),
                isLoading = true
            ),
            onInputChanged = {},
            onSend = {},
            onErrorDismissed = {}
        )
    }
}
