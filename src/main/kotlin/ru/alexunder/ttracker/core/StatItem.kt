package ru.alexunder.ttracker.core

import java.time.Duration

data class StatItem(
        val task: Task,
        private val workItems: List<WorkItem>) {

    val todayDuration : Duration = workItems
            .filter { it.task == task }
            .map { it.duration }
            .reduce { first, second -> first.plus(second) }
}