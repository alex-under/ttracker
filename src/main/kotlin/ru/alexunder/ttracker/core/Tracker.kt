package ru.alexunder.ttracker.core

import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.TrackingInProgress
import ru.alexunder.ttracker.core.events.TrackingStarted
import ru.alexunder.ttracker.core.events.TrackingStopped
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

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

    private val progressSendingPeriod = TimeUnit.MINUTES.toMillis(1)
    private val timer: Timer

    init {
        RxBus.publish(TrackingStarted(task, from))
        timer = timer(daemon = true, period = progressSendingPeriod, initialDelay = progressSendingPeriod) {
            sendTrackingProgress()
        }
    }

    override fun stop() {
        timer.cancel()
        RxBus.publish(TrackingStopped(task, from, LocalDateTime.now()))
    }

    private fun sendTrackingProgress() {
        println("tracking in progress: $task")
        RxBus.publish(TrackingInProgress(task, from, LocalDateTime.now()))
    }
}