package com.berg.airapp.presentation.models

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.berg.airapp.core.ui.theme.AirAppTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

private data class ModelTab(
    val label: String,
    val emoji: String,
    val name: String,
    val description: String
)

private val TABS = listOf(
    ModelTab("Haiku", "🟢", "claude-haiku-4-5", "Быстрая и дешёвая"),
    ModelTab("Sonnet", "🟡", "claude-sonnet-4-5", "Баланс скорости и качества"),
    ModelTab("Opus", "🔴", "claude-opus-4-5", "Мощная и медленная")
)

@Composable
fun ModelsScreen(
    onBack: () -> Unit,
    viewModel: ModelsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ModelsScreenContent(
        uiState = uiState,
        onPromptChanged = viewModel::onPromptChanged,
        onTabSelected = viewModel::onTabSelected,
        onCompare = viewModel::compare,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsScreenContent(
    uiState: ModelsUiState,
    onPromptChanged: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onCompare: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Версии моделей") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // Поле ввода + кнопка
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                OutlinedTextField(
                    value = uiState.prompt,
                    onValueChange = onPromptChanged,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Введи запрос...") },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4,
                    enabled = !uiState.isAnyLoading
                )
                Button(
                    onClick = onCompare,
                    enabled = !uiState.isAnyLoading && uiState.prompt.isNotBlank(),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Запустить")
                }
            }

            // Табы
            PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
                TABS.forEachIndexed { index, tab ->
                    val tabState = uiState.tabStateAt(index)
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { onTabSelected(index) },
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (tabState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 1.5.dp
                                    )
                                } else {
                                    Text(tab.emoji)
                                }
                                Text(tab.label)
                            }
                        }
                    )
                }
            }

            // Содержимое выбранного таба
            val currentTab = TABS[uiState.selectedTab]
            val currentState = uiState.tabStateAt(uiState.selectedTab)

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Карточка с инфо о модели
                    ModelInfoCard(tab = currentTab)

                    when {
                        currentState.isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            }
                        }

                        currentState.hasError -> {
                            Text(
                                text = currentState.error ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        currentState.hasResult -> {
                            // Метрики
                            MetricsCard(state = currentState)

                            // Ответ модели
                            Text(
                                text = currentState.text,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        else -> {
                            Text(
                                text = "Введи запрос и нажми «Запустить»",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelInfoCard(tab: ModelTab) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${tab.emoji} ${tab.name}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = tab.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun MetricsCard(state: ModelTabState) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricItem(
                label = "⏱ Время",
                value = formatDuration(state.durationMs)
            )
            MetricItem(
                label = "📊 Токены",
                value = "${state.inputTokens}+${state.outputTokens}"
            )
            MetricItem(
                label = "💰 Цена",
                value = formatCost(state.costUsd)
            )
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatDuration(ms: Long): String = when {
    ms < 1000 -> "${ms} мс"
    else -> String.format(Locale.US, "%.1f с", ms / 1000.0)
}

private fun formatCost(usd: Double): String = when {
    usd < 0.000001 -> "< $0.000001"
    usd < 0.01 -> String.format(Locale.US, "$%.6f", usd)
    else -> String.format(Locale.US, "$%.4f", usd)
}

private fun ModelsUiState.tabStateAt(index: Int): ModelTabState = when (index) {
    0 -> haiku
    1 -> sonnet
    2 -> opus
    else -> haiku
}

// --- Previews ---

@Preview(showBackground = true, name = "Models — empty")
@Composable
private fun ModelsEmptyPreview() {
    AirAppTheme {
        ModelsScreenContent(
            uiState = ModelsUiState(),
            onPromptChanged = {},
            onTabSelected = {},
            onCompare = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Models — with result")
@Composable
private fun ModelsWithResultPreview() {
    AirAppTheme {
        ModelsScreenContent(
            uiState = ModelsUiState(
                selectedTab = 0,
                haiku = ModelTabState(
                    text = "Квантовая запутанность — это когда две частицы связаны так, что измерение одной мгновенно определяет состояние другой.",
                    inputTokens = 12,
                    outputTokens = 48,
                    durationMs = 820,
                    costUsd = 0.000204
                )
            ),
            onPromptChanged = {},
            onTabSelected = {},
            onCompare = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Models — loading")
@Composable
private fun ModelsLoadingPreview() {
    AirAppTheme {
        ModelsScreenContent(
            uiState = ModelsUiState(
                haiku = ModelTabState(isLoading = true),
                sonnet = ModelTabState(isLoading = true),
                opus = ModelTabState(isLoading = true)
            ),
            onPromptChanged = {},
            onTabSelected = {},
            onCompare = {},
            onBack = {}
        )
    }
}
