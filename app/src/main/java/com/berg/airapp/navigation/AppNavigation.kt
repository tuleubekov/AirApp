package com.berg.airapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.berg.airapp.presentation.chat.ChatScreen
import com.berg.airapp.presentation.comparison.ComparisonScreen
import com.berg.airapp.presentation.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onOpenChat = { navController.navigate("chat") },
                onOpenComparison = { navController.navigate("comparison") }
            )
        }
        composable("chat") {
            ChatScreen()
        }
        composable("comparison") {
            ComparisonScreen(onBack = { navController.popBackStack() })
        }
    }
}
