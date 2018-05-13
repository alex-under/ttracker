package ru.alexunder.ttracker.ui

import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object Formats {
    val hourMinutes = DateTimeFormatter.ofPattern("HH:mm")!!
    val simpleDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")!!
}

fun Duration.toHoursString(): String {
    val hours = toHours()
    val minutes = toMinutes() % TimeUnit.HOURS.toMinutes(1)
    return if (hours > 0) "${hours}h ${minutes}m"
    else "${minutes}m"
}
