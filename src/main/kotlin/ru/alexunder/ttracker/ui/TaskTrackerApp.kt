package ru.alexunder.ttracker.ui

import dorkbox.systemTray.MenuItem
import dorkbox.systemTray.SystemTray
import dorkbox.util.JavaFX
import javafx.application.Platform
import javafx.stage.Stage
import tornadofx.*
import java.awt.event.ActionListener

class TaskTrackerApp : App(TaskSelectorView::class) {

    private val context: TasksContext by inject()


    override fun start(stage: Stage) {
        super.start(stage)
        intiTray(stage)
    }

    private fun intiTray(stage: Stage) {
        SystemTray.AUTO_SIZE = false
        val systemTray = SystemTray.get() ?: throw RuntimeException("Failed to init dorkbox.SystemTray")



        systemTray.setImage(Resources.activeImage)
        systemTray.status = "tracking..."

        systemTray.menu.add(MenuItem("Quit", ActionListener {
            systemTray.shutdown()
            shutdownFxApp(stage)
        }))

        systemTray.menu.add(MenuItem("Test"))
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