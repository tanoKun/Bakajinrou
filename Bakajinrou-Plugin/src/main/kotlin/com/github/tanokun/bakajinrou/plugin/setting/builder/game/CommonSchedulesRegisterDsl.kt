package com.github.tanokun.bakajinrou.plugin.setting.builder.game

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onCancellation
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onCancellationByOvertime
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onLaunching
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class CommonSchedulesRegisterDsl(private val context: DIContext) {
    fun commonSchedules(dsl: Dsl.() -> Unit): Pair<JinrouGame, JinrouGameController> {
        dsl(Dsl())

        return context.get<JinrouGame>() to context.get<JinrouGameController>()
    }

    inner class Dsl {
        private val scheduler = context.get<GameScheduler>()

        fun <L : Any> schedule(klass: KClass<L>, dsl: L.() -> Unit) {
            val instance = context.construct(klass)

            dsl(instance)
        }

        infix fun KFunction<*>.on(timing: Timing): Unit = when (timing) {
            Launching -> {
                val params = context.getParameters(this)

                scheduler.addSchedule(onLaunching {
                    this.call(*params)
                })
            }
            Cancellation -> {
                val params = context.getParameters(this)

                scheduler.addSchedule(onCancellation {
                    this.call(*params)
                })
            }
            OverTime -> {
                val params = context.getParameters(this)

                scheduler.addSchedule(onCancellationByOvertime {
                    this.call(*params)
                })
            }
        }

        inline fun <reified L: Any> schedule(noinline dsl: L.() -> Unit) = schedule(L::class, dsl)
    }
}

sealed interface Timing
object Launching: Timing
object Cancellation: Timing
object OverTime: Timing