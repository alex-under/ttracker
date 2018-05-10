package ru.alexunder.ttracker.ui

import java.time.format.DateTimeFormatter

object Formats {
    val hourMinutes = DateTimeFormatter.ofPattern("HH:mm")!!
    val simpleDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")!!
}
