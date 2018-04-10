package ru.alexunder.ttracker.core

import java.time.LocalDateTime

data class WorkItem(
        val task: Task,
        val from: LocalDateTime,
        val to: LocalDateTime
)

fun main(args: Array<String>) {
    println("hello")
}