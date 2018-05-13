package ru.alexunder.ttracker.ui

import dorkbox.systemTray.MenuItem
import dorkbox.systemTray.SystemTray
import dorkbox.util.JavaFX
import javafx.stage.Stage
import ru.alexunder.ttracker.core.Tracker
import ru.alexunder.ttracker.core.events.RxBus
import ru.alexunder.ttracker.core.events.TrackingStarted
import ru.alexunder.ttracker.core.events.TrackingStopped
import java.awt.event.ActionListener
import javax.swing.JSeparator

class SysTray(
        private val taskSelectStage: Stage,
        private val workLogStage: Stage,
        private val shutdownRunnable: () -> Unit) {

    private val tracker = Tracker
    private lateinit var stopMenuItem: MenuItem

    init {
        val tray = initTray()
        buildMenu(tray)
        subscribeForTrackerEvents(tray)
    }

    private fun initTray(): SystemTray {
        SystemTray.AUTO_SIZE = false
        val systemTray = SystemTray.get() ?: throw RuntimeException("Failed to init dorkbox.SystemTray")
        systemTray.setImage(Resources.inactiveImage)
        return systemTray
    }

    private fun subscribeForTrackerEvents(tray: SystemTray) {
        RxBus.subscribe(TrackingStarted::class) { startEvent ->
            tray.setImage(Resources.activeImage)
            tray.status =
                    "tracking '${startEvent.task.name}' " +
                    "from ${startEvent.startedAt.format(Formats.hourMinutes)}"
            stopMenuItem.enabled = true
        }
        RxBus.subscribe(TrackingStopped::class) { _ ->
            tray.setImage(Resources.inactiveImage)
            tray.status = "idle"
            stopMenuItem.enabled = false
        }
    }

    private fun buildMenu(systemTray: SystemTray) {

        systemTray.menu.add(MenuItem("Select task...", ActionListener {
            JavaFX.dispatch {
                taskSelectStage.show()
            }
        }))

        systemTray.menu.add(MenuItem("Work log...", ActionListener {
            JavaFX.dispatch {
                workLogStage.show()
            }
        }))

        stopMenuItem = systemTray.menu.add(MenuItem("Stop tracking", ActionListener {
            tracker.stopTracking()
        }))
        stopMenuItem.enabled = tracker.isTracking()

        systemTray.menu.add(JSeparator())

        systemTray.menu.add(MenuItem("Quit", ActionListener {
            systemTray.shutdown()
            shutdownRunnable.invoke()
        }))
    }
}
