package ru.alexunder.ttracker.core

import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.WorkItemAdded
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object WorkLog {

    private val workItems : MutableList<WorkItem> = ArrayList()

    init {
        workItems.add(WorkItem(Task(100, "qa_1"), date("2018-05-07T10:00:00"), date("2018-05-07T11:00:00")))
        workItems.add(WorkItem(Task(101, "meeting"), date("2018-05-07T11:00:00"), date("2018-05-07T13:10:00")))
        workItems.add(WorkItem(Task(102, "fix bug"), date("2018-05-07T14:05:00"), date("2018-05-07T16:25:00")))

        workItems.add(WorkItem(Task(100, "qa_1"), date("2018-05-08T09:30:00"), date("2018-05-08T09:45:00")))
        workItems.add(WorkItem(Task(101, "meeting"), date("2018-05-08T09:45:00"), date("2018-05-08T10:30:00")))
        workItems.add(WorkItem(Task(107, "implement"), date("2018-05-08T10:30:00"), date("2018-05-08T10:50:00")))
        workItems.add(WorkItem(Task(108, "implem"), date("2018-05-08T13:30:00"), date("2018-05-08T15:30:00")))

        println(WorkLogWriter.getMe())
    }

    private fun date(string: String): LocalDateTime =
            LocalDateTime.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))


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

    fun getDayStats(day: LocalDate): List<DayStatItem> =
            workItems
                    .filter { dayTasks(day).contains(it.task) }     //todo check call on every cycle
                    .groupBy { it.task }
                    .map { entry -> DayStatItem(entry.key, day, entry.value) }

    private fun dayTasks(day: LocalDate) : Set<Task> =
            dayWorkItems(day)
                    .map { it.task }
                    .toSet()
}
