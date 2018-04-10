package ru.alexunder.ttracker.core

import java.time.LocalDateTime

object Tracker {

    private val workLog: WorkLog = WorkLog()
    var state: TrackerState = Stopped()

    fun startTracking(task: Task) {
        state.stop()
        state = Tracking(task = task, workLog = workLog)
    }

    fun stopTracking() {
        state.stop()
        state = Stopped()
    }
}

sealed class TrackerState {
    abstract fun stop()
}

data class Stopped(
        val at: LocalDateTime = LocalDateTime.now()
) : TrackerState() {
    override fun stop() {}
}

data class Tracking(
        val task: Task,
        val from: LocalDateTime = LocalDateTime.now(),
        val workLog: WorkLog
) : TrackerState() {
    override fun stop() {
        workLog.addItem(task = task, from = from, to = LocalDateTime.now())
    }
}