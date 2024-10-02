package com.api.contigo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File
import androidx.core.content.FileProvider
import android.net.Uri

@Composable
fun PhotoGalleryScreen(context: Context) {
    var selectedPhoto by remember { mutableStateOf<File?>(null) }  // Almacena la foto seleccionada
    var showConfirmationDialog by remember { mutableStateOf(false) }  // Controla si se muestra el cuadro de confirmación
    var showPhotoDialog by remember { mutableStateOf(false) }  // Controla si se muestra la foto en grande
    val photosList = remember { loadPhotosFromLocalStorage(context) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(photosList) { photoFile ->
            val bitmap = loadImageFromFile(photoFile)
            bitmap?.let {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(100.dp)
                        .clickable {
                            selectedPhoto = photoFile
                            showPhotoDialog = true
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                    elevation = CardDefaults.elevatedCardElevation(8.dp)
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Photo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // Cuadro de diálogo para ver la foto en grande
    if (showPhotoDialog && selectedPhoto != null) {
        Dialog(
            onDismissRequest = { showPhotoDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val bitmap = loadImageFromFile(selectedPhoto!!)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Selected Photo",
                            modifier = Modifier
                                .padding(16.dp)
                                .size(300.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Button(
                            onClick = {
                                // Acción para compartir la foto
                                val photoUri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    selectedPhoto!!
                                )
                                sharePhoto(context, photoUri)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Compartir")
                        }
                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                // Mostrar diálogo de confirmación para eliminar la foto
                                showConfirmationDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }

    // Cuadro de diálogo de confirmación de eliminación
    if (showConfirmationDialog && selectedPhoto != null) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmar Eliminación", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("¿Estás seguro de que deseas eliminar esta foto?", color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                Button(
                    onClick = {
                        deletePhoto(context, selectedPhoto!!)
                        showConfirmationDialog = false
                        showPhotoDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Función para cargar fotos desde el almacenamiento local
fun loadPhotosFromLocalStorage(context: Context): List<File> {
    val directory = File(context.filesDir, "photos")
    return if (directory.exists()) {
        directory.listFiles()?.toList() ?: emptyList()
    } else {
        emptyList()
    }
}

// Función para cargar una imagen desde un archivo
fun loadImageFromFile(file: File): Bitmap? {
    return BitmapFactory.decodeFile(file.path)
}

// Función para eliminar una foto del almacenamiento local
fun deletePhoto(context: Context, file: File) {
    file.delete()
}

// Función para compartir una foto con un texto predeterminado
fun sharePhoto(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "Mira mi foto!")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir foto con"))
}
