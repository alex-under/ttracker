package ru.alexunder.ttracker.core

import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.WorkItemAdded
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object WorkLogKeeper {

    private val filePath = FileNames.workLog.path
    private val dateTimeStoreFormat = DateTimeFormatter.ISO_DATE_TIME
    private const val delimiter = '\t'

    init {
        RxBus.subscribe(WorkItemAdded::class) {
            Files.write(filePath,
                    listOf(it.workItem.toFileString()),
                    Charsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        }
    }

    private fun WorkItem.toFileString(): String {
        val sb = StringBuilder()
        sb.writeField(task.id)
        sb.writeQuoted(task.name)
        sb.writeField(from.format(dateTimeStoreFormat))
        sb.append(to.format(dateTimeStoreFormat))
        return sb.toString()
    }

    private fun StringBuilder.writeField(value: Any) {
        append(value.toString()).append(delimiter)
    }

    private fun StringBuilder.writeQuoted(value: String) {
        append("\"").append(value).append("\"").append(delimiter)
    }

    fun readWorkItems(): List<WorkItem> {
        if (!Files.exists(filePath)) {
            return Collections.emptyList()
        }
        return Files.readAllLines(filePath, Charsets.UTF_8)
                .map { it.toWorkItem() }
    }

    private fun String.toWorkItem(): WorkItem {
        val tokens = this.split(delimiter)
        if (tokens.size != 4) {
            throw IOException("Wrong work item log string: $this")
        }
        val taskId = tokens[0].toLong()
        val taskName = tokens[1].unquote()
        val from = LocalDateTime.parse(tokens[2].unquote(), dateTimeStoreFormat)
        val to = LocalDateTime.parse(tokens[3].unquote(), dateTimeStoreFormat)
        return WorkItem(Task(taskId, taskName), from, to)
    }

    private fun String.unquote() =
            this.replace(Regex("^\"|\"$"), "")
}