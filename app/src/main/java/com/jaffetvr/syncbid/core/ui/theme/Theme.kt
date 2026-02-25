package com.jaffetvr.syncbid.core.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SyncBidColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Black,
    primaryContainer = GoldSubtle,
    onPrimaryContainer = GoldLight,
    secondary = Green,
    onSecondary = Black,
    secondaryContainer = GreenSubtle,
    onSecondaryContainer = GreenLight,
    tertiary = Blue,
    error = Red,
    onError = White,
    errorContainer = RedSubtle,
    onErrorContainer = RedLight,
    background = Black,
    onBackground = White90,
    surface = Black2,
    onSurface = White90,
    surfaceVariant = Black3,
    onSurfaceVariant = White60,
    outline = White12,
    outlineVariant = White06
)

@Composable
fun SyncBidTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Black.toArgb()
            window.navigationBarColor = Black.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = SyncBidColorScheme,
        typography = Typography,
        content = content
    )
}