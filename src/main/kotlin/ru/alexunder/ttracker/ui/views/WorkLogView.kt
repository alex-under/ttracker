package ru.alexunder.ttracker.ui.views

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.util.StringConverter
import ru.alexunder.ttracker.core.DayStatItem
import ru.alexunder.ttracker.core.WorkItem
import ru.alexunder.ttracker.core.WorkLog
import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.WorkItemAdded
import ru.alexunder.ttracker.ui.Formats
import tornadofx.*
import java.time.LocalDate

class WorkLogController : Controller() {
    private val workLog = WorkLog
    val selectedDate = SimpleObjectProperty<LocalDate>(LocalDate.now())
    val workItems = SimpleListProperty<WorkItem>()
    val dayStatItems = SimpleListProperty<DayStatItem>()

    init {
        refreshLists()

        RxBus.listen(WorkItemAdded::class.java).subscribe { _ ->
            refreshLists()
        }
        selectedDate.addListener { _ ->
            refreshLists()
        }
    }

    private fun refreshLists() {
        workItems.value = workLog.dayWorkItems(selectedDate.value).observable()
        dayStatItems.value = workLog.getDayStats(selectedDate.value).observable()
    }
}

class DateSelectView : View() {
    private val controller: WorkLogController by inject()

    override val root = hbox {
        button {
            text = "<"
            setOnAction {
                controller.selectedDate.value = controller.selectedDate.value.minusDays(1)
            }
        }
        datepicker {
            bind(controller.selectedDate)

            converter = object : StringConverter<LocalDate>() {
                private val format = Formats.simpleDate

                override fun toString(date: LocalDate?): String =
                        date?.format(format) ?: ""

                override fun fromString(string: String?): LocalDate =
                        LocalDate.parse(string ?: "", format)
            }
        }
        button {
            text = ">"
            setOnAction {
                controller.selectedDate.value = controller.selectedDate.value.plusDays(1)
            }
        }
        button {
            text = "today"
            setOnAction {
                controller.selectedDate.value = LocalDate.now()
            }
        }

        fitToParentSize()
    }
}

class WorkItemsView : View() {
    private val controller: WorkLogController by inject()

    override val root = tableview(controller.workItems) {
        readonlyColumn("task", WorkItem::task) {
            value { param ->
                param.value.task.name
            }
        }
        readonlyColumn("from", WorkItem::from) {
            value { param ->
                param.value.from.format(Formats.hourMinutes)
            }
        }
        readonlyColumn("to", WorkItem::to) {
            value { param ->
                param.value.to.format(Formats.hourMinutes)
            }
        }

        columnResizePolicy = SmartResize.POLICY
    }
}

class WorkStatView : View() {
    private val controller: WorkLogController by inject()

    override val root = tableview(controller.dayStatItems) {
        readonlyColumn("task", DayStatItem::task) {
            value { param ->
                param.value.task.name
            }
        }
        readonlyColumn("today", DayStatItem::task) {
            value { param ->
                param.value.dayDuration.toString()
            }
        }
        readonlyColumn("total", DayStatItem::task) {
            value { param ->
                param.value.totalDuration.toString()
            }
        }
    }
}

class WorkLogView : View("Work log") {
    private val dateSelectView: DateSelectView by inject()
    private val workItemsView: WorkItemsView by inject()
    private val workStatView: WorkStatView by inject()

    override val root = vbox {
        add(dateSelectView)
        splitpane(Orientation.VERTICAL) {
            add(workItemsView)
            add(workStatView)
        }
    }
}
