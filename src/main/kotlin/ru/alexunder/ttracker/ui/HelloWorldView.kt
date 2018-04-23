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
    val selectedTask: Property<Task> = SimpleObjectProperty()

    init {
        tasks.value = taskProvider.getAllTasks().observable()
    }

    fun search(name: String) {
        runAsync {
            taskProvider.findTaskByName(name).observable()
        } ui {
            tasks.value = it
        }
    }
}

class SearchView : View() {
    private val context: TasksContext by inject()

    override val root = hbox {
        textfield {
            textProperty().addListener { _, _, newValue ->
                context.search(newValue)
            }

            setOnKeyPressed { event ->
                println("key event: $event")
            }
            requestFocus()
        }
        button("start") {
            isFocusTraversable = false
        }
    }
}

class SearchResults : View() {
    private val context: TasksContext by inject()

    override val root = tableview(context.tasks) {
        readonlyColumn("id", Task::id)
        readonlyColumn("name", Task::name)
        bindSelected(context.selectedTask)
        columnResizePolicy = SmartResize.POLICY
        isFocusTraversable = false
    }
}

class HelloWorldView : View() {
    private val searchView: SearchView by inject()
    private val searchResults: SearchResults by inject()

    override val root = vbox {
/*
        addEventHandler(Event.ANY) { event ->
            if (event is KeyEvent) {
                searchView.fire(event.copyFor(searchView, searchView))
                event.consume()
            }
        }
*/
/*        addEventFilter(KeyEvent.KEY_PRESSED, { event ->
            if (event.code == KeyCode.DOWN || event.code == KeyCode.UP || event.code == KeyCode.LEFT
                    || event.code == KeyCode.RIGHT) {
                event.consume()
                println("consumed event: $event")
            }
        })*/

        add(searchView)
        add(searchResults)
    }
}

class HelloWorldApp : App(HelloWorldView::class)