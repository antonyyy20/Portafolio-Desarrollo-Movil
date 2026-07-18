package com.ingeniero.navegacionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ingeniero.navegacionapp.navigation.AppNavigation
import com.ingeniero.navegacionapp.ui.theme.NavegacionAppTheme
import com.ingeniero.navegacionapp.ui.theme.Background

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavegacionAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Background) {
                    AppNavigation()
                }
            }
        }
    }
}
