package com.berg.airapp.core.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp

@Composable
fun ErrorSnackbarEffect(
    error: String?,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(error) {
        error?.let {
            keyboardController?.hide()
            snackbarHostState.showSnackbar(it)
            onDismiss()
        }
    }
}

@Composable
fun ErrorSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(
        hostState = hostState,
        modifier = Modifier.padding(bottom = 80.dp)
    )
}
