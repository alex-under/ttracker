package ru.alexunder.ttracker.core

import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.WorkItemAdded
import java.time.LocalDateTime

object WorkLog {

    private val workItems : MutableList<WorkItem> = ArrayList()

    init {
        workItems.add(WorkItem(Task(100, "work item 1"), LocalDateTime.now().minusMinutes(1), LocalDateTime.now()))
        workItems.add(WorkItem(Task(101, "work item 2"), LocalDateTime.now().minusMinutes(1), LocalDateTime.now()))
        workItems.add(WorkItem(Task(102, "work item 3"), LocalDateTime.now().minusMinutes(1), LocalDateTime.now()))
    }

    fun addItem(task: Task, from: LocalDateTime, to: LocalDateTime) {
        val workItem = WorkItem(task, from, to)
        workItems.add(workItem)
        RxBus.publish(WorkItemAdded(workItem))
    }

    fun workItems() : List<WorkItem> =
            workItems

}
