package ru.alexunder.ttracker.core.events

import ru.alexunder.ttracker.core.Task
import java.time.Duration
import java.time.LocalDateTime

data class TrackingStarted(
        val task: Task,
        val startedAt: LocalDateTime
)

data class TrackingStopped(
        val task: Task,
        val startedAt: LocalDateTime,
        val stoppedAt: LocalDateTime
)

data class TrackingInProgress(
        val task: Task,
        val startedAt: LocalDateTime,
        val inProgressAt: LocalDateTime
) {
    val duration = Duration.between(startedAt, inProgressAt)!!
}