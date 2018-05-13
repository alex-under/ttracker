package ru.alexunder.ttracker.core.events

import io.reactivex.subjects.PublishSubject
import kotlin.reflect.KClass

object RxBus {
    private val publisher = PublishSubject.create<Any>()

    fun publish(event: Any) =
        publisher.onNext(event)

    fun <T : Any> subscribe(eventType: KClass<T>, listener : (T) -> Unit) {
        publisher.ofType(eventType.java).subscribe { event ->
            listener.invoke(event)
        }
    }
}