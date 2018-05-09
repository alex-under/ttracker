package ru.alexunder.ttracker.ui

import dorkbox.util.JavaFX
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage
import tornadofx.*

class TaskTrackerApp : App(TaskSelectorView::class) {

    private lateinit var tray: SysTray
    private lateinit var keyHook: GlobalKeyHook
    private lateinit var taskSelectStage: Stage
    private lateinit var workLogStage: Stage

    override fun start(stage: Stage) {
        super.start(stage)
        taskSelectStage = stage
        Platform.setImplicitExit(false)
        taskSelectStage.hide()
        workLogStage = buildWorkLogStage()
        keyHook = GlobalKeyHook(taskSelectStage)
        keyHook.registerKeyHook()
        tray = SysTray(taskSelectStage, workLogStage, { shutdown() })
    }

    private fun buildWorkLogStage(): Stage {
        val newStage = Stage()
        newStage.scene = Scene(WorkLogView().root)
        return newStage
    }

    private fun shutdown() {
        keyHook.unregisterKeyHook()
        val quit = {
            taskSelectStage.hide()
            workLogStage.hide()
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