package ru.alexunder.ttracker.ui.views

import javafx.beans.property.SimpleListProperty
import ru.alexunder.ttracker.core.StatItem
import ru.alexunder.ttracker.core.WorkItem
import ru.alexunder.ttracker.core.WorkLog
import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.WorkItemAdded
import ru.alexunder.ttracker.ui.Formats
import tornadofx.*
import java.time.LocalDate

class WorkLogController : Controller() {
    private val workLog = WorkLog
    val workItems: SimpleListProperty<WorkItem> = SimpleListProperty()
    val statItems: SimpleListProperty<StatItem> = SimpleListProperty()

    init {
        workItems.value = workLog.workItems().observable()
        statItems.value = workLog.getDayStats(LocalDate.now()).observable()

        RxBus.listen(WorkItemAdded::class.java).subscribe { _ ->
            workItems.invalidate()
            statItems.value = workLog.getDayStats(LocalDate.now()).observable()
        }
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

    override val root = tableview(controller.statItems) {
        readonlyColumn("task", StatItem::task) {
            value { param ->
                param.value.task.name
            }
        }
        readonlyColumn("today", StatItem::task) {
            value { param ->
                param.value.todayDuration.toString()
            }
        }
    }
}

class WorkLogView : View("Work log") {
    private val workItemsView: WorkItemsView by inject()
    private val workStatView: WorkStatView by inject()

    override val root = vbox {
        add(workItemsView)
        add(workStatView)
        isFocusTraversable = false
    }
}
