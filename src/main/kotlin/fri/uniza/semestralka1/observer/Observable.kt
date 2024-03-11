package fri.uniza.semestralka1.observer

import kotlin.properties.Delegates


/**
 * Type alias for observing function.
 */
typealias Observer<T> = (T?) -> Unit

/**
 * Class that enables to observe value changes on [property].
 * @author David Zimen
 */
class Observable<T> {
    /**
     * [Map] of [Observer] functions with [String] as keys.
     */
    private val observers = mutableMapOf<String, Observer<T>>()

    /**
     * Property for detecting changes.
     */
    private var property: T? by Delegates.observable(null) { _, _, new ->
        observers.forEach { (_, func) ->
            func.invoke(new)
        }
    }

    /**
     * Sets new value for [property] and notifies all [observers].
     */
    fun next(newValue: T?) {
        property = newValue
    }

    /**
     * Adds observer function to [observers] for change detection.
     */
    fun subscribe(name: String, observer: Observer<T>) {
        observers[name] = observer
    }

    /**
     * Removes observer with [name].
     */
    fun removeObserver(name: String) {
        observers.remove(name)
    }
}
