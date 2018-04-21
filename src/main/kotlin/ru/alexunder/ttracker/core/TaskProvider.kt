package ru.alexunder.ttracker.core

class TaskProvider {

    private val tasks : MutableList<Task> = mutableListOf(
            Task(id = 1, name = "create something"),
            Task(id = 2, name = "qa"),
            Task(id = 3, name = "meeting"),
            Task(id = 4, name = "code upload"),
            Task(id = 5, name = "do nothing"))

    fun getAllTasks() =
            tasks.toList()

    fun getTaskById(id : Int) : Task =
            tasks.first { it.id == id }

    fun findTaskByName(name : String) : List<Task> =
            tasks.filter { it.name.contains(other = name, ignoreCase = true) }

}
