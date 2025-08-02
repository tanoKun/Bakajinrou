package com.github.tanokun.bakajinrou.plugin.setting.builder.game

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import org.bukkit.plugin.Plugin
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

class ObserversDefiner(private val context: DIContext) {
    fun observers(
        uiContext: CoroutineContext = context.get<Plugin>().minecraftDispatcher,
        asyncContext: CoroutineContext = context.get<Plugin>().asyncDispatcher,
        dsl: ObserverDefinerDsl.() -> Unit
    ): CommonSchedulesRegisterDsl {
        dsl(ObserverDefinerDsl(uiContext, asyncContext))

        return CommonSchedulesRegisterDsl(context)
    }

    inner class ObserverDefinerDsl(val uiContext: CoroutineContext, val asyncContext: CoroutineContext) {
        fun <T : Any> get(type: KClass<T>): T = context.get(type)

        inline fun <reified T : Any> get(): T = get(T::class)
    }
}