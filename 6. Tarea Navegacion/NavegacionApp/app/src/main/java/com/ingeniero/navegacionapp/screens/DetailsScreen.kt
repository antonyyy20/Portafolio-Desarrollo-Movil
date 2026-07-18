package com.ingeniero.navegacionapp.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.ingeniero.navegacionapp.R
import com.ingeniero.navegacionapp.ui.theme.*

@Composable
fun DetailsScreen(navController: NavController, itemId: Int) {
    val scroll = rememberScrollState()

    val accentColor = when (itemId) {
        1    -> DuoGreen
        2    -> DuoBlue
        3    -> DuoPurple
        else -> DuoOrange
    }

    // Animación: brillo (alpha pulsante) + escala leve
    val inf = rememberInfiniteTransition(label = "trophy")
    val alpha by inf.animateFloat(
        initialValue = 0.75f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    val scale by inf.animateFloat(
        initialValue = 1f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
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

        Text("Detalles", style = MaterialTheme.typography.headlineLarge.copy(color = accentColor))

        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors   = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.1f)),
            border   = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
        ) {
            Text("Parámetro recibido — ID: $itemId",
                style = MaterialTheme.typography.bodyLarge.copy(color = accentColor),
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                textAlign = TextAlign.Center)
        }

        // IMAGEN con animación brillo + escala
        Image(
            painter            = painterResource(R.drawable.img_trophy),
            contentDescription = "Trofeo",
            modifier           = Modifier.size(160.dp).scale(scale).alpha(alpha)
        )

        OutlinedTextField(
            value = "Manuel Guillen", onValueChange = {}, readOnly = true,
            label = { Text("Ingeniero Desarrollador") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor, unfocusedBorderColor = BorderLight, focusedLabelColor = accentColor)
        )

        OutlinedTextField(
            value = "Ingeniería de Software • IA • Full Stack", onValueChange = {}, readOnly = true,
            label = { Text("Especialidad") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor, unfocusedBorderColor = BorderLight, focusedLabelColor = accentColor)
        )

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
        ) { Text("Volver", style = MaterialTheme.typography.labelLarge.copy(color = White)) }

        Spacer(Modifier.height(16.dp))
    }
}
