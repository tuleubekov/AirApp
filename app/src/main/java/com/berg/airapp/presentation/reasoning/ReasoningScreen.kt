package com.berg.airapp.presentation.reasoning

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.berg.airapp.core.presentation.ErrorSnackbarEffect
import com.berg.airapp.core.presentation.ErrorSnackbarHost
import com.berg.airapp.core.ui.theme.AirAppTheme
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

            PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
                TABS.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { onTabSelected(index) },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.Center)
                        )
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            when (uiState.selectedTab) {
                                0 -> DirectTabContent(uiState.directResponse)
                                1 -> StepByStepTabContent(
                                    instruction = uiState.stepByStepInstruction,
                                    response = uiState.stepByStepResponse
                                )
                                2 -> MetaTabContent(
                                    generatedPrompt = uiState.metaGeneratedPrompt,
                                    response = uiState.metaResponse
                                )
                                3 -> ExpertsTabContent(
                                    instruction = uiState.expertsInstruction,
                                    response = uiState.expertsResponse
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Tab contents ──────────────────────────────────────────────────────────────

@Composable
private fun DirectTabContent(response: String) {
    PromptCard(title = "Промпт") {
        Text(
            text = "Задача отправляется без дополнительных инструкций",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    ResponseText(response)
}

@Composable
private fun StepByStepTabContent(instruction: String, response: String) {
    if (instruction.isNotEmpty()) {
        PromptCard(title = "Добавленная инструкция") {
            MonoText(instruction)
        }
    }
    ResponseText(response)
}

@Composable
private fun MetaTabContent(generatedPrompt: String, response: String) {
    PromptCard(title = "Шаг 1 — Запрос на генерацию промпта") {
        MonoText("Составь промпт для решения следующей задачи.\nВерни только промпт, без пояснений:\n\n[задача из поля выше]")
    }
    if (generatedPrompt.isNotEmpty()) {
        PromptCard(title = "Шаг 2 — Сгенерированный промпт ✨") {
            MonoText(generatedPrompt)
        }
    }
    ResponseText(response)
}

@Composable
private fun ExpertsTabContent(instruction: String, response: String) {
    if (instruction.isNotEmpty()) {
        PromptCard(title = "Инструкция для экспертов") {
            MonoText(instruction)
        }
    }
    ResponseText(response)
}

// ── Reusable components ───────────────────────────────────────────────────────

@Composable
private fun PromptCard(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(true) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Свернуть" else "Развернуть",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                    Box(modifier = Modifier.padding(12.dp)) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
private fun MonoText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
        color = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
private fun ResponseText(response: String) {
    if (response.isEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Нажми «Решить», чтобы получить ответ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    } else {
        Text(
            text = response,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

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

@Preview(showBackground = true, name = "Reasoning — meta tab with response")
@Composable
private fun ReasoningMetaPreview() {
    AirAppTheme {
        ReasoningScreenContent(
            uiState = ReasoningUiState(
                selectedTab = 2,
                metaGeneratedPrompt = "Реши следующую математическую задачу, используя систему уравнений. Обозначь неизвестные переменными и найди их значения.",
                metaResponse = "Пусть у Бориса x яблок. Тогда у Анны 2x яблок.\nПосле передачи: Анна = 2x + 3 = 14 → x = 5.5\n\nПересматриваем условие..."
            ),
            onTaskChanged = {},
            onTabSelected = {},
            onSolve = {},
            onErrorDismissed = {},
            onBack = {}
        )
    }
}
