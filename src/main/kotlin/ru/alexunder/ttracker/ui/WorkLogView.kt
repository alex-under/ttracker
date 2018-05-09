package ru.alexunder.ttracker.ui

import javafx.beans.property.SimpleListProperty
import ru.alexunder.ttracker.core.WorkItem
import ru.alexunder.ttracker.core.WorkLog
import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.WorkItemAdded
import tornadofx.*

class WorkLogController : Controller() {
    private val workLog = WorkLog
    val workItems: SimpleListProperty<WorkItem> = SimpleListProperty()

    init {
        workItems.value = workLog.workItems().observable()

        RxBus.listen(WorkItemAdded::class.java).subscribe { _ ->
            workItems.invalidate()
        }
    }
}

class WorkLogView : View("Work log") {
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