package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val TacticalDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    background = DarkBackground,
    onBackground = CyberTextPrimary,
    surface = DarkSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = DarkOutline,
    outlineVariant = Color(0xFF1E293B),
    error = DarkError,
    onError = DarkOnError
)

private val OledBlackColorScheme = darkColorScheme(
    primary = Color(0xFF22D3EE),
    onPrimary = Color(0xFF00363F),
    primaryContainer = Color(0xFF082F49),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF003822),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = Color(0xFFA78BFA),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFAFAFA),
    surface = Color(0xFF080808),
    onSurface = Color(0xFFFAFAFA),
    surfaceVariant = Color(0xFF141414),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF27272A),
    outlineVariant = Color(0xFF18181B),
    error = SeverityCritical,
    onError = Color.Black
)

private val CyberMatrixColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color(0xFF003822),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = Color(0xFF00FF66),
    onSecondary = Color(0xFF00391A),
    secondaryContainer = Color(0xFF004D25),
    onSecondaryContainer = Color(0xFF8CF5B5),
    tertiary = Color(0xFF2DD4BF),
    background = Color(0xFF030A06),
    onBackground = Color(0xFFE2F8EB),
    surface = Color(0xFF07140B),
    onSurface = Color(0xFFE2F8EB),
    surfaceVariant = Color(0xFF0E2315),
    onSurfaceVariant = Color(0xFF86EFAC),
    outline = Color(0xFF1A3D25),
    outlineVariant = Color(0xFF0C2413),
    error = SeverityCritical,
    onError = Color.Black
)

private val LightAnalystColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF059669),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = Color(0xFF6366F1),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = SeverityCritical,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    themeMode: CyberThemeMode = CyberThemeMode.TACTICAL_DARK,
    content: @Composable () -> Unit,
) {
    val (colorScheme, palette) = when (themeMode) {
        CyberThemeMode.TACTICAL_DARK -> TacticalDarkColorScheme to TacticalDarkPalette
        CyberThemeMode.OLED_BLACK -> OledBlackColorScheme to OledBlackPalette
        CyberThemeMode.CYBER_MATRIX -> CyberMatrixColorScheme to CyberMatrixPalette
        CyberThemeMode.LIGHT_ANALYST -> LightAnalystColorScheme to LightAnalystPalette
    }

    CompositionLocalProvider(LocalCyberPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
