package ru.alexunder.ttracker.ui

import javafx.beans.property.Property
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCode
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
            taskProvider.findTaskByName(name)
        } ui {
            tasks.value.clear()
            tasks.value.addAll(it)
        }
    }
}

object UpKeyPressed : FXEvent(EventBus.RunOn.ApplicationThread)
object DownKeyPressed : FXEvent(EventBus.RunOn.ApplicationThread)
object EnterKeyPressed : FXEvent(EventBus.RunOn.ApplicationThread)
class SearchStringChanged(val value : String) : FXEvent(EventBus.RunOn.BackgroundThread)

class SearchQueryView : View() {

    override val root = hbox {
        textfield {
            textProperty().addListener { _, _, newValue ->
                fire(SearchStringChanged(newValue))
            }

            setOnKeyPressed { event ->
                when (event.code) {
                    KeyCode.DOWN -> fire(DownKeyPressed)
                    KeyCode.UP -> fire(UpKeyPressed)
                    KeyCode.ENTER -> fire(EnterKeyPressed)
                    else -> {}
                }
                println("textfield key event: $event")
            }

            requestFocus()
        }

        button("start") {
            subscribe<EnterKeyPressed> {
                println("enter pressed")
            }
            action {
                println("mose clicked")
            }
            isFocusTraversable = false
        }
    }
}

class SearchResultsView : View() {
    private val context: TasksContext by inject()

    override val root = tableview(context.tasks) {
        readonlyColumn("id", Task::id)
        readonlyColumn("name", Task::name)
        bindSelected(context.selectedTask)
        columnResizePolicy = SmartResize.POLICY
        isFocusTraversable = false
        selectFirst()

        items.onChange {
            println("items changed")
            selectFirst()
        }

        subscribe<SearchStringChanged> { event ->
            context.search(event.value)
        }

        subscribe<UpKeyPressed> {
            selectionModel.select(selectionModel.selectedIndex - 1)
        }
        subscribe<DownKeyPressed> {
            selectionModel.select(selectionModel.selectedIndex + 1)
        }
    }
}

class HelloWorldView : View() {
    private val searchQueryView: SearchQueryView by inject()
    private val searchResultsView: SearchResultsView by inject()

    override val root = vbox {
        requestFocus()
        add(searchQueryView)
        add(searchResultsView)
        isFocusTraversable = false
    }
}

class HelloWorldApp : App(HelloWorldView::class)