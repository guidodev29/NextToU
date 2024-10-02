package com.api.contigo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import android.media.ExifInterface
import android.graphics.Matrix
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CameraScreen(
    navController: NavController,
    onBackToMainScreen: () -> Unit
) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showCamera by remember { mutableStateOf(true) }

    // Lanzador para la cámara con Uri
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            bitmap = photoUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use {
                    // Decodificar la imagen y rotarla si es necesario
                    BitmapFactory.decodeStream(it)?.let { decodedBitmap ->
                        val rotatedBitmap = rotateImageIfRequired(context, decodedBitmap, uri)
                        saveRotatedBitmap(context, rotatedBitmap, uri) // Guardar la imagen rotada directamente
                        rotatedBitmap
                    }
                }
            }
            showCamera = false
        }
    }

    // Pantalla con la imagen de fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background3), // Asigna el recurso de imagen de fondo
                contentScale = ContentScale.Crop
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Ajusta el padding para la organización de los elementos
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showCamera) {
                // Botón más grande para tomar foto
                Button(
                    onClick = {
                        // Creamos el archivo URI donde se guardará la foto
                        val imageUri = createImageUri(context)
                        photoUri = imageUri
                        launcher.launch(imageUri)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE), // Color de fondo del botón
                        contentColor = Color.White // Color del texto
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp) // Botón más alto para mayor visibilidad
                ) {
                    Text(
                        text = "Captura tu momento",
                        style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Mostrar previsualización de la imagen capturada
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Foto tomada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp) // Mostrar imagen más grande
                                .padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para guardar la foto
                    Button(
                        onClick = {
                            photoUri?.let {
                                Toast.makeText(context, "Foto guardada en ${context.filesDir}/photos", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Guardar Recuerdo")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para volver a la pantalla principal
                    Button(
                        onClick = onBackToMainScreen,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}

// Función para rotar la imagen si es necesario
fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
    val exif = ExifInterface(inputStream)

    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    val matrix = Matrix()

    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

// Función para guardar la imagen rotada en el mismo URI
fun saveRotatedBitmap(context: Context, bitmap: Bitmap, uri: Uri) {
    val outputStream = context.contentResolver.openOutputStream(uri)
    outputStream?.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) // Sobrescribir con la imagen rotada
    }
}

// Función para crear un archivo URI para la foto
fun createImageUri(context: Context): Uri {
    val photoFile = File(context.filesDir, "photos").apply {
        if (!exists()) mkdir()
    }.let {
        File(it, "foto_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg")
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // Asegúrate de que esto coincida con tu `provider` en el AndroidManifest
        photoFile
    )
}
