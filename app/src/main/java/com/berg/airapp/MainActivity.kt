package com.berg.airapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.berg.airapp.navigation.AppNavigation
import com.berg.airapp.ui.theme.AirAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AirAppTheme {
                AppNavigation()
            }
        }
    }
}
