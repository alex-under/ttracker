package ru.alexunder.ttracker.ui

import dorkbox.util.JavaFX
import javafx.stage.Stage
import org.jnativehook.GlobalScreen
import org.jnativehook.NativeHookException
import org.jnativehook.NativeInputEvent
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.util.logging.Level
import java.util.logging.Logger


fun registerKeyHook(stage: Stage) {
    try {
        muteJnativeHookLogger()
        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(GlobalKeyListener(stage))
    } catch (ex: NativeHookException) {
        println("There was a problem registering the native hook: ${ex.message}")
        System.exit(1)
    }
}

fun muteJnativeHookLogger() {
    val logger = Logger.getLogger(GlobalScreen::class.java.`package`.name)
    logger.level = Level.WARNING
    logger.useParentHandlers = false
}


class GlobalKeyListener(private val stage: Stage) : NativeKeyListener {

    private val field = NativeInputEvent::class.java.getDeclaredField("reserved")

    init {
        field.isAccessible = true
    }

    override fun nativeKeyPressed(e: NativeKeyEvent) {
        if (e.keyCode != NativeKeyEvent.VC_W ||
                e.modifiers and NativeKeyEvent.META_MASK == 0) {
            return
        }

        JavaFX.dispatch {
            stage.show()
        }
        field.setShort(e, 0x01.toShort()) // try to consume event
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {
    }

    override fun nativeKeyTyped(e: NativeKeyEvent) {
    }
}