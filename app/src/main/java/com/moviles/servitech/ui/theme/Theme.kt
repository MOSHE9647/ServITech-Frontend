package com.moviles.servitech.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

val LightColorScheme = lightColorScheme(
    // Main Colors
    primary = AppColors.PrimaryButtonBgLight,
    onPrimary = AppColors.OnPrimaryButtonLight,
    primaryContainer = AppColors.PrimaryButtonBgLight.copy(alpha = 0.8f),

    // Secondary Colors
    secondary = AppColors.SecondaryButtonBgLight,
    onSecondary = AppColors.TextPrimaryLight,
    secondaryContainer = AppColors.SecondaryButtonBgLight.copy(alpha = 0.8f),

    // Background and Surface
    background = AppColors.BackgroundLight,
    surface = AppColors.BackgroundLight,
    onBackground = AppColors.TextPrimaryLight,
    onSurface = AppColors.TextPrimaryLight,
    surfaceVariant = AppColors.SecondaryButtonBgLight,

    // Borders
    outline = AppColors.CardBorderLight,
    outlineVariant = AppColors.SecondaryButtonBorderLight,

    // Errors
    error = AppColors.ErrorLight,
    onError = AppColors.OnErrorLight,
    errorContainer = AppColors.ErrorContainerLight,
    onErrorContainer = AppColors.OnErrorContainerLight
)

val DarkColorScheme = darkColorScheme(
    // Main colors (inverted)
    primary = AppColors.PrimaryButtonBgDark,
    onPrimary = AppColors.OnPrimaryButtonDark,
    primaryContainer = AppColors.PrimaryButtonBgDark.copy(alpha = 0.8f),

    // Secondary colors
    secondary = AppColors.SecondaryButtonBgDark,
    onSecondary = AppColors.TextPrimaryDark,
    secondaryContainer = AppColors.SecondaryButtonBgDark.copy(alpha = 0.8f),

    // Background and surface
    background = AppColors.BackgroundDark,
    surface = AppColors.BackgroundDark,
    onBackground = AppColors.TextPrimaryDark,
    onSurface = AppColors.TextPrimaryDark,
    surfaceVariant = AppColors.SecondaryButtonBgDark,

    // Borders
    outline = AppColors.CardBorderDark,
    outlineVariant = AppColors.SecondaryButtonBorderDark,

    // Errors
    error = AppColors.ErrorDark,
    onError = AppColors.OnErrorDark,
    errorContainer = AppColors.ErrorContainerDark,
    onErrorContainer = AppColors.OnErrorContainerDark
)

@Composable
fun ServITechTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}