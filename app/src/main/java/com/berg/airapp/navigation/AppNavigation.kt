package com.berg.airapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.berg.airapp.presentation.chat.ChatScreen
import com.berg.airapp.presentation.comparison.ComparisonScreen
import com.berg.airapp.presentation.home.HomeScreen
import com.berg.airapp.presentation.models.ModelsScreen
import com.berg.airapp.presentation.reasoning.ReasoningScreen
import com.berg.airapp.presentation.temperature.TemperatureScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onOpenChat = { navController.navigate("chat") },
                onOpenComparison = { navController.navigate("comparison") },
                onOpenReasoning = { navController.navigate("reasoning") },
                onOpenTemperature = { navController.navigate("temperature") },
                onOpenModels = { navController.navigate("models") }
            )
        }
        composable("chat") {
            ChatScreen()
        }
        composable("comparison") {
            ComparisonScreen(onBack = { navController.popBackStack() })
        }
        composable("reasoning") {
            ReasoningScreen(onBack = { navController.popBackStack() })
        }
        composable("temperature") {
            TemperatureScreen(onBack = { navController.popBackStack() })
        }
        composable("models") {
            ModelsScreen(onBack = { navController.popBackStack() })
        }
    }
}
