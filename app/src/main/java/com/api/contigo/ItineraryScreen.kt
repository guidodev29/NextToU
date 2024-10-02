package com.api.contigo

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ItineraryScreen(navController: NavController) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()

    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Agregar Evento al Itinerario",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(8.dp)

        )

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título del evento") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError && title.isBlank()
        )
        TextField(value = description, onValueChange = { description = it }, label = { Text("Descripción del evento") }, isError = showError && title.isBlank())

        Button(
            onClick = {
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        val timePickerDialog = TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)
                                selectedDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(calendar.time)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
                        )
                        timePickerDialog.show()
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar Fecha y Hora" )
        }

        Text(text = "Fecha y Hora seleccionada: $selectedDateTime", modifier = Modifier.padding(8.dp),  color = Color.White.copy(alpha = 0.8f))

        if (showError && selectedDateTime.isBlank()) {
            Text(text = "Por favor selecciona una fecha y hora", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                if (title.isNotBlank() && selectedDateTime.isNotBlank()) {
                    saveEventToLocalStorage(context, title, selectedDateTime)
                    scheduleNotification(context, title, calendar.timeInMillis)
                    Toast.makeText(context, "Evento guardado y notificación programada", Toast.LENGTH_SHORT).show()
                    showError = false
                } else {
                    // Mostrar error si algún campo está vacío
                    showError = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar evento y programar notificación")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("events_screen") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver todos los eventos")
        }
    }
}

fun saveEventToLocalStorage(context: Context, title: String, dateTime: String) {
    val sharedPreferences = context.getSharedPreferences("events", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val eventList = sharedPreferences.getStringSet("event_list", mutableSetOf()) ?: mutableSetOf()
    eventList.add("$title|$dateTime")
    editor.putStringSet("event_list", eventList)
    editor.apply()
}

fun scheduleNotification(context: Context, title: String, timeInMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("EVENT_TITLE", title)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    try {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    } catch (e: SecurityException) {
        Toast.makeText(context, "No se puede programar la alarma sin permisos", Toast.LENGTH_SHORT).show()
    }
}