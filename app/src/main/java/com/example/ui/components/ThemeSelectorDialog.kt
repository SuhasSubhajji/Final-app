package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberPalette
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.CyberThemeMode
import com.example.ui.theme.LightAnalystPalette
import com.example.ui.theme.OledBlackPalette
import com.example.ui.theme.TacticalDarkPalette
import com.example.ui.theme.CyberMatrixPalette

@Composable
fun ThemeSelectorDialog(
    currentMode: CyberThemeMode,
    onSelectMode: (CyberThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = CyberTheme.colors

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = theme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.border),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("theme_selector_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.accentPrimary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = null,
                                    tint = theme.accentPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SOC Display Environment",
                                color = theme.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Low-light & night monitoring modes",
                                color = theme.textMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = theme.textMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Theme Options List
                CyberThemeMode.entries.forEach { mode ->
                    val isSelected = mode == currentMode
                    val palette = when (mode) {
                        CyberThemeMode.TACTICAL_DARK -> TacticalDarkPalette
                        CyberThemeMode.OLED_BLACK -> OledBlackPalette
                        CyberThemeMode.CYBER_MATRIX -> CyberMatrixPalette
                        CyberThemeMode.LIGHT_ANALYST -> LightAnalystPalette
                    }
                    val icon: ImageVector = when (mode) {
                        CyberThemeMode.TACTICAL_DARK -> Icons.Default.DarkMode
                        CyberThemeMode.OLED_BLACK -> Icons.Default.Brightness2
                        CyberThemeMode.CYBER_MATRIX -> Icons.Default.Terminal
                        CyberThemeMode.LIGHT_ANALYST -> Icons.Default.LightMode
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                onSelectMode(mode)
                                onDismiss()
                            }
                            .testTag("theme_option_${mode.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) theme.accentPrimary.copy(alpha = 0.12f) else theme.surfaceVariant
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) theme.accentPrimary else theme.border.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon and Preview Swatch
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(palette.bg)
                                    .border(1.dp, palette.border, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = palette.accentPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = mode.displayName,
                                        color = if (isSelected) theme.accentPrimary else theme.textPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (mode == CyberThemeMode.OLED_BLACK) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF22D3EE).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "ZERO GLOW",
                                                color = Color(0xFF22D3EE),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = mode.description,
                                    color = theme.textMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(theme.accentPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (theme.isDark) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Preference is persistently stored on-device and applies instantly across all security feeds, dashboards, and roadmaps.",
                    color = theme.textMuted,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

@Composable
fun CyberThemeToggleButton(
    currentMode: CyberThemeMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors
    val icon = when (currentMode) {
        CyberThemeMode.TACTICAL_DARK -> Icons.Default.DarkMode
        CyberThemeMode.OLED_BLACK -> Icons.Default.Brightness2
        CyberThemeMode.CYBER_MATRIX -> Icons.Default.Terminal
        CyberThemeMode.LIGHT_ANALYST -> Icons.Default.LightMode
    }

    IconButton(
        onClick = onClick,
        modifier = modifier.testTag("theme_mode_toggle_button")
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = theme.accentPrimary.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.accentPrimary.copy(alpha = 0.4f)),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Switch theme (current: ${currentMode.displayName})",
                    tint = theme.accentPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

