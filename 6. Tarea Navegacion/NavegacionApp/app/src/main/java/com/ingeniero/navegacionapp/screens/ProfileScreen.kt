package com.ingeniero.navegacionapp.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.ingeniero.navegacionapp.R
import com.ingeniero.navegacionapp.navigation.Screen
import com.ingeniero.navegacionapp.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
    val scroll = rememberScrollState()

    // Animación: pulso (scale)
    val inf = rememberInfiniteTransition(label = "profile")
    val scale by inf.animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scroll)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Text("Perfil", style = MaterialTheme.typography.headlineLarge.copy(color = DuoBlue))

        // IMAGEN con animación pulse
        Image(
            painter            = painterResource(R.drawable.img_profile),
            contentDescription = "Perfil",
            modifier           = Modifier.size(160.dp).scale(scale)
        )

        OutlinedTextField(
            value = "Manuel Guillen", onValueChange = {}, readOnly = true,
            label = { Text("Ingeniero Desarrollador") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DuoBlue, unfocusedBorderColor = BorderLight, focusedLabelColor = DuoBlue)
        )

        OutlinedTextField(
            value = "Software Full Stack • IA", onValueChange = {}, readOnly = true,
            label = { Text("Especialidad") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DuoBlue, unfocusedBorderColor = BorderLight, focusedLabelColor = DuoBlue)
        )

        Button(
            onClick = { navController.navigate(Screen.Settings.route) },
            modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DuoBlue)
        ) { Text("Ir a Configuración", style = MaterialTheme.typography.labelLarge.copy(color = White)) }

        Button(
            onClick = { navController.navigate(Screen.Details.createRoute(2)) },
            modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)
        ) { Text("Ver Detalles (ID: 2)", style = MaterialTheme.typography.labelLarge.copy(color = White)) }

        Spacer(Modifier.height(16.dp))
    }
}
