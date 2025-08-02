package com.github.tanokun.bakajinrou.plugin.setting.builder.game

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onCancellation
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onLaunching
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleListener
import kotlin.reflect.KClass

class ListenersRegisterDsl(private val context: DIContext) {
    fun listeners(dsl: Dsl.() -> Unit): ObserversDefiner {
        dsl(Dsl())

        return ObserversDefiner(context)
    }

    inner class Dsl {
        private val scheduler = context.get<GameScheduler>()

        fun <L: LifecycleListener> use(klass: KClass<L>) {
            val listener = context.construct(klass)

            scheduler.addSchedule(onLaunching {
                listener.registerAll()
            })

            scheduler.addSchedule(onCancellation {
                listener.unregisterAll()
            })
        }

        inline fun <reified L: LifecycleListener> use() = use(L::class)
    }
}