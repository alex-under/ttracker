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


class GlobalKeyHook(stage: Stage) {
    private val keyListener = GlobalKeyListener(stage)

    init {
        muteLogger()
    }

    fun registerKeyHook() {
        try {
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(keyListener)
        } catch (ex: NativeHookException) {
            println("There was a problem registering the native hook: ${ex.message}")
            System.exit(1)
        }
    }

    fun unregisterKeyHook() {
        GlobalScreen.removeNativeKeyListener(keyListener)
        GlobalScreen.unregisterNativeHook()
    }

    private fun muteLogger() {
        val logger = Logger.getLogger(GlobalScreen::class.java.`package`.name)
        logger.level = Level.WARNING
        logger.useParentHandlers = false
    }
}

class GlobalKeyListener(private val stage: Stage) : NativeKeyListener {
    private val eventConsumer = EventConsumer()

    override fun nativeKeyPressed(event: NativeKeyEvent) {
        if (event.keyCode != NativeKeyEvent.VC_W ||
                event.modifiers and NativeKeyEvent.META_MASK == 0) {
            return
        }

        JavaFX.dispatch {
            if (stage.isShowing) {
                stage.hide()
            } else {
                stage.isAlwaysOnTop = true
                stage.show()
                stage.toFront()
                stage.requestFocus()
            }
        }
        eventConsumer.tryConsumeEvent(event)
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {}
    override fun nativeKeyTyped(e: NativeKeyEvent) {}
}

class EventConsumer {
    private val field = NativeInputEvent::class.java.getDeclaredField("reserved")

    init {
        field.isAccessible = true
    }

    fun tryConsumeEvent(e: NativeKeyEvent) {
        field.setShort(e, 0x01.toShort()) // try to consume event
    }
}
