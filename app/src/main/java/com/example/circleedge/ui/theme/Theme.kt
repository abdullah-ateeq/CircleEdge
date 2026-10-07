package com.example.circleedge.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.circleedge.data.model.AppThemeMode

private val TechnicalDarkColorScheme = darkColorScheme(
    primary = SlateTealDark,
    onPrimary = OnSlateTealDark,
    primaryContainer = SlateTealContainerDark,
    onPrimaryContainer = OnSlateTealContainerDark,
    secondary = SlateSecondaryDark,
    tertiary = SlateTertiaryDark,
    background = TechnicalBackgroundDark,
    surface = TechnicalSurfaceDark,
    surfaceVariant = TechnicalSurfaceVariantDark,
    onBackground = TechnicalOnSurfaceDark,
    onSurface = TechnicalOnSurfaceDark,
    outline = TechnicalOutlineDark
)

private val TechnicalLightColorScheme = lightColorScheme(
    primary = SlateTealLight,
    onPrimary = OnSlateTealLight,
    primaryContainer = SlateTealContainerLight,
    onPrimaryContainer = OnSlateTealContainerLight,
    secondary = SlateSecondaryLight,
    tertiary = SlateTertiaryLight,
    background = TechnicalBackgroundLight,
    surface = TechnicalSurfaceLight,
    surfaceVariant = TechnicalSurfaceVariantLight,
    onBackground = TechnicalOnSurfaceLight,
    onSurface = TechnicalOnSurfaceLight,
    outline = TechnicalOutlineLight
)

@Composable
fun CircleEdgeTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> TechnicalDarkColorScheme
        else -> TechnicalLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}