package ru.alexunder.ttracker.core

import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.TrackingStarted
import ru.alexunder.ttracker.core.events.TrackingStopped
import java.time.LocalDateTime

object Tracker {

    private var state: TrackerState = Stopped()

    fun startTracking(task: Task) {
        state.stop()
        state = Tracking(task)
        println("tracking task: $task")
    }

    fun stopTracking() {
        println("stopping track")
        state.stop()
        state = Stopped()
    }

    fun isTracking() =
            state is Tracking
}

sealed class TrackerState {
    abstract fun stop()
}

class Stopped(val at: LocalDateTime = LocalDateTime.now()) : TrackerState() {
    override fun stop() {}
}

class Tracking(
        private val task: Task,
        private val from: LocalDateTime = LocalDateTime.now()) : TrackerState() {

    init {
        RxBus.publish(TrackingStarted(task, from))
    }

    override fun stop() {
        RxBus.publish(TrackingStopped(task, from, LocalDateTime.now()))
    }
}