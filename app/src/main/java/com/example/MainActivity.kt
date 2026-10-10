package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notification.SecurityAlertManager
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DevSecOpsToolsScreen
import com.example.ui.screens.FeedListScreen
import com.example.ui.screens.GeminiChatbotScreen
import com.example.ui.screens.LearningRoadmapScreen
import com.example.ui.screens.ThreatDetailScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
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
            val themeMode by viewModel.currentThemeMode.collectAsState()
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

sealed class Screen(val route: String, val label: String) {
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object Feeds : Screen("feeds", "Feeds")
    data object Roadmaps : Screen("roadmaps", "Learning")
    data object Alerts : Screen("alerts", "Alerts")
    data object DevSecOps : Screen("devsecops", "Hub")
    data object Chatbot : Screen("chatbot", "Gemini AI")
    data object Detail : Screen("detail/{threatId}", "Threat Detail") {
        fun createRoute(threatId: String) = "detail/${java.net.URLEncoder.encode(threatId, "UTF-8")}"
    }
}

@Composable
fun CyberShieldApp(
    viewModel: ThreatFeedViewModel,
    initialThreatId: String?,
    onThreatHandled: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val stats by viewModel.stats.collectAsState()

    // Handle deep link / notification click navigation
    LaunchedEffect(initialThreatId) {
        if (!initialThreatId.isNullOrBlank()) {
            navController.navigate(Screen.Detail.createRoute(initialThreatId))
            onThreatHandled()
        }
    }

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Feeds.route,
        Screen.Roadmaps.route,
        Screen.Alerts.route,
        Screen.DevSecOps.route
    )

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
                    // 1. Dashboard Tab
                    NavigationBarItem(
                        selected = currentRoute == Screen.Dashboard.route,
                        onClick = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Dashboard,
                                contentDescription = "Dashboard",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Home",
                                fontSize = 11.sp,
                                fontWeight = if (currentRoute == Screen.Dashboard.route) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberCyan,
                            selectedTextColor = CyberCyan,
                            unselectedIconColor = CyberTextMuted,
                            unselectedTextColor = CyberTextMuted,
                            indicatorColor = CyberCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_dashboard")
                    )

                    // 2. Feeds Tab
                    NavigationBarItem(
                        selected = currentRoute == Screen.Feeds.route,
                        onClick = {
                            navController.navigate(Screen.Feeds.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Threat Feeds",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Feeds",
                                fontSize = 11.sp,
                                fontWeight = if (currentRoute == Screen.Feeds.route) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberCyan,
                            selectedTextColor = CyberCyan,
                            unselectedIconColor = CyberTextMuted,
                            unselectedTextColor = CyberTextMuted,
                            indicatorColor = CyberCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_feeds")
                    )

                    // 3. Roadmaps Tab
                    NavigationBarItem(
                        selected = currentRoute == Screen.Roadmaps.route,
                        onClick = {
                            navController.navigate(Screen.Roadmaps.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Learning Roadmaps",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Learning",
                                fontSize = 11.sp,
                                fontWeight = if (currentRoute == Screen.Roadmaps.route) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberEmerald,
                            selectedTextColor = CyberEmerald,
                            unselectedIconColor = CyberTextMuted,
                            unselectedTextColor = CyberTextMuted,
                            indicatorColor = CyberEmerald.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_roadmaps")
                    )

                    // 4. Alerts Tab
                    NavigationBarItem(
                        selected = currentRoute == Screen.Alerts.route,
                        onClick = {
                            navController.navigate(Screen.Alerts.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (stats.unreadAlertsCount > 0) {
                                        Badge(
                                            containerColor = SeverityCritical,
                                            contentColor = Color.White
                                        ) {
                                            Text("${stats.unreadAlertsCount}", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Push Alerts",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "Alerts",
                                fontSize = 11.sp,
                                fontWeight = if (currentRoute == Screen.Alerts.route) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SeverityCritical,
                            selectedTextColor = SeverityCritical,
                            unselectedIconColor = CyberTextMuted,
                            unselectedTextColor = CyberTextMuted,
                            indicatorColor = SeverityCritical.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_alerts")
                    )

                    // 5. DevSecOps Hub Tab
                    NavigationBarItem(
                        selected = currentRoute == Screen.DevSecOps.route,
                        onClick = {
                            navController.navigate(Screen.DevSecOps.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "DevSecOps Hub",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Hub",
                                fontSize = 11.sp,
                                fontWeight = if (currentRoute == Screen.DevSecOps.route) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberCyan,
                            selectedTextColor = CyberCyan,
                            unselectedIconColor = CyberTextMuted,
                            unselectedTextColor = CyberTextMuted,
                            indicatorColor = CyberCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_devsecops")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onThreatClick = { threatId ->
                        navController.navigate(Screen.Detail.createRoute(threatId))
                    },
                    onNavigateToRoadmaps = {
                        navController.navigate(Screen.Roadmaps.route)
                    },
                    onNavigateToAlerts = {
                        navController.navigate(Screen.Alerts.route)
                    },
                    onNavigateToHub = {
                        navController.navigate(Screen.DevSecOps.route)
                    },
                    onNavigateToChatbot = {
                        navController.navigate(Screen.Chatbot.route)
                    }
                )
            }

            composable(Screen.Feeds.route) {
                FeedListScreen(
                    viewModel = viewModel,
                    onThreatClick = { threatId ->
                        navController.navigate(Screen.Detail.createRoute(threatId))
                    },
                    onNavigateToAlerts = {
                        navController.navigate(Screen.Alerts.route)
                    },
                    onNavigateToChatbot = {
                        navController.navigate(Screen.Chatbot.route)
                    }
                )
            }

            composable(Screen.Roadmaps.route) {
                LearningRoadmapScreen(
                    viewModel = viewModel
                )
            }

            composable(Screen.Chatbot.route) {
                GeminiChatbotScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

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

            composable(Screen.Alerts.route) {
                AlertsScreen(
                    viewModel = viewModel,
                    onNavigateToThreat = { threatId ->
                        navController.navigate(Screen.Detail.createRoute(threatId))
                    }
                )
            }

            composable(Screen.DevSecOps.route) {
                DevSecOpsToolsScreen(
                    viewModel = viewModel,
                    onThreatClick = { threatId ->
                        navController.navigate(Screen.Detail.createRoute(threatId))
                    },
                    onNavigateToChatbot = {
                        navController.navigate(Screen.Chatbot.route)
                    }
                )
            }
        }
    }
}
