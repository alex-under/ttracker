package ru.alexunder.ttracker.ui
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import tornadofx.*

class HelloWorldView : View() {
    override val root = VBox()

    init {
        root += Button("ok")
        root += Label("label sss www")
    }
}

class HelloWorldApp : App(HelloWorldView::class)