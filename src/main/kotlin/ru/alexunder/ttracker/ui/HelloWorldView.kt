package ru.alexunder.ttracker.ui

import javafx.beans.property.Property
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import ru.alexunder.ttracker.core.Task
import ru.alexunder.ttracker.core.TaskProvider
import tornadofx.*

class TasksContext : Controller() {
    private val taskProvider = TaskProvider()
    val tasks = SimpleListProperty<Task>()

    fun search(name : String) {
        runAsync {
            taskProvider.findTaskByName(name).observable()
        } ui {
            tasks.value = it
        }
    }
}

class HelloWorldView : View() {
    private val selectedTask : Property<Task> = SimpleObjectProperty()
    private val context : TasksContext by inject()

    override val root = vbox {
        hbox {
            textfield {
                textProperty().addListener { _, _, newValue ->
                    println("text changed to $newValue")
                    context.search(newValue)
                }
            }
            button("start")
        }

        tableview(context.tasks) {
            readonlyColumn("id", Task::id)
            readonlyColumn("name", Task::name)
            bindSelected(selectedTask)
        }
    }
}

class HelloWorldApp : App(HelloWorldView::class)