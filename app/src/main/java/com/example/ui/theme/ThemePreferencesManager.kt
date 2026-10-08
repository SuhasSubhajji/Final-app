package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CyberThemeMode(
    val id: String,
    val displayName: String,
    val description: String,
    val isDark: Boolean
) {
    TACTICAL_DARK(
        id = "tactical_dark",
        displayName = "Tactical SOC Dark",
        description = "Midnight navy with anti-glare cyan & emerald for 24/7 SOC monitoring",
        isDark = true
    ),
    OLED_BLACK(
        id = "oled_black",
        displayName = "Pitch Black OLED",
        description = "Pure black (#000000) for night shifts, zero OLED pixel glow & max battery",
        isDark = true
    ),
    CYBER_MATRIX(
        id = "cyber_matrix",
        displayName = "Matrix Terminal",
        description = "Obsidian black with tactical terminal green for command center monitoring",
        isDark = true
    ),
    LIGHT_ANALYST(
        id = "light_analyst",
        displayName = "Day Analyst Light",
        description = "Crisp high-contrast slate & azure for well-lit office environments",
        isDark = false
    );

    companion object {
        fun fromId(id: String?): CyberThemeMode {
            return entries.firstOrNull { it.id == id } ?: TACTICAL_DARK
        }
    }
}

data class CyberPalette(
    val bg: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val isDark: Boolean
)

val TacticalDarkPalette = CyberPalette(
    bg = Color(0xFF0B0F19),
    surface = Color(0xFF111827),
    surfaceVariant = Color(0xFF1E293B),
    border = Color(0xFF334155),
    textPrimary = Color(0xFFF1F5F9),
    textSecondary = Color(0xFF94A3B8),
    textMuted = Color(0xFF64748B),
    accentPrimary = Color(0xFF00D2FF),
    accentSecondary = Color(0xFF10B981),
    isDark = true
)

val OledBlackPalette = CyberPalette(
    bg = Color(0xFF000000),
    surface = Color(0xFF080808),
    surfaceVariant = Color(0xFF121212),
    border = Color(0xFF27272A),
    textPrimary = Color(0xFFFAFAFA),
    textSecondary = Color(0xFFA1A1AA),
    textMuted = Color(0xFF71717A),
    accentPrimary = Color(0xFF22D3EE),
    accentSecondary = Color(0xFF34D399),
    isDark = true
)

val CyberMatrixPalette = CyberPalette(
    bg = Color(0xFF030A06),
    surface = Color(0xFF07140B),
    surfaceVariant = Color(0xFF0E2315),
    border = Color(0xFF1A3D25),
    textPrimary = Color(0xFFE2F8EB),
    textSecondary = Color(0xFF86EFAC),
    textMuted = Color(0xFF4ADE80).copy(alpha = 0.6f),
    accentPrimary = Color(0xFF10B981),
    accentSecondary = Color(0xFF00FF66),
    isDark = true
)

val LightAnalystPalette = CyberPalette(
    bg = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F5F9),
    border = Color(0xFFE2E8F0),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textMuted = Color(0xFF94A3B8),
    accentPrimary = Color(0xFF0284C7),
    accentSecondary = Color(0xFF059669),
    isDark = false
)

val LocalCyberPalette = staticCompositionLocalOf { TacticalDarkPalette }

object CyberTheme {
    val colors: CyberPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalCyberPalette.current
}

class ThemePreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("cybershield_theme_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        CyberThemeMode.fromId(prefs.getString(KEY_THEME_MODE, CyberThemeMode.TACTICAL_DARK.id))
    )
    val themeMode: StateFlow<CyberThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: CyberThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.id).apply()
        _themeMode.value = mode
    }

    fun toggleNextMode() {
        val next = when (_themeMode.value) {
            CyberThemeMode.TACTICAL_DARK -> CyberThemeMode.OLED_BLACK
            CyberThemeMode.OLED_BLACK -> CyberThemeMode.CYBER_MATRIX
            CyberThemeMode.CYBER_MATRIX -> CyberThemeMode.LIGHT_ANALYST
            CyberThemeMode.LIGHT_ANALYST -> CyberThemeMode.TACTICAL_DARK
        }
        setThemeMode(next)
    }

    companion object {
        private const val KEY_THEME_MODE = "key_theme_mode"
        @Volatile private var instance: ThemePreferencesManager? = null

        fun getInstance(context: Context): ThemePreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: ThemePreferencesManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
