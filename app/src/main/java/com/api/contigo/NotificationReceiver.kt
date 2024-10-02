package com.api.contigo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("EVENT_TITLE") ?: "Recordatorio"

        if (context != null && NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            val notification = NotificationCompat.Builder(context, "event_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            with(NotificationManagerCompat.from(context)) {
                notify(0, notification)
            }
        } else {
            // Manejo de error si no se tienen los permisos de notificación
        }
    }
}
