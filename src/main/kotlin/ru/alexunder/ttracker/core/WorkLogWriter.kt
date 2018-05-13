package ru.alexunder.ttracker.core

import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.WorkItemAdded
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.format.DateTimeFormatter

object WorkLogWriter {

    private val filePath = Paths.get("${System.getProperty("user.home")}/.ttracker_log.txt")
    private const val delimiter = '\t'

    init {
        println("writer init")
        RxBus.subscribe(WorkItemAdded::class) { workItemAdded ->
            Files.write(filePath, listOf(workItemAdded.workItem.toFileString()), StandardOpenOption.APPEND)
        }
    }

    fun getMe(): Char {
        return delimiter
    }

    private fun WorkItem.toFileString() : String {
        val sb = StringBuilder()
        sb.writeField(task.id)
        sb.writeQuoted(task.name)
        sb.writeQuoted(from.format(DateTimeFormatter.ISO_DATE_TIME))
        sb.writeQuoted(to.format(DateTimeFormatter.ISO_DATE_TIME))
        return sb.toString()
    }

    private fun StringBuilder.writeField(value: Any) {
        append(value.toString()).append(delimiter)
    }

    private fun StringBuilder.writeQuoted(value: String) {
        append("\"").append(value).append("\"").append(delimiter)
    }
}