package com.ingeniero.navegacionapp.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.ingeniero.navegacionapp.R
import com.ingeniero.navegacionapp.navigation.Screen
import com.ingeniero.navegacionapp.ui.theme.*

@Composable
fun SettingsScreen(navController: NavController) {
    val scroll = rememberScrollState()
    var notif by remember { mutableStateOf(true) }
    var sync  by remember { mutableStateOf(false) }

    // Animación: rotación continua
    val inf = rememberInfiniteTransition(label = "gear")
    val rotation by inf.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "rotate"
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

        Text("Configuración", style = MaterialTheme.typography.headlineLarge.copy(color = DuoPurple))

        // IMAGEN con animación de rotación
        Image(
            painter            = painterResource(R.drawable.img_settings),
            contentDescription = "Engranaje",
            modifier           = Modifier.size(160.dp).rotate(rotation)
        )

        OutlinedTextField(
            value = "Manuel Guillen", onValueChange = {}, readOnly = true,
            label = { Text("Ingeniero Desarrollador") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DuoPurple, unfocusedBorderColor = BorderLight, focusedLabelColor = DuoPurple)
        )

        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
            colors   = CardDefaults.cardColors(containerColor = White),
            border   = BorderStroke(1.dp, BorderLight)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Notificaciones", style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary))
                    Switch(checked = notif, onCheckedChange = { notif = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = DuoPurple))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Sincronización", style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary))
                    Switch(checked = sync, onCheckedChange = { sync = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = DuoPurple))
                }
            }
        }

        Button(
            onClick = { navController.navigate(Screen.Details.createRoute(3)) },
            modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DuoPurple)
        ) { Text("Ver Detalles (ID: 3)", style = MaterialTheme.typography.labelLarge.copy(color = White)) }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E5E5))
        ) { Text("Volver", style = MaterialTheme.typography.labelLarge.copy(color = TextPrimary)) }

        Spacer(Modifier.height(16.dp))
    }
}
