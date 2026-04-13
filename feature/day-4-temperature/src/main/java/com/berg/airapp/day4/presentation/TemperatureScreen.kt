package com.berg.airapp.day4.presentation

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
import com.berg.airapp.core.presentation.ErrorSnackbarEffect
import com.berg.airapp.core.presentation.ErrorSnackbarHost
import com.berg.airapp.core.ui.theme.AirAppTheme
import org.koin.androidx.compose.koinViewModel

private data class TempTab(val label: String, val value: String, val description: String)

private val TABS = listOf(
    TempTab("t = 0", "0.0", "Детерминированный. Один и тот же ответ каждый раз. Лучше для фактов и точных задач."),
    TempTab("t = 0.5", "0.5", "Баланс. Немного вариативности при сохранении связности. Хорош для большинства задач."),
    TempTab("t = 1.0", "1.0", "Максимальная случайность. Творческий, непредсказуемый. Лучше для генерации идей.")
)

@Composable
fun TemperatureScreen(
    onBack: () -> Unit,
    viewModel: TemperatureViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    TemperatureScreenContent(
        uiState = uiState,
        onPromptChanged = viewModel::onPromptChanged,
        onTabSelected = viewModel::onTabSelected,
        onCompare = viewModel::compare,
        onErrorDismissed = viewModel::dismissError,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureScreenContent(
    uiState: TemperatureUiState,
    onPromptChanged: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
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
                title = { Text("Температура") },
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
                    value = uiState.prompt,
                    onValueChange = onPromptChanged,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Введи запрос...") },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4,
                    enabled = !uiState.isLoading
                )
                Button(
                    onClick = onCompare,
                    enabled = !uiState.isLoading && uiState.prompt.isNotBlank(),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Сравнить")
                }
            }

            PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
                TABS.forEachIndexed { index, tab ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { onTabSelected(index) },
                        text = { Text(tab.label) }
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
                        val tab = TABS[uiState.selectedTab]
                        val response = when (uiState.selectedTab) {
                            0 -> uiState.coldResponse
                            1 -> uiState.mediumResponse
                            2 -> uiState.hotResponse
                            else -> ""
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TemperatureInfoCard(tab.value, tab.description)

                            if (response.isNotEmpty()) {
                                Text(
                                    text = response,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = "Введи запрос и нажми «Сравнить»",
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
}

@Composable
private fun TemperatureInfoCard(value: String, description: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "temperature = $value",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Temperature — empty")
@Composable
private fun TemperatureEmptyPreview() {
    AirAppTheme {
        TemperatureScreenContent(
            uiState = TemperatureUiState(),
            onPromptChanged = {},
            onTabSelected = {},
            onCompare = {},
            onErrorDismissed = {},
            onBack = {}
        )
    }
}
