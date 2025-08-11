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
import com.github.tanokun.bakajinrou.game.participant.initialization.InherentMethodsInitializer
import com.github.tanokun.bakajinrou.game.protect.ProtectVerificatorProvider
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.adapter.protect.BukkitProtectVerificatorProvider
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.interaction.method.ability.use.ProtectObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.ability.use.TriggerUsingAbility
import com.github.tanokun.bakajinrou.plugin.interaction.method.ability.use.fortune.FoxDivineObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.ability.use.fortune.UsedDivineObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.ability.use.medium.UsedCommuneObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.attack.AttackByBowListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.attack.AttackByPotionListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.attack.AttackBySwordListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.attack.consume.OnAttackWithResistanceObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.attack.consume.OnAttackWithShieldObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.attack.consume.OnAttackWithTotemObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.craft.OnCraftEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.hidden.HiddenItemListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.resistance.activate.UseResistancePotion
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.SyncRemoveInventoryObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.ability.sync.SyncGrantCommuneAbilityObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.ability.sync.SyncGrantDivineAbilityObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.ability.sync.SyncGrantProtectAbilityObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.advantage.sync.SyncGrantExchangeMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.advantage.sync.SyncGrantInvisibilityMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.advantage.sync.SyncGrantSpeedMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.attack.sync.SyncGrantArrowMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.attack.sync.SyncGrantDamagePotionMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.attack.sync.SyncGrantSwordMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.protective.sync.SyncGrantFakeMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.protective.sync.SyncGrantResistanceMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.protective.sync.SyncGrantShieldMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.protective.sync.SyncGrantTotemMethodObserver
import com.github.tanokun.bakajinrou.plugin.interaction.method.transfer.TransferMethodListener
import com.github.tanokun.bakajinrou.plugin.interaction.participant.chat.ParticipantChatEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.participant.initialization.WolfInitializer
import com.github.tanokun.bakajinrou.plugin.interaction.participant.state.dead.DeathConfirmedObserver
import com.github.tanokun.bakajinrou.plugin.interaction.participant.state.dead.body.BukkitBodyHandler
import com.github.tanokun.bakajinrou.plugin.interaction.participant.state.update.TabListPacketListener
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
        singleOf(::JinrouLogger) bind GameLogger::class
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
            scopedOf(::UsedDivineObserver) bind Observer::class
            scopedOf(::FoxDivineObserver) bind Observer::class
            scopedOf(::UsedCommuneObserver) bind Observer::class
            scopedOf(::ProtectObserver) bind Observer::class

            scopedOf(::TriggerUsingAbility) bind Observer::class

            //ゲーム開始時の初期化
            scopedOf(::CommonGameInitializer) bind Observer::class
            scopedOf(::InherentMethodsInitializer) bind Observer::class
            scopedOf(::WolfInitializer) bind Observer::class

            // 手段 -> Bukkitのアイテム
            scopedOf(::SyncGrantCommuneAbilityObserver) bind Observer::class
            scopedOf(::SyncGrantDivineAbilityObserver) bind Observer::class
            scopedOf(::SyncGrantProtectAbilityObserver) bind Observer::class

            scopedOf(::SyncGrantExchangeMethodObserver) bind Observer::class
            scopedOf(::SyncGrantInvisibilityMethodObserver) bind Observer::class
            scopedOf(::SyncGrantSpeedMethodObserver) bind Observer::class

            scopedOf(::SyncGrantArrowMethodObserver) bind Observer::class
            scopedOf(::SyncGrantDamagePotionMethodObserver) bind Observer::class
            scopedOf(::SyncGrantSwordMethodObserver) bind Observer::class

            scopedOf(::SyncGrantFakeMethodObserver) bind Observer::class
            scopedOf(::SyncGrantResistanceMethodObserver) bind Observer::class
            scopedOf(::SyncGrantShieldMethodObserver) bind Observer::class
            scopedOf(::SyncGrantTotemMethodObserver) bind Observer::class

            scopedOf(::SyncRemoveInventoryObserver) bind Observer::class

            //攻撃系
            scopedOf(::OnAttackWithTotemObserver) bind Observer::class
            scopedOf(::OnAttackWithResistanceObserver) bind Observer::class
            scopedOf(::OnAttackWithShieldObserver) bind Observer::class
//----------------------------------------------------------------------------------------------------------------------
            // 最上位監視
            scoped { CommonGameFinisher(get(), get(), get(), topScope) } bind Observer::class
            scoped { JudgeGameObserver(get(), get(), get(), get(), topScope) }  bind Observer::class
        }
    }

    val listenersModule = module {
        scope<GameComponentSession> {
            scopedOf(::BindingListeners) onClose { it?.close() }

            scopedOf(::AttackBySwordListener) bind LifecycleListener::class
            scopedOf(::AttackByBowListener) bind LifecycleListener::class
            scopedOf(::AttackByPotionListener) bind LifecycleListener::class
            scopedOf(::OnCraftEventListener) bind LifecycleListener::class
            scopedOf(::TabListPacketListener) bind LifecycleListener::class
            scopedOf(::ParticipantChatEventListener) bind LifecycleListener::class
            scopedOf(::HiddenItemListener) bind LifecycleListener::class
            scopedOf(::TransferMethodListener) bind LifecycleListener::class
            scopedOf(::UseResistancePotion) bind LifecycleListener::class
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
            scopedOf(::BukkitProtectVerificatorProvider) bind ProtectVerificatorProvider::class
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