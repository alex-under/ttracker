package ru.alexunder.ttracker.ui

import javafx.application.Platform
import javafx.stage.Stage
import tornadofx.*

class TaskTrackerApp : App(TaskSelectorView::class) {

    private lateinit var tray: SysTray
    private lateinit var keyHook: GlobalKeyHook

    override fun start(stage: Stage) {
        super.start(stage)
        Platform.setImplicitExit(false)
        stage.hide()
        tray = SysTray(stage)
        keyHook = GlobalKeyHook(stage)
        keyHook.registerKeyHook()
    }
}