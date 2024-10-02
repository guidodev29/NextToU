package com.api.contigo

import java.text.SimpleDateFormat
import java.util.*

data class EventData(
    val title: String,
    val description: String,
    val timeInMillis: Long
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }
}
