package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.dogtags.DogTagTrackerScreen
import com.example.ui.screens.lineup.LineupBuilderScreen
import com.example.ui.screens.recruitment.RecruitmentScreen
import com.example.ui.screens.roster.RosterScreen
import com.example.ui.screens.scrims.ScrimsScreen
import com.example.ui.screens.tools.GuildToolsScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.GuildViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Hub", Icons.Default.Dashboard)
    object Roster : Screen("roster", "Roster", Icons.Default.People)
    object DogTags : Screen("dogtags", "Friday Tags", Icons.Default.MilitaryTech)
    object Scrims : Screen("scrims", "Wars", Icons.Default.SportsEsports)
    object Lineup : Screen("lineup", "Lineups", Icons.Default.Groups)
    object Recruitment : Screen("recruitment", "Recruits", Icons.Default.PersonAdd)
    object Tools : Screen("tools", "FF Tools", Icons.Default.Handyman)
}

class MainActivity : ComponentActivity() {

    private val viewModel: GuildViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FreeFireGuildTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: GuildViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Roster,
        Screen.DogTags,
        Screen.Scrims,
        Screen.Lineup,
        Screen.Tools
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(listOf(FFFireOrange, FFFireGold))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔥", fontSize = 18.sp)
                        }

                        Column {
                            Text(
                                text = "FF GUILD MASTER",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = FFTextPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "ESPORTS & GUILD MANAGEMENT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FFFireGold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FFDarkBackground,
                    titleContentColor = FFTextPrimary
                ),
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(FFFireRed.copy(alpha = 0.2f))
                            .border(0.8.dp, FFFireRed.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = FFFireRed,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = FFDarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(0.5.dp, FFDarkBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                bottomNavItems.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            )
                        },
                        selected = selected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FFFireGold,
                            selectedTextColor = FFFireGold,
                            unselectedIconColor = FFTextMuted,
                            unselectedTextColor = FFTextMuted,
                            indicatorColor = FFFireOrange.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_${screen.route}")
                    )
                }
            }
        },
        containerColor = FFDarkBackground
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
                    onNavigateToRoster = { navController.navigate(Screen.Roster.route) },
                    onNavigateToDogTags = { navController.navigate(Screen.DogTags.route) },
                    onNavigateToScrims = { navController.navigate(Screen.Scrims.route) },
                    onNavigateToLineup = { navController.navigate(Screen.Lineup.route) },
                    onNavigateToRecruitment = { navController.navigate(Screen.Recruitment.route) },
                    onNavigateToTools = { navController.navigate(Screen.Tools.route) }
                )
            }
            composable(Screen.Roster.route) {
                RosterScreen(viewModel = viewModel)
            }
            composable(Screen.DogTags.route) {
                DogTagTrackerScreen(viewModel = viewModel)
            }
            composable(Screen.Scrims.route) {
                ScrimsScreen(viewModel = viewModel)
            }
            composable(Screen.Lineup.route) {
                LineupBuilderScreen(viewModel = viewModel)
            }
            composable(Screen.Recruitment.route) {
                RecruitmentScreen(viewModel = viewModel)
            }
            composable(Screen.Tools.route) {
                GuildToolsScreen(viewModel = viewModel)
            }
        }
    }
}
