package ru.alexunder.ttracker.core

import java.time.Duration
import java.time.LocalDate

class DayStatItem(
        val task: Task,
        day: LocalDate,
        allWorkItems: List<WorkItem>) {

    val totalDuration: Duration =
            allWorkItems.map { it.duration }
                    .reduce { first, second -> first.plus(second) }

    val dayDuration: Duration =
            allWorkItems.filter { it.from.toLocalDate() == day }
                    .map { it.duration }
                    .reduce { first, second -> first.plus(second) }
}