package com.api.contigo


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Estado para manejar el permiso de ubicación
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Estado para manejar si el mapa debe mostrarse
    var showMap by remember { mutableStateOf(false) }

    // Verificar si el permiso ya está concedido y mostrar el mapa
    LaunchedEffect(key1 = locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            showMap = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)) // Fondo más oscuro y moderno
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!showMap) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "NextToU",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Permite a NextToU acceder a tu ubicación para mostrarte en el mapa.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { locationPermissionState.launchPermissionRequest() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp) // Botón redondeado para un diseño moderno
                ) {
                    Text(text = "Solicitar Permiso de Ubicación", color = Color.White)
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "¡Tu aventura comienza aquí!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Tu ubicación actual en el mapa",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                MapViewComposable(context = context, lifecycleOwner = lifecycleOwner)

                Spacer(modifier = Modifier.height(16.dp))

                // Botón adicional para explorar más (diseño consistente con el resto de la app)
                Button(
                    onClick = { /* MERO ADORNO */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "NextToU siempre contigo, justo NextToU", color = Color.White, fontSize = 18.sp,)
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapViewComposable(context: Context, lifecycleOwner: LifecycleOwner) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Manejo del ciclo de vida del MapView
    DisposableEffect(key1 = lifecycleOwner) {
        val map = MapView(context)
        mapView = map

        // Configurar el MapView
        map.onCreate(Bundle())
        map.onStart()

        // Asíncrono para inicializar el mapa cuando esté listo
        map.getMapAsync { googleMap ->
            // Obtener la ubicación actual
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLocation = LatLng(it.latitude, it.longitude)
                    googleMap.addMarker(MarkerOptions().position(currentLocation).title("Tu Ubicación Actual"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                } ?: run {
                    Toast.makeText(context, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }

            // Si no hay ubicación, muestra un marcador en Sídney
            val defaultLocation = LatLng(-34.0, 151.0)
            googleMap.addMarker(MarkerOptions().position(defaultLocation).title("Ubicación Predeterminada"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        }

        onDispose {
            mapView?.apply {
                onStop()
                onDestroy()
            }
            mapView = null
        }
    }

    // Asegurarse de que MapView esté inicializado antes de mostrarlo
    mapView?.let { map ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Ajustar el tamaño del contenedor
                .padding(16.dp) // Añadir padding
        ) {
            // Mostrar el MapView dentro de AndroidView
            AndroidView(factory = { map })
        }
    }
}
