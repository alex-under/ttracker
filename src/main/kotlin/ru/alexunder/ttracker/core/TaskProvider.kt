package ru.alexunder.ttracker.core

class TaskProvider {

    private var idSeq: Long = 0
    private val tasks: MutableList<Task> = mutableListOf(
            Task(id = nextId(), name = "create something"),
            Task(id = nextId(), name = "qa"),
            Task(id = nextId(), name = "meeting"),
            Task(id = nextId(), name = "code upload"),
            Task(id = nextId(), name = "do nothing"))

    fun createTask(name: String) : Task {
        val task = Task(nextId(), name)
        tasks.add(task)
        return task
    }

    fun getAllTasks() =
            tasks.toList()

    fun getTaskById(id: Long): Task =
            tasks.first { it.id == id }

    fun findTaskByName(name: String): List<Task> =
            tasks.filter { it.name.contains(other = name, ignoreCase = true) }

    private fun nextId() = ++idSeq
}
