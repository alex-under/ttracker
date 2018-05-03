package ru.alexunder.ttracker.core

import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.TrackingStarted
import ru.alexunder.ttracker.core.events.TrackingStopped
import java.time.LocalDateTime

object Tracker {

    private val workLog: WorkLog = WorkLog()
    private var state: TrackerState = Stopped()

    fun startTracking(task: Task) {
        state.stop()
        state = Tracking(task = task, workLog = workLog)
        println("tracking task: $task")
    }

    fun stopTracking() {
        println("stopping track")
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

    init {
        RxBus.publish(TrackingStarted(task, from))
    }

    override fun stop() {
        val to = LocalDateTime.now()
        RxBus.publish(TrackingStopped(task, from, to))
        workLog.addItem(task = task, from = from, to = to)  // todo log by event
    }
}