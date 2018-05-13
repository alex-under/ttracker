package ru.alexunder.ttracker.core

import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.WorkItemAdded
import java.time.LocalDate
import java.time.LocalDateTime

object WorkLog {

    private val workItems : MutableList<WorkItem> = ArrayList()

    init {
        workItems.addAll(WorkLogKeeper.readWorkItems())
    }

    fun addItem(task: Task, from: LocalDateTime, to: LocalDateTime) {
        // todo handle work items between 2 days (covers midnight
        val workItem = WorkItem(task, from, to)
        workItems.add(workItem)
        RxBus.publish(WorkItemAdded(workItem))
    }

    fun allWorkItems() : List<WorkItem> =
            workItems

    fun dayWorkItems(day: LocalDate) : List<WorkItem> =
            workItems.filter { it.from.toLocalDate() == day }

    fun dayStats(day: LocalDate): List<DayStatItem> =
            workItems
                    .filter { dayTasks(day).contains(it.task) }     //todo check call on every cycle
                    .groupBy { it.task }
                    .map { entry -> DayStatItem(entry.key, day, entry.value) }

    private fun dayTasks(day: LocalDate) : Set<Task> =
            dayWorkItems(day)
                    .map { it.task }
                    .toSet()
}
