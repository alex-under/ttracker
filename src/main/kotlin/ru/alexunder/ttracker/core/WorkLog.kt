package ru.alexunder.ttracker.core

import java.time.LocalDateTime

object WorkLog {

    private val workItems : MutableList<WorkItem> = ArrayList()

    fun addItem(task: Task, from: LocalDateTime, to: LocalDateTime) {
        workItems.add(WorkItem(task, from, to))
    }

    fun workItems() =
            workItems

}
