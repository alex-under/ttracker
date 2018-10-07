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
        state = state.start(task)
    }

    fun stopTracking() {
        state = state.stop()
    }

    fun isTracking() = state.isTracking()
}


sealed class TrackerState {
    abstract fun stop(): TrackerState
    abstract fun start(task: Task): TrackerState
    abstract fun isTracking(): Boolean
}


class Stopped(val at: LocalDateTime = LocalDateTime.now()) : TrackerState() {
    override fun start(task: Task) = Tracking(task)
    override fun isTracking() = false
    override fun stop() = this
}


class Tracking(
        private val task: Task,
        private val from: LocalDateTime = LocalDateTime.now()) : TrackerState() {

    private val progressSendingPeriod = TimeUnit.MINUTES.toMillis(1)
    private val timer: Timer

    init {
        RxBus.publish(TrackingStarted(task, from))
        timer = timer(
                daemon = true,
                period = progressSendingPeriod,
                initialDelay = progressSendingPeriod) {
            sendTrackingProgress()
        }
    }

    override fun start(task: Task): TrackerState {
        if (this.task == task) {
            return this
        }
        stop()
        return Tracking(task)
    }

    override fun stop(): TrackerState {
        timer.cancel()
        RxBus.publish(TrackingStopped(task, from, LocalDateTime.now()))
        return Stopped()
    }

    override fun isTracking(): Boolean = true

    private fun sendTrackingProgress() {
        println("tracking in progress: $task")
        RxBus.publish(TrackingInProgress(task, from, LocalDateTime.now()))
    }
}