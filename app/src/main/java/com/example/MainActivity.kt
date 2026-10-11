package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notification.SecurityAlertManager
import com.example.ui.components.AppUpdatePromptDialog
import com.example.ui.components.CveDetailModalSheet
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.AttackScreen
import com.example.ui.screens.GeminiChatbotScreen
import com.example.ui.screens.IntelScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SecurityAlertsLibraryScreen
import com.example.ui.screens.ThreatDetailScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SeverityCritical
import com.example.ui.viewmodel.ThreatFeedViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ThreatFeedViewModel by viewModels()
    private var pendingThreatId = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            val themeMode by viewModel.currentThemeMode.collectAsStateWithLifecycle()
            MyApplicationTheme(themeMode = themeMode) {
                CyberShieldApp(
                    viewModel = viewModel,
                    initialThreatId = pendingThreatId.value,
                    onThreatHandled = { pendingThreatId.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val threatId = intent?.getStringExtra(SecurityAlertManager.EXTRA_THREAT_ID)
        if (!threatId.isNullOrBlank()) {
            pendingThreatId.value = threatId
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Intel : Screen("intel", "Intel", Icons.Default.Security)
    data object Alerts : Screen("alerts", "Alerts", Icons.Default.NotificationsActive)
    data object Attack : Screen("attack", "ATT&CK", Icons.Default.Shield)
    data object Learn : Screen("learn", "Learn", Icons.Default.School)
    data object Library : Screen("library", "Library", Icons.Default.FolderSpecial)
    data object AlertsLibrary : Screen("alerts_library", "Alerts Library", Icons.Default.Security)
    data object Chatbot : Screen("chatbot", "Gemini AI", Icons.Default.Security)
    data object Detail : Screen("detail/{threatId}", "Threat Detail", Icons.Default.Security) {
        fun createRoute(threatId: String) = "detail/${java.net.URLEncoder.encode(threatId, "UTF-8")}"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberShieldApp(
    viewModel: ThreatFeedViewModel,
    initialThreatId: String?,
    onThreatHandled: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val appUpdateInfo by viewModel.appUpdateInfo.collectAsStateWithLifecycle()
    val updateDownloadState by viewModel.updateDownloadState.collectAsStateWithLifecycle()

    // Prompt for in-app APK updates when detected
    appUpdateInfo?.let { update ->
        AppUpdatePromptDialog(
            updateInfo = update,
            downloadState = updateDownloadState,
            onConfirmUpdate = {
                viewModel.downloadAndInstallUpdate(update)
            },
            onDismiss = {
                viewModel.dismissAppUpdate()
            }
        )
    }

    // Handle deep links / notification taps
    LaunchedEffect(initialThreatId) {
        if (!initialThreatId.isNullOrBlank()) {
            navController.navigate(Screen.Detail.createRoute(initialThreatId))
            onThreatHandled()
        }
    }

    val topLevelScreens = listOf(
        Screen.Intel,
        Screen.Alerts,
        Screen.Attack,
        Screen.Learn,
        Screen.Library
    )

    val showBottomBar = currentRoute in topLevelScreens.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CyberTheme.colors.bg,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = CyberTheme.colors.surface,
                    contentColor = CyberTheme.colors.textPrimary,
                    tonalElevation = 8.dp,
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    topLevelScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (screen == Screen.Alerts && stats.unreadAlertsCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = SeverityCritical,
                                                contentColor = Color.White
                                            ) {
                                                Text("${stats.unreadAlertsCount}", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.label,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = when (screen) {
                                    Screen.Alerts -> SeverityCritical
                                    Screen.Learn -> CyberEmerald
                                    else -> CyberCyan
                                },
                                selectedTextColor = when (screen) {
                                    Screen.Alerts -> SeverityCritical
                                    Screen.Learn -> CyberEmerald
                                    else -> CyberCyan
                                },
                                unselectedIconColor = CyberTextMuted,
                                unselectedTextColor = CyberTextMuted,
                                indicatorColor = when (screen) {
                                    Screen.Alerts -> SeverityCritical.copy(alpha = 0.15f)
                                    Screen.Learn -> CyberEmerald.copy(alpha = 0.15f)
                                    else -> CyberCyan.copy(alpha = 0.15f)
                                }
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Intel.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Intel Screen
            composable(Screen.Intel.route) {
                IntelScreen(
                    viewModel = viewModel,
                    onThreatClick = { threatId ->
                        navController.navigate(Screen.Detail.createRoute(threatId))
                    },
                    onNavigateToAlerts = {
                        navController.navigate(Screen.Alerts.route)
                    },
                    onNavigateToAlertsLibrary = {
                        navController.navigate(Screen.AlertsLibrary.route)
                    },
                    onNavigateToChatbot = {
                        navController.navigate(Screen.Chatbot.route)
                    }
                )
            }

            // 2. Alerts Screen
            composable(Screen.Alerts.route) {
                AlertsScreen(
                    viewModel = viewModel,
                    onNavigateToThreat = { threatId ->
                        navController.navigate(Screen.Detail.createRoute(threatId))
                    }
                )
            }

            // 3. ATT&CK Screen
            composable(Screen.Attack.route) {
                AttackScreen(
                    viewModel = viewModel,
                    onNavigateToChatbot = {
                        navController.navigate(Screen.Chatbot.route)
                    }
                )
            }

            // 4. Learn Screen
            composable(Screen.Learn.route) {
                LearnScreen(
                    viewModel = viewModel,
                    onNavigateToChatbot = {
                        navController.navigate(Screen.Chatbot.route)
                    },
                    onNavigateToAlertsLibrary = {
                        navController.navigate(Screen.AlertsLibrary.route)
                    }
                )
            }

            // 5. Library & Settings Screen
            composable(Screen.Library.route) {
                LibraryScreen(
                    viewModel = viewModel,
                    onThreatClick = { threatId ->
                        navController.navigate(Screen.Detail.createRoute(threatId))
                    },
                    onNavigateToChatbot = {
                        navController.navigate(Screen.Chatbot.route)
                    }
                )
            }

            // 6. Security Alerts Library (Investigation Guide)
            composable(Screen.AlertsLibrary.route) {
                SecurityAlertsLibraryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Gemini Chatbot Secondary Screen
            composable(Screen.Chatbot.route) {
                GeminiChatbotScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Threat Detail Screen
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("threatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val rawThreatId = backStackEntry.arguments?.getString("threatId") ?: ""
                val threatId = try { java.net.URLDecoder.decode(rawThreatId, "UTF-8") } catch (e: Throwable) { rawThreatId }
                ThreatDetailScreen(
                    threatId = threatId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Expanded CVE Details Modal Bottom Sheet
        val selectedCveDetail by viewModel.selectedCveDetail.collectAsStateWithLifecycle()
        val selectedCveMatchingThreat by viewModel.selectedCveMatchingThreat.collectAsStateWithLifecycle()
        val cveSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        selectedCveDetail?.let { detail ->
            CveDetailModalSheet(
                cveDetail = detail,
                sheetState = cveSheetState,
                matchingThreat = selectedCveMatchingThreat,
                onDismissRequest = {
                    viewModel.clearSelectedCve()
                },
                onViewThreatDetail = { threatId ->
                    navController.navigate(Screen.Detail.createRoute(threatId))
                }
            )
        }
    }
}
