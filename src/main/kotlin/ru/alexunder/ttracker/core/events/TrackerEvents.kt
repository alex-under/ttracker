package ru.alexunder.ttracker.core.events

import ru.alexunder.ttracker.core.Task
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