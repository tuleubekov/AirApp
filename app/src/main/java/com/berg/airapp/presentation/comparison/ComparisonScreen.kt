package com.berg.airapp.presentation.comparison

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.berg.airapp.presentation.components.ErrorSnackbarEffect
import com.berg.airapp.presentation.components.ErrorSnackbarHost
import com.berg.airapp.ui.theme.AirAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ComparisonScreen(
    onBack: () -> Unit,
    viewModel: ComparisonViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ComparisonScreenContent(
        uiState = uiState,
        onPromptChanged = viewModel::onPromptChanged,
        onCompare = viewModel::compare,
        onErrorDismissed = viewModel::dismissError,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreenContent(
    uiState: ComparisonUiState,
    onPromptChanged: (String) -> Unit,
    onCompare: () -> Unit,
    onErrorDismissed: () -> Unit,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    ErrorSnackbarEffect(
        error = uiState.error,
        snackbarHostState = snackbarHostState,
        onDismiss = onErrorDismissed
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Comparison") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        snackbarHost = { ErrorSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.prompt,
                    onValueChange = onPromptChanged,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Введи вопрос...") },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    enabled = !uiState.isLoading
                )
                Button(
                    onClick = onCompare,
                    enabled = !uiState.isLoading && uiState.prompt.isNotBlank()
                ) {
                    Text("Сравнить")
                }
            }

            ResponseCard(
                title = "Без ограничений",
                subtitle = null,
                isLoading = uiState.isLoading,
                response = uiState.responseWithout
            )

            ResponseCard(
                title = "С ограничениями",
                subtitle = "max_tokens: 100 · stop: [\"###\"] · system: JSON",
                isLoading = uiState.isLoading,
                response = uiState.responseWith
            )
        }
    }
}

@Composable
private fun ResponseCard(
    title: String,
    subtitle: String?,
    isLoading: Boolean,
    response: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
                response.isNotEmpty() -> {
                    Text(
                        text = response,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    Text(
                        text = "Ответ появится здесь...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Comparison — empty")
@Composable
private fun ComparisonEmptyPreview() {
    AirAppTheme {
        ComparisonScreenContent(
            uiState = ComparisonUiState(),
            onPromptChanged = {},
            onCompare = {},
            onErrorDismissed = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Comparison — with responses")
@Composable
private fun ComparisonWithResponsesPreview() {
    AirAppTheme {
        ComparisonScreenContent(
            uiState = ComparisonUiState(
                prompt = "Что такое корутины?",
                responseWithout = "Корутины — это легковесные потоки выполнения в Kotlin, которые позволяют писать асинхронный код в синхронном стиле.",
                responseWith = """{"answer": "Корутины — это легковесные потоки."}"""
            ),
            onPromptChanged = {},
            onCompare = {},
            onErrorDismissed = {},
            onBack = {}
        )
    }
}
