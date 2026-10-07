package com.example.circleedge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.circleedge.data.local.CircleEdgePreferences
import com.example.circleedge.ui.navigation.CircleEdgeNavHost
import com.example.circleedge.ui.theme.CircleEdgeTheme
import com.example.circleedge.ui.viewmodel.CircleEdgeViewModel
import com.example.circleedge.ui.viewmodel.CircleEdgeViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = CircleEdgePreferences(applicationContext)
        val viewModel: CircleEdgeViewModel by viewModels {
            CircleEdgeViewModelFactory(preferences)
        }

        setContent {
            val settings by viewModel.settings.collectAsState()

            CircleEdgeTheme(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor
            ) {
                CircleEdgeNavHost(viewModel = viewModel)
            }
        }
    }
}