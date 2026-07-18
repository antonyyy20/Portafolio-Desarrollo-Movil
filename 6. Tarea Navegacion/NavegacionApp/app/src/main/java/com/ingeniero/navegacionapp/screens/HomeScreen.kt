package com.ingeniero.navegacionapp.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.ingeniero.navegacionapp.R
import com.ingeniero.navegacionapp.navigation.Screen
import com.ingeniero.navegacionapp.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    val scroll = rememberScrollState()

    // Animación: flotar arriba y abajo
    val inf = rememberInfiniteTransition(label = "rocket")
    val offsetY by inf.animateFloat(
        initialValue = 0f, targetValue = -12f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
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

        Text("Inicio", style = MaterialTheme.typography.headlineLarge.copy(color = DuoGreen))

        // IMAGEN con animación float
        Image(
            painter            = painterResource(R.drawable.img_rocket),
            contentDescription = "Cohete",
            modifier           = Modifier.size(160.dp).offset(y = offsetY.dp)
        )

        OutlinedTextField(
            value = "Manuel Guillen", onValueChange = {}, readOnly = true,
            label = { Text("Ingeniero Desarrollador") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DuoGreen, unfocusedBorderColor = BorderLight, focusedLabelColor = DuoGreen)
        )

        Button(
            onClick = { navController.navigate(Screen.Profile.route) },
            modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)
        ) { Text("Ir a Perfil", style = MaterialTheme.typography.labelLarge.copy(color = White)) }

        Button(
            onClick = { navController.navigate(Screen.Details.createRoute(1)) },
            modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DuoBlue)
        ) { Text("Ver Detalles (ID: 1)", style = MaterialTheme.typography.labelLarge.copy(color = White)) }

        Spacer(Modifier.height(16.dp))
    }
}
