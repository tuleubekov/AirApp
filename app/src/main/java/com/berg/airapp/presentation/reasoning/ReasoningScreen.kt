package com.berg.airapp.presentation.reasoning

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.berg.airapp.presentation.components.ErrorSnackbarEffect
import com.berg.airapp.presentation.components.ErrorSnackbarHost
import com.berg.airapp.ui.theme.AirAppTheme
import org.koin.androidx.compose.koinViewModel

private val TABS = listOf("Direct", "Пошагово", "Мета", "Эксперты")

@Composable
fun ReasoningScreen(
    onBack: () -> Unit,
    viewModel: ReasoningViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ReasoningScreenContent(
        uiState = uiState,
        onTaskChanged = viewModel::onTaskChanged,
        onTabSelected = viewModel::onTabSelected,
        onSolve = viewModel::solve,
        onErrorDismissed = viewModel::dismissError,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReasoningScreenContent(
    uiState: ReasoningUiState,
    onTaskChanged: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onSolve: () -> Unit,
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
                title = { Text("Способы рассуждения") },
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
        ) {
            // Input + button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                OutlinedTextField(
                    value = uiState.task,
                    onValueChange = onTaskChanged,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Введи задачу...") },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4,
                    enabled = !uiState.isLoading
                )
                Button(
                    onClick = onSolve,
                    enabled = !uiState.isLoading && uiState.task.isNotBlank(),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Решить")
                }
            }

            // Tabs
            PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
                TABS.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { onTabSelected(index) },
                        text = { Text(title) }
                    )
                }
            }

            // Tab content
            val currentResponse = when (uiState.selectedTab) {
                0 -> uiState.directResponse
                1 -> uiState.stepByStepResponse
                2 -> uiState.metaResponse
                3 -> uiState.expertsResponse
                else -> ""
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }
                    currentResponse.isNotEmpty() -> {
                        Text(
                            text = currentResponse,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                    else -> {
                        Text(
                            text = "Введи задачу и нажми «Решить»",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Reasoning — empty")
@Composable
private fun ReasoningEmptyPreview() {
    AirAppTheme {
        ReasoningScreenContent(
            uiState = ReasoningUiState(),
            onTaskChanged = {},
            onTabSelected = {},
            onSolve = {},
            onErrorDismissed = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Reasoning — with response")
@Composable
private fun ReasoningWithResponsePreview() {
    AirAppTheme {
        ReasoningScreenContent(
            uiState = ReasoningUiState(
                selectedTab = 1,
                directResponse = "У Анны было 8 яблок, у Бориса — 4.",
                stepByStepResponse = "Шаг 1: Обозначим количество яблок у Бориса как x.\nШаг 2: У Анны вдвое больше — 2x.\nШаг 3: После передачи 3 яблок: Анна = 2x + 3 = 14.\nШаг 4: 2x = 11, x = 5.5 — нет целого решения, пересмотрим...",
                metaResponse = "",
                expertsResponse = ""
            ),
            onTaskChanged = {},
            onTabSelected = {},
            onSolve = {},
            onErrorDismissed = {},
            onBack = {}
        )
    }
}
