package com.example.checkmyfridge.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = grey,
    primaryContainer = darkGrey,
    secondaryContainer = deepGrey,
    background = black,
    onBackground = white,
    onSurface = white,
    onPrimary = black,
)

private val LightColorScheme = lightColorScheme(
    primary = darkBlue,
    primaryContainer = blue,
    secondaryContainer = lightBlue,
    background = white,
    onBackground = black,
    onSurface = black,
)

private val WoodColorScheme = lightColorScheme(
    primary = lightBrown,
    primaryContainer = brown,
    secondaryContainer = cream,
    background = white,
    onBackground = black,
    onSurface = black,
)

@Composable
fun CheckMyFridgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, themeIndex: Int,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeIndex == 1 -> LightColorScheme
        themeIndex == 2 -> WoodColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}