package com.ingeniero.navegacionapp.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.*
import com.ingeniero.navegacionapp.screens.*
import com.ingeniero.navegacionapp.ui.theme.*

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem("Inicio",   Icons.Filled.Home,     Screen.Home.route),
        BottomNavItem("Perfil",   Icons.Filled.Person,   Screen.Profile.route),
        BottomNavItem("Ajustes",  Icons.Filled.Settings, Screen.Settings.route),
        BottomNavItem("Detalles", Icons.Filled.Star,     "details/0"),
    )

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        containerColor = Background,
        bottomBar = {
            NavigationBar(containerColor = White, tonalElevation = 0.dp) {
                items.forEach { item ->
                    val selected = when {
                        item.route == "details/0" -> currentRoute == Screen.Details.route
                        else -> currentRoute == item.route
                    }
                    NavigationBarItem(
                        selected = selected,
                        onClick  = {
                            val dest = item.route
                            navController.navigate(dest) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon   = { Icon(item.icon, contentDescription = item.label) },
                        label  = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = DuoGreen,
                            selectedTextColor   = DuoGreen,
                            unselectedIconColor = TextHint,
                            unselectedTextColor = TextHint,
                            indicatorColor      = DuoGreenLight
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(padding),
            enterTransition  = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 5 } },
            exitTransition   = { fadeOut(tween(200)) },
            popEnterTransition  = { fadeIn(tween(300)) },
            popExitTransition   = { fadeOut(tween(200)) + slideOutHorizontally(tween(300)) { it / 5 } }
        ) {
            composable(Screen.Home.route)     { HomeScreen(navController) }
            composable(Screen.Profile.route)  { ProfileScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            composable(
                route     = Screen.Details.route,
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { back ->
                DetailsScreen(navController, back.arguments?.getInt("itemId") ?: 0)
            }
        }
    }
}
