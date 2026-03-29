package com.berg.airapp.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.berg.airapp.ui.theme.AirAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenChat: () -> Unit,
    onOpenComparison: () -> Unit,
    onOpenReasoning: () -> Unit,
    onOpenTemperature: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("AirApp") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Привет!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Выбери режим работы",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = onOpenChat,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Чат")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onOpenComparison,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("День 2: Формат ответа")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onOpenReasoning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("День 3: Способы рассуждения")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onOpenTemperature,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("День 4: Температура")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    AirAppTheme {
        HomeScreen(
            onOpenChat = {},
            onOpenComparison = {},
            onOpenReasoning = {},
            onOpenTemperature = {}
        )
    }
}
