package com.example.circleedge.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Splash : Screen("splash", "Splash", Icons.Default.Circle)
    object Calculator : Screen("calculator", "Calculator", Icons.Default.Calculate)
    object History : Screen("history", "History", Icons.Default.History)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object About : Screen("about", "About", Icons.Default.Info)
}

val BottomNavScreens = listOf(
    Screen.Calculator,
    Screen.History,
    Screen.Settings,
    Screen.About
)
