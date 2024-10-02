package com.api.contigo

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EventsScreen(context: Context) {
    val eventsList = loadEventsFromLocalStorage(context)
    var eventToDelete by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(eventsList.size) { index ->
            val (title, description, dateTime) = eventsList[index].split("|")
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = description, style = MaterialTheme.typography.bodyMedium)  // Mostrar la descripción
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = dateTime, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End // Mover el botón a la derecha
                    ) {
                        Button(
                            onClick = { eventToDelete = eventsList[index] },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                        ) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                        }
                    }
                }
            }
        }
    }

    // Mostrar el diálogo de confirmación cuando se elija eliminar un evento
    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que deseas eliminar este evento?") },
            confirmButton = {
                TextButton(onClick = {
                    deleteEventFromLocalStorage(context, event)
                    eventToDelete = null
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text("No")
                }
            }
        )
    }
}

// Función para eliminar un evento del almacenamiento local
fun deleteEventFromLocalStorage(context: Context, event: String) {
    val sharedPreferences = context.getSharedPreferences("events", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val eventList = sharedPreferences.getStringSet("event_list", mutableSetOf()) ?: mutableSetOf()
    eventList.remove(event)
    editor.putStringSet("event_list", eventList)
    editor.apply()
}

fun loadEventsFromLocalStorage(context: Context): List<String> {
    val sharedPreferences = context.getSharedPreferences("events", Context.MODE_PRIVATE)
    return sharedPreferences.getStringSet("event_list", mutableSetOf())?.toList() ?: emptyList()
}