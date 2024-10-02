package com.api.contigo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.backgroud), // Asume que has colocado la imagen en drawable
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
                color = Color.White, // Texto en blanco para destacar en el fondo
                textAlign = TextAlign.Center
            )

            Text(
                text = "Bienvenido a NextToU, tu compañera de viajes. Guarda tus momentos, itinerarios y captura fotos memorables.",
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(alpha = 0.8f), // Texto en blanco semitransparente
                textAlign = TextAlign.Center
            )

            // Botones de opciones
            Button(
                onClick = { navController.navigate("map_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008577)) // Verde oscuro para combinar con la imagen
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)) // Color púrpura para contrastar
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)) // Color Aqua
            ) {
                Text(
                    text = "Ver Fotos",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Button(
                onClick = { navController.navigate("itinerary_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)) // Color Lila
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
