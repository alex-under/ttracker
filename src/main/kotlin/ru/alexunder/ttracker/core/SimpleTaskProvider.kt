package ru.alexunder.ttracker.core

interface TaskProvider {
    fun createTask(name: String) : Task
    fun getAllTasks() : List<Task>
    fun getTaskById(id: Long): Task
    fun findTaskByName(name: String): List<Task>
    fun deleteTask(id: Long)
}


class SimpleTaskProvider : TaskProvider {

    private var idSeq = 0L
    private val tasks = mutableListOf(
            Task(id = nextId(), name = "create something"),
            Task(id = nextId(), name = "qa"),
            Task(id = nextId(), name = "meeting"),
            Task(id = nextId(), name = "code upload"),
            Task(id = nextId(), name = "do nothing"))

    override fun createTask(name: String) : Task {
        val task = Task(nextId(), name)
        tasks.add(task)
        return task
    }

    override fun getAllTasks() : List<Task> = tasks

    override fun getTaskById(id: Long): Task =
            tasks.first { it.id == id }

    override fun findTaskByName(name: String): List<Task> =
            tasks.filter { it.name.contains(other = name, ignoreCase = true) }

    override fun deleteTask(id: Long) {
        val task = getTaskById(id)
        tasks.remove(task)
    }

    private fun nextId() = ++idSeq
}
