package ru.alexunder.ttracker.ui

import javafx.beans.property.Property
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCode
import ru.alexunder.ttracker.core.Task
import ru.alexunder.ttracker.core.TaskProvider
import ru.alexunder.ttracker.core.Tracker
import tornadofx.*


class TaskSelectorController : Controller() {
    private val taskProvider = TaskProvider()
    private val taskTracker = Tracker
    val tasks: SimpleListProperty<Task> = SimpleListProperty()
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

    fun startSelected() {
        taskTracker.startTracking(selectedTask.value)
        notifyStateChange()
    }

    fun startNew(name: String) {
        val createdTask = taskProvider.createTask(name)
        taskTracker.startTracking(createdTask)
        notifyStateChange()
    }

    fun notifyStateChange() {
        fire(TrackingStateChanged)
    }
}

object TrackingStateChanged : FXEvent(EventBus.RunOn.ApplicationThread)

object UpKeyPressed : FXEvent(EventBus.RunOn.BackgroundThread)
object DownKeyPressed : FXEvent(EventBus.RunOn.ApplicationThread)
object EnterKeyPressed : FXEvent(EventBus.RunOn.ApplicationThread)
class SearchStringChanged(val value: String) : FXEvent(EventBus.RunOn.BackgroundThread)

class SearchQueryView : View() {
    private val context: TaskSelectorController by inject()
    var searchString = ""

    override val root = hbox {
        textfield {
            textProperty().addListener { _, _, newValue ->
                searchString = newValue
                fire(SearchStringChanged(newValue))
            }

            setOnKeyPressed { event ->
                when (event.code) {
                    KeyCode.DOWN -> fire(DownKeyPressed)
                    KeyCode.UP -> fire(UpKeyPressed)
                    KeyCode.ENTER -> fire(EnterKeyPressed)
                    else -> {
                    }
                }
            }

            requestFocus()
        }

        button("start") {
            context.selectedTask.onChange { task ->
                text = if (task == null) "create" else "start"
            }
            subscribe<EnterKeyPressed> {
                start()
            }
            action {
                start()
            }
            isFocusTraversable = false
        }

    }

    private fun start() {
        if (context.selectedTask.value != null) {
            context.startSelected()
        } else {
            context.startNew(searchString)
        }
    }
}

class SearchResultsView : View() {
    private val context: TaskSelectorController by inject()

    override val root = tableview(context.tasks) {
        readonlyColumn("id", Task::id)
        readonlyColumn("name", Task::name)
        bindSelected(context.selectedTask)
        selectFirst()
        columnResizePolicy = SmartResize.POLICY
        isFocusTraversable = false

        items.onChange {
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

class TaskSelectorView : View() {
    private val searchQueryView: SearchQueryView by inject()
    private val searchResultsView: SearchResultsView by inject()

    override val root = vbox {
        add(searchQueryView)
        add(searchResultsView)
        isFocusTraversable = false
    }
}

