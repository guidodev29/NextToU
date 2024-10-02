package com.api.contigo

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center, // Centrar verticalmente
        horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente
    ) {
        // Título
        Text(
            text = "Agregar un nuevo evento",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 28.dp)
        )

        // Inputs para agregar título y descripción del evento
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título del evento") },
            modifier = Modifier.fillMaxWidth(0.8f) // Ancho de 80%
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción del evento") },
            modifier = Modifier.fillMaxWidth(0.8f) // Ancho de 80%
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para seleccionar fecha y hora
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
            modifier = Modifier.fillMaxWidth(0.8f) // Ancho de 80%
        ) {
            Text("Seleccionar Fecha y Hora")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la fecha y hora seleccionada
        Text(
            text = "Fecha y Hora seleccionada: $selectedDateTime",
            color = Color.White,
            modifier = Modifier.fillMaxWidth(0.8f) // Ancho de 80%
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para agregar el evento
        Button(
            onClick = {
                if (title.isNotBlank() && selectedDateTime.isNotBlank()) {
                    saveEventToLocalStorage(context, title, description, selectedDateTime)
                    val eventDateTimeMillis = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(selectedDateTime)?.time
                    eventDateTimeMillis?.let {
                        scheduleNotification(context, title, it)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f) // Ancho de 80%
        ) {
            Text("Guardar evento y programar notificación")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ver eventos
        Button(
            onClick = {
                navController.navigate("events_screen")
            },
            modifier = Modifier.fillMaxWidth(0.8f) // Ancho de 80%
        ) {
            Text("Ver todos los eventos")
        }
    }
}

// Función para guardar el evento en el almacenamiento local
fun saveEventToLocalStorage(context: Context, title: String, description: String, dateTime: String) {
    val sharedPreferences = context.getSharedPreferences("events", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val eventList = sharedPreferences.getStringSet("event_list", mutableSetOf()) ?: mutableSetOf()
    eventList.add("$title|$description|$dateTime")
    editor.putStringSet("event_list", eventList)
    editor.apply()
}

// Función para programar una notificación
fun scheduleNotification(context: Context, title: String, timeInMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("EVENT_TITLE", title)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Verificar si se puede programar alarmas exactas
    if (alarmManager.canScheduleExactAlarms()) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        Toast.makeText(context, "Evento agregado con éxito...", Toast.LENGTH_LONG).show()

    } else {
        // Mostrar un mensaje o dirigir al usuario a la configuración
        Toast.makeText(context, "La aplicación necesita permiso para programar alarmas exactas. Por favor, habilítelo en la configuración.", Toast.LENGTH_LONG).show()

        // Redirigir al usuario a la configuración para habilitar el permiso
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        context.startActivity(intent)
    }
}
