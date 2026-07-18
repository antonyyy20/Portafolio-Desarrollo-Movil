package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab1.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                val favorites = remember { mutableStateListOf<String>() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "lista",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("lista") {
                            WorldCountriesScreen(
                                favorites = favorites,
                                onToggleFavorite = { country ->
                                    if (favorites.contains(country)) favorites.remove(country)
                                    else favorites.add(country)
                                },
                                onNavigateToFavorites = { navController.navigate("favoritos") }
                            )
                        }
                        composable("favoritos") {
                            SavedFavoritesScreen(
                                favorites = favorites,
                                onToggleFavorite = { country ->
                                    if (favorites.contains(country)) favorites.remove(country)
                                    else favorites.add(country)
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
