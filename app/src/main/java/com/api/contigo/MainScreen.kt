package com.api.contigo

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    // Lanzador para pedir permiso de notificaciones
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
        if (isGranted) {
            navController.navigate("itinerary_screen") // Navegar si se concede el permiso
        } else {
            // Mostrar algún mensaje si es necesario
            // Aquí puedes mostrar un toast u otra notificación de que el permiso fue rechazado
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.backgroud),
            contentDescription = "Fondo de pantalla",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado de la aplicación
            Text(
                text = "NextToU",
                modifier = Modifier
                    .padding(bottom = 24.dp),
                fontSize = 64.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Bienvenido a NextToU, tu compañera de viajes. Guarda tus momentos, itinerarios y captura fotos memorables.",
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            // Botones de opciones
            Button(
                onClick = { navController.navigate("map_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008577)) // Verde oscuro
            ) {
                Text(
                    text = "Ver Mapa",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Button(
                onClick = { navController.navigate("camera_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)) // Púrpura
            ) {
                Text(
                    text = "Tomar Foto",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Button(
                onClick = { navController.navigate("photo_gallery_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)) // Aqua
            ) {
                Text(
                    text = "Ver Fotos",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            // Botón de Itinerario
            Button(
                onClick = {
                    // Verifica si el permiso ya fue concedido
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
                        navController.navigate("itinerary_screen") // Navegar si el permiso ya está concedido
                    } else {
                        // Solicita el permiso si no ha sido concedido
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)) // Lila
            ) {
                Text(
                    text = "Itinerario",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}
