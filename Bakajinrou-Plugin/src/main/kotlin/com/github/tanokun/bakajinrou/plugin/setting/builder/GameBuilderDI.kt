package com.github.tanokun.bakajinrou.plugin.setting.builder

import com.comphenix.protocol.ProtocolLibrary
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesProvider
import com.github.tanokun.bakajinrou.game.attack.AttackController
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.game.logger.GameLogger
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.plugin.logger.JinrouLogger
import com.github.tanokun.bakajinrou.plugin.scheduler.JinrouGameScheduler
import com.github.tanokun.bakajinrou.plugin.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.setting.builder.observer.BindingObservers
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import java.util.*
import kotlin.random.Random
import kotlin.time.Duration

class GameBuilderDI(plugin: Plugin) {
    val singleModule = module {
        single { plugin }
        single { plugin.logger }
        singleOf(ProtocolLibrary::getProtocolManager)
        singleOf(Bukkit::getServer)
        singleOf(Bukkit::getScheduler)
        singleOf<GameLogger>(::JinrouLogger)
        singleOf(::DebugLogger)
    }

    val observersModule = module {
        scope<GameComponentSession> {
            scopedOf(::BindingObservers)
        }
    }

    val providersModule = module {
        scope<GameComponentSession> {
            scopedOf(::GrantedStrategiesProvider)
        }
    }

    val gameBuildScopeModule = module {
        single<Random> { Random.Default }

        scope<GameComponentSession> {
            scoped<ParticipantBuilder> { (template: HashMap<RequestedPositions, Int>, candidates: Set<UUID>) ->
                ParticipantBuilder(template, candidates, get<Random>())
            }

            scoped<GameScheduler> { (timer: Duration) ->
                JinrouGameScheduler(startTime = timer, bukkitScheduler = get(), plugin = get())
            }

            scoped<JinrouGame> { (participants: ParticipantScope.All) ->
                JinrouGame(participants)
            }

            scoped<JinrouGameController> { (participants: ParticipantScope.All, mainScope: CoroutineScope, timer: Duration) ->
                JinrouGameController(
                    game = get { parametersOf(participants) },
                    scheduler = get { parametersOf(timer) },
                    debug = get(),
                    mainScope = mainScope
                )
            }

            scoped<CoroutineScope> { get<JinrouGameController>().mainDispatcherScope }

            scopedOf(::AttackController)
        }
    }
}