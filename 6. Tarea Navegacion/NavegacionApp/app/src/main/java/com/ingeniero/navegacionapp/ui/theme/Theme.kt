package com.ingeniero.navegacionapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun NavegacionAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary    = DuoGreen,
            onPrimary  = White,
            background = Background,
            surface    = White,
            onBackground = TextPrimary,
            onSurface    = TextPrimary
        ),
        typography = Typography,
        content    = content
    )
}
