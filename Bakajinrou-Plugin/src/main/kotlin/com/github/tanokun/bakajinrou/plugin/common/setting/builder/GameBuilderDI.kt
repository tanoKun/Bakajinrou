package com.github.tanokun.bakajinrou.plugin.common.setting.builder

import com.comphenix.protocol.ProtocolLibrary
import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.UpdateMutexProvider
import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.knight.ProtectAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.medium.CommuneAbilityExecutor
import com.github.tanokun.bakajinrou.game.attack.Attacking
import com.github.tanokun.bakajinrou.game.attack.BodyHandler
import com.github.tanokun.bakajinrou.game.chat.ChatIntegrity
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.game.logger.GameLogger
import com.github.tanokun.bakajinrou.game.method.transfer.TransferMethod
import com.github.tanokun.bakajinrou.game.participant.observer.initialization.InherentMethodsInitializer
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.interaction.method.ability.observe.CommuneObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.ability.observe.ProtectObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.ability.observe.TriggerUsingAbility
import com.github.tanokun.bakajinrou.plugin.interaction.method.ability.observe.fortune.DivineObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.attack.listen.OnAttackByBowEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.attack.listen.OnAttackByPotionEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.attack.listen.OnAttackBySwordEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.craft.listen.OnCraftEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.hidden.listener.HiddenItemListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.ability.grant.observe.AddSyncCommuneAbilityObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.ability.grant.observe.AddSyncDivineAbilityObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.ability.grant.observe.AddSyncProtectAbilityObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.advantage.grant.observe.AddSyncExchangeMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.advantage.grant.observe.AddSyncInvisibilityMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.advantage.grant.observe.AddSyncSpeedMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.attack.grant.observe.AddSyncArrowMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.attack.grant.observe.AddSyncDamagePotionMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.attack.grant.observe.AddSyncSwordMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.protective.grant.observe.AddSyncFakeMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.protective.grant.observe.AddSyncResistanceMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.protective.grant.observe.AddSyncShieldMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.protective.grant.observe.AddSyncTotemMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.remove.observe.RemoveSyncInventoryObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.transfer.listen.TransferMethodListener
import com.github.tanokun.bakajinrou.plugin.interaction.participant.chat.listen.ParticipantChatEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.participant.initialization.observe.WolfInitializer
import com.github.tanokun.bakajinrou.plugin.interaction.participant.state.dead.body.BukkitBodyHandler
import com.github.tanokun.bakajinrou.plugin.interaction.participant.state.dead.observe.DeathConfirmedObserver
import com.github.tanokun.bakajinrou.plugin.interaction.participant.state.update.listen.TabListPacketListener
import com.github.tanokun.bakajinrou.plugin.interaction.participant.state.update.view.TabListModifier
import com.github.tanokun.bakajinrou.plugin.logger.JinrouLogger
import com.github.tanokun.bakajinrou.plugin.system.game.finish.observe.CommonGameFinisher
import com.github.tanokun.bakajinrou.plugin.system.game.finish.observe.JudgeGameObserver
import com.github.tanokun.bakajinrou.plugin.system.game.launch.observe.CommonGameInitializer
import com.github.tanokun.bakajinrou.plugin.system.scheduler.JinrouGameScheduler
import com.github.tanokun.bakajinrou.plugin.system.scheduler.observe.GlowingAnnouncer
import com.github.tanokun.bakajinrou.plugin.system.scheduler.observe.HiddenPositionAnnouncer
import com.github.tanokun.bakajinrou.plugin.system.scheduler.observe.QuartzDistribution
import com.github.tanokun.bakajinrou.plugin.system.scheduler.observe.TimeAnnouncer
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

class GameBuilderDI(plugin: Plugin) {
    val singleModule = module {
        single { plugin }
        single { plugin.logger }
        singleOf(ProtocolLibrary::getProtocolManager)
        singleOf(Bukkit::getServer)
        singleOf(Bukkit::getScheduler)
        singleOf<GameLogger>(::JinrouLogger)
        singleOf(::DebugLogger)
        singleOf(::BukkitPlayerProvider)
        single { ChatIntegrity }
    }

    private val topScope by lazy { CoroutineScope(plugin.scope.coroutineContext) }

    val observersModule = module {
        scope<GameComponentSession> {
            //スケジュール系
            scopedOf(::TimeAnnouncer) bind Observer::class
            scopedOf(::HiddenPositionAnnouncer) bind Observer::class
            scopedOf(::GlowingAnnouncer) bind Observer::class
            scopedOf(::QuartzDistribution) bind Observer::class

            //プレイヤー状態
            scopedOf(::DeathConfirmedObserver) bind Observer::class

            //ability
            scopedOf(::CommuneObserver) bind Observer::class
            scopedOf(::DivineObserver) bind Observer::class
            scopedOf(::ProtectObserver) bind Observer::class

            scopedOf(::TriggerUsingAbility) bind Observer::class

            //ゲーム開始時の初期化
            scopedOf(::CommonGameInitializer) bind Observer::class
            scopedOf(::InherentMethodsInitializer) bind Observer::class
            scopedOf(::WolfInitializer) bind Observer::class

            // 手段 -> Bukkitのアイテム
            scopedOf(::AddSyncCommuneAbilityObserver) bind Observer::class
            scopedOf(::AddSyncDivineAbilityObserver) bind Observer::class
            scopedOf(::AddSyncProtectAbilityObserver) bind Observer::class

            scopedOf(::AddSyncExchangeMethodObserver) bind Observer::class
            scopedOf(::AddSyncInvisibilityMethodObserver) bind Observer::class
            scopedOf(::AddSyncSpeedMethodObserver) bind Observer::class

            scopedOf(::AddSyncArrowMethodObserver) bind Observer::class
            scopedOf(::AddSyncDamagePotionMethodObserver) bind Observer::class
            scopedOf(::AddSyncSwordMethodObserver) bind Observer::class

            scopedOf(::AddSyncFakeMethodObserver) bind Observer::class
            scopedOf(::AddSyncResistanceMethodObserver) bind Observer::class
            scopedOf(::AddSyncShieldMethodObserver) bind Observer::class
            scopedOf(::AddSyncTotemMethodObserver) bind Observer::class

            scopedOf(::RemoveSyncInventoryObserver) bind Observer::class
//----------------------------------------------------------------------------------------------------------------------
            // 最上位監視
            scoped { CommonGameFinisher(get(), get(), get(), topScope) } bind Observer::class
            scoped { JudgeGameObserver(get(), get(), get(), get(), topScope) }  bind Observer::class
        }
    }

    val listenersModule = module {
        scope<GameComponentSession> {
            scopedOf(::BindingListeners) onClose { it?.close() }

            scopedOf(::OnAttackBySwordEventListener) bind LifecycleListener::class
            scopedOf(::OnAttackByBowEventListener) bind LifecycleListener::class
            scopedOf(::OnAttackByPotionEventListener) bind LifecycleListener::class
            scopedOf(::OnCraftEventListener) bind LifecycleListener::class
            scopedOf(::TabListPacketListener) bind LifecycleListener::class
            scopedOf(::ParticipantChatEventListener) bind LifecycleListener::class
            scopedOf(::HiddenItemListener) bind LifecycleListener::class
            scopedOf(::TransferMethodListener) bind LifecycleListener::class
        }
    }

    val otherModule = module {
        scope<GameComponentSession> {
            scopedOf(::Crafting)
            scopedOf(::TabListModifier)
            scopedOf(::TransferMethod)
            scopedOf(::DivineAbilityExecutor)
            scopedOf(::CommuneAbilityExecutor)
            scopedOf(::ProtectAbilityExecutor)
            scopedOf(::GrantedStrategiesPublisher)
            scopedOf(::BukkitBodyHandler) bind BodyHandler::class
        }
    }

    val gameBuildScopeModule = module {
        includes(singleModule, observersModule, listenersModule, otherModule)

        single<Random> { Random.Default }

        scope<GameComponentSession> {
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
                    topScope = topScope
                )
            }

            scoped<CoroutineScope> { get<JinrouGameSession>().mainDispatcherScope }

            scopedOf(::Attacking)


        }
    }
}