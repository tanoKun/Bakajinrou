package com.github.tanokun.bakajinrou.plugin.module

import com.comphenix.protocol.ProtocolLibrary
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.UpdateMutexProvider
import com.github.tanokun.bakajinrou.api.advantage.using.ExchangeSelector
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.knight.ProtectAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.medium.CommuneAbilityExecutor
import com.github.tanokun.bakajinrou.game.attacking.Attacking
import com.github.tanokun.bakajinrou.game.chat.ChatIntegrity
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.method.advantage.using.LocationExchanger
import com.github.tanokun.bakajinrou.game.method.resistance.activator.ResistanceActivator
import com.github.tanokun.bakajinrou.game.method.transferring.TransferMethod
import com.github.tanokun.bakajinrou.game.participant.comingout.ComingOutHandler
import com.github.tanokun.bakajinrou.game.participant.initialization.InherentMethodsInitializer
import com.github.tanokun.bakajinrou.game.participant.state.suspended.ChangeSuspended
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.coroutine.TopCoroutineScope
import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.BindingListeners
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder
import com.github.tanokun.bakajinrou.plugin.interaction.game.scheduler.JinrouGameScheduler
import com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.team.modifier.ViewTeamModifier
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose
import java.util.*
import kotlin.random.Random
import kotlin.time.Duration

class GameBuilderModule(plugin: Plugin) {
    val bukkitModule = module {
        single { plugin }
        single { plugin.logger }
        singleOf(ProtocolLibrary::getProtocolManager)
        singleOf(Bukkit::getServer)
        singleOf(Bukkit::getScheduler)
    }

    val observersModule = module {
        scope<GameComponents> {
            scopedOf(::InherentMethodsInitializer) bind Observer::class
        }
    }

    val listenersModule = module {
        scope<GameComponents> {
            scopedOf(::BindingListeners) onClose { it?.close() }
        }
    }

    val methodModule = module {
        scope<GameComponents> {
            scopedOf(::DivineAbilityExecutor)
            scopedOf(::CommuneAbilityExecutor)
            scopedOf(::ProtectAbilityExecutor)
            scopedOf(::ResistanceActivator)
            scopedOf(::ExchangeSelector)
            scopedOf(::LocationExchanger)
            scopedOf(::Crafting)
            scopedOf(::TransferMethod)
        }
    }

    val participantModule = module {
        scope<GameComponents> {
            scopedOf(::GrantedStrategiesPublisher)
            scopedOf(::Attacking)
            scopedOf(::ChangeSuspended)
            scopedOf(::ComingOutHandler)
        }
    }

    val renderingModule = module {
        single { ChatIntegrity }
        single { Terminal(interactive = true, ansiLevel = AnsiLevel.TRUECOLOR) }

        scope<GameComponents> {
            scoped { ViewTeamModifier(get(), get(), get<JinrouGame>().getCurrentParticipants().excludeSpectators()) }
        }
    }

    val gameBuildScopeModule = module {
        includes(bukkitModule, observersModule, listenersModule, methodModule, participantModule, renderingModule)

        single<Random> { Random.Default }

        scope<GameComponents> {
            scoped<ParticipantBuilder> { (template: HashMap<RequestedPositions, Int>, candidates: Set<UUID>) ->
                ParticipantBuilder(template, candidates, get<Random>())
            }

            scoped<GameScheduler> { (timer: Duration) ->
                JinrouGameScheduler(startTime = timer, bukkitScheduler = get(), plugin = get())
            }

            scoped<JinrouGame> { (participants: ParticipantScope.All) ->
                JinrouGame(UpdateMutexProvider(), participants)
            }

            scoped<JinrouGameSession> { (participants: ParticipantScope.All, mainScope: CoroutineScope, timer: Duration) ->
                JinrouGameSession(
                    game = get { parametersOf(participants) },
                    scheduler = get { parametersOf(timer) },
                    debug = get(),
                    topScope = get<TopCoroutineScope>()
                )
            }

            scoped<TopCoroutineScope> { TopCoroutineScope(CoroutineScope(plugin.scope.coroutineContext)) }
            scoped<CoroutineScope> { get<JinrouGameSession>().mainDispatcherScope }
        }
    }
}