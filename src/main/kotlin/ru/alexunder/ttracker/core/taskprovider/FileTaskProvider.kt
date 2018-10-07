package ru.alexunder.ttracker.core.taskprovider

import ru.alexunder.ttracker.core.Task
import ru.alexunder.ttracker.core.TaskProvider

class FileTaskProvider : TaskProvider {

    private val keeper = TaskYamlSaver
    private var tasksById: MutableMap<Long, Task> = hashMapOf()
    private var idSeq = 0L

    init {
        loadTasks()
    }

    private fun saveTasks() {
        keeper.save(TaskFileModel(idSeq, tasksById.values))
    }

    private fun loadTasks() {
        val fileModel = keeper.load()
        idSeq = fileModel.idSeq
        tasksById.clear()
        fileModel.tasks.associateByTo(tasksById, { it.id })
    }

    override fun createTask(name: String) : Task {
        val existingTask = getTaskByName(name)
        if (existingTask != null) {
            return existingTask
        }
        val task = Task(nextId(), name)
        tasksById[task.id] = task
        saveTasks()
        return task
    }

    override fun getAllTasks() : List<Task> =
            tasksById.values.toList()

    override fun getTaskById(id: Long): Task =
            tasksById[id] ?: throw TaskNotFoundException(id)

    override fun findTasksByName(name: String): List<Task> =
            tasksById.values.filter { it.name.contains(other = name, ignoreCase = true) }

    override fun deleteTask(id: Long) {
        tasksById.remove(id)
        saveTasks()
    }

    private fun nextId() = ++idSeq

    private fun getTaskByName(name: String): Task? =
            tasksById.values.find { it.name == name }
}

class TaskNotFoundException(taskId: Long) : Throwable("Task $taskId not found")
