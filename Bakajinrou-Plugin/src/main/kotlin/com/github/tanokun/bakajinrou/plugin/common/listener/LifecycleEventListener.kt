package com.github.tanokun.bakajinrou.plugin.common.listener

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.RegisteredListener

private typealias Callback<E> = (E) -> Unit

abstract class LifecycleEventListener internal constructor(private val plugin: Plugin, listenerDsl: LifecycleEventListener.() -> Unit): Listener,
    LifecycleListener {

    private val registeredListeners = arrayListOf<Pair<RegisteredListener, HandlerList>>()

    init {
        listenerDsl(this)
    }

    internal inline fun <reified E: Event> register(
        eventPriority: EventPriority = EventPriority.NORMAL, ignoreCancelled: Boolean = false, noinline callback: Callback<E>
    ) {
        val eventClass = E::class.java
        val handlerList = eventClass
            .getMethod("getHandlerList")
            .invoke(null) as HandlerList

        val registeredListener = RegisteredListener(this, GameEventExecutor<E>(callback, eventClass), eventPriority, plugin, ignoreCancelled)
        registeredListeners.add(registeredListener to handlerList)
    }

    protected class GameEventExecutor<E: Event>(private val callback: Callback<E>, private val eventClass: Class<E>): EventExecutor {
        @Suppress("UNCHECKED_CAST")
        override fun execute(listener: Listener, event: Event) {
            if (event::class.java != eventClass) return

            callback(event as E)
        }
    }

    override fun registerAll() {
        registeredListeners.forEach { (listener, handlerList) ->
            handlerList.register(listener)
        }
    }

    override fun unregisterAll() {
        registeredListeners.forEach { (listener, handlerList) ->
            handlerList.unregister(listener)
        }
    }
}