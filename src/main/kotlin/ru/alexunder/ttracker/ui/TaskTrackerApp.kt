package ru.alexunder.ttracker.ui

import dorkbox.systemTray.MenuItem
import dorkbox.systemTray.SystemTray
import dorkbox.util.JavaFX
import javafx.application.Platform
import javafx.stage.Stage
import ru.alexunder.ttracker.core.Tracker
import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.TrackingStarted
import ru.alexunder.ttracker.core.events.TrackingStopped
import tornadofx.*
import java.awt.event.ActionListener
import javax.swing.JSeparator

class TaskTrackerApp : App(TaskSelectorView::class) {

    private val tracker = Tracker


    override fun start(stage: Stage) {
        super.start(stage)
        val tray = initTray(stage)
        subscribeForTrackerEvents(tray)
    }

    private fun initTray(stage: Stage) : SystemTray {
        SystemTray.AUTO_SIZE = false
        val systemTray = SystemTray.get() ?: throw RuntimeException("Failed to init dorkbox.SystemTray")
        systemTray.setImage(Resources.inactiveImage)
        buildMenu(systemTray, stage)
        return systemTray
    }

    private fun subscribeForTrackerEvents(tray: SystemTray) {
        RxBus.listen(TrackingStarted::class.java).subscribe { startEvent ->
            tray.setImage(Resources.activeImage)
            tray.status = "tracking ${startEvent.task.name} from ${startEvent.startedAt}"
        }
        RxBus.listen(TrackingStopped::class.java).subscribe { stopEvent ->
            tray.setImage(Resources.inactiveImage)
            tray.status = "idle"
        }
    }

    private fun buildMenu(systemTray: SystemTray, stage: Stage) {

        systemTray.menu.add(MenuItem("Stop tracking", ActionListener {
            tracker.stopTracking()
        }))

        systemTray.menu.add(JSeparator())

        systemTray.menu.add(MenuItem("Quit", ActionListener {
            systemTray.shutdown()
            shutdownFxApp(stage)
        }))
    }

    private fun shutdownFxApp(stage: Stage) {
        val quit = {
            stage.hide()
            Platform.exit()
        }
        if (JavaFX.isEventThread()) {
            quit.invoke()
        } else {
            JavaFX.dispatch {
                quit.invoke()
            }
        }
    }
}