package ru.alexunder.ttracker.ui

import dorkbox.systemTray.MenuItem
import dorkbox.systemTray.SystemTray
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

        systemTray.setImage("/home/alex/downloads/clock-circular-outline (4).png")
        systemTray.status = "tracking..."

        systemTray.menu.add(MenuItem("Quit", ActionListener {
            systemTray.shutdown()
            stage.close()
            System.exit(0)  // not necessary if all non-daemon threads have stopped.
        }))
        systemTray.menu.add(MenuItem("Test"))
    }
}