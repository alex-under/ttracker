package ru.alexunder.ttracker.core.taskprovider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import ru.alexunder.ttracker.core.FileNames
import ru.alexunder.ttracker.core.Task
import java.io.File

data class TaskFileModel(
        var idSeq: Long = 0,
        var tasks: Collection<Task> = emptyList())

object TaskYamlSaver {
    private val yamlFactory = YAMLFactory()
    private val objectMapper = ObjectMapper(yamlFactory)

    fun save(model: TaskFileModel) {
        objectMapper.writeValue(File(FileNames.tasks.name), model)
    }

    fun load() : TaskFileModel {
        return objectMapper.readValue(File(FileNames.tasks.name), TaskFileModel::class.java)
    }
}