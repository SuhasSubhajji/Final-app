package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MitreInteractiveGraphComposable
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberTheme
import com.example.ui.viewmodel.ThreatFeedViewModel

@Composable
fun AttackScreen(
    viewModel: ThreatFeedViewModel,
    onNavigateToChatbot: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val theme = CyberTheme.colors
    var showGraphView by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.bg,
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Graph View Toggle FAB
                FloatingActionButton(
                    onClick = { showGraphView = !showGraphView },
                    containerColor = CyberTheme.colors.surface,
                    contentColor = CyberCyan,
                    modifier = Modifier.testTag("fab_toggle_mitre_graph")
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = if (showGraphView) "Show Hub" else "Show D3 Graph",
                        modifier = Modifier.size(20.dp)
                    )
                }

                onNavigateToChatbot?.let { launchChat ->
                    FloatingActionButton(
                        onClick = { launchChat() },
                        containerColor = CyberCyan,
                        contentColor = Color.Black,
                        modifier = Modifier.testTag("fab_ask_gemini_attack")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Ask Gemini AI",
                                modifier = Modifier.size(20.dp)
                            )
                            Text("Ask AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(theme.bg)
        ) {
            if (showGraphView) {
                MitreInteractiveGraphComposable(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                MitreMatrixScreen()
            }
        }
    }
}
