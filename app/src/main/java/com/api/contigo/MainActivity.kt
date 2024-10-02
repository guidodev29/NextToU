package com.api.contigo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        setContent {
            TravelApp()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Eventos"
            val descriptionText = "Canal para eventos programados"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("event_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun TravelApp() {
    val navController = rememberNavController()
    val context = LocalContext.current // Obtener el contexto de la composable
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) } // Aquí definimos el estado del bitmap

    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") { MainScreen(navController) }
        composable("map_screen") { MapScreen() }
        composable("camera_screen") {
            CameraScreen(
                navController = navController,
                onBackToMainScreen = {
                    navController.navigateUp()  // Navegar de regreso
                }
            )
        }
        composable(route = "photo_gallery_screen") {
            PhotoGalleryScreen(context = LocalContext.current)
        }
        composable("itinerary_screen") {
            ItineraryScreen(navController = navController) // No es necesario pasar `eventsList`
        }
        composable("events_screen") {
            EventsScreen(context = context) // Pasamos solo el contexto
        }
    }
}
