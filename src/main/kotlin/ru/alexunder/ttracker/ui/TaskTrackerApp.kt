package ru.alexunder.ttracker.ui

import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage
import tornadofx.*

class TaskTrackerApp : App(TaskSelectorView::class) {

    private lateinit var tray: SysTray
    private lateinit var keyHook: GlobalKeyHook

    override fun start(stage: Stage) {
        super.start(stage)
        Platform.setImplicitExit(false)
        stage.hide()
        val workLogStage = buildWorkLogStage()
        tray = SysTray(stage, workLogStage)
        keyHook = GlobalKeyHook(stage)
        keyHook.registerKeyHook()
    }

    private fun buildWorkLogStage(): Stage {
        val newStage = Stage()
        newStage.scene = Scene(WorkLogView().root)
        return newStage
    }
}