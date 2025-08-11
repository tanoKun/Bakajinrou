package com.github.tanokun.bakajinrou.plugin.common.setting.builder

import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope

class BindingListeners(override val scope: Scope) : KoinScopeComponent, AutoCloseable {
    val listeners = scope.getAll<LifecycleListener>()

    override fun close() = listeners.forEach(LifecycleListener::unregisterAll)

    fun registerAll() = listeners.forEach(LifecycleListener::registerAll)

}