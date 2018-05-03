package ru.alexunder.ttracker.ui

import dorkbox.systemTray.MenuItem
import dorkbox.systemTray.SystemTray
import dorkbox.util.JavaFX
import javafx.application.Platform
import javafx.stage.Stage
import tornadofx.*
import java.awt.event.ActionListener
import javax.swing.JSeparator

class TaskTrackerApp : App(TaskSelectorView::class) {

    private val context: TaskSelectorController by inject()




    override fun start(stage: Stage) {
        super.start(stage)
        val tray = intiTray(stage)



        context.selectedTask.onChange { task ->
            tray.status = task?.name
        }
    }

    private fun intiTray(stage: Stage) : SystemTray {
        SystemTray.AUTO_SIZE = false
        val systemTray = SystemTray.get() ?: throw RuntimeException("Failed to init dorkbox.SystemTray")
        systemTray.setImage(Resources.activeImage)
        systemTray.status = "tracking..."
        buildMenu(systemTray, stage)
        return systemTray
    }

    private fun buildMenu(systemTray: SystemTray, stage: Stage) {

        systemTray.menu.add(MenuItem("Set active", ActionListener {
            systemTray.setImage(Resources.activeImage)
        }))

        systemTray.menu.add(MenuItem("Set inactive", ActionListener {
            systemTray.setImage(Resources.inactiveImage)
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