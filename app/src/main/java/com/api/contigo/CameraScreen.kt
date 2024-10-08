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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navController: NavController,
    onBackToMainScreen: () -> Unit
) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showCamera by remember { mutableStateOf(true) }

    // Gestión del permiso de la cámara
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    // Gestión del permiso para acceder a los archivos
    val storagePermissionState = rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    // Lanzador para la cámara con Uri
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            bitmap = photoUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)?.let { decodedBitmap ->
                        val rotatedBitmap = rotateImageIfRequired(context, decodedBitmap, uri)
                        saveRotatedBitmap(context, rotatedBitmap, uri)
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
                painter = painterResource(id = R.drawable.background3),
                contentScale = ContentScale.Crop
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showCamera) {
                if (cameraPermissionState.status.isGranted) {
                    // Botón para tomar foto
                    Button(
                        onClick = {
                            val imageUri = createImageUri(context)
                            photoUri = imageUri
                            launcher.launch(imageUri)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        Text(
                            text = "Captura tu momento",
                            style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp)
                        )
                    }
                } else {
                    // Botón para solicitar permiso de cámara
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        Text(
                            text = "Solicitar permiso de cámara",
                            style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para ver fotos (solicita acceso a archivos)
                Button(
                    onClick = {
                        if (storagePermissionState.status.isGranted) {
                            navController.navigate("photo_gallery")
                        } else {
                            storagePermissionState.launchPermissionRequest()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    Text("Ver Fotos")
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Foto tomada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
        "${context.packageName}.provider",
        photoFile
    )
}
