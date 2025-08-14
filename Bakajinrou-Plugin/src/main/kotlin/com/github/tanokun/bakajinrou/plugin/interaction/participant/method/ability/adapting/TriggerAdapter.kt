package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.ability.adapting

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ability.Ability
import com.github.tanokun.bakajinrou.api.ability.CommuneAbility
import com.github.tanokun.bakajinrou.api.ability.DivineAbility
import com.github.tanokun.bakajinrou.api.ability.ProtectAbility
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.knight.ProtectAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.medium.CommuneAbilityExecutor
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.method.ability.gui.AbilityGUI
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class TriggerAdapter(
    private val plugin: Plugin,
    private val grantedStrategiesPublisher: GrantedStrategiesPublisher,
    private val mainScope: CoroutineScope,
    private val divineExecutor: DivineAbilityExecutor,
    private val communeExecutor: CommuneAbilityExecutor,
    private val protectExecutor: ProtectAbilityExecutor,
    private val translator: JinrouTranslator,
    private val game: JinrouGame
): Observer {
    private val listeners = hashMapOf<Ability, ClickListener<*>>()

    init {
        mainScope.launch {
            grantedStrategiesPublisher.observeDifference()
                .filterIsInstance<MethodDifference.Granted>()
                .map { it.grantedMethod }
                .filterIsInstance<Ability>()
                .collect(::trigger)
        }

        mainScope.launch {
            grantedStrategiesPublisher.observeDifference()
                .filterIsInstance<MethodDifference.Removed>()
                .map { it.removedMethod }
                .filterIsInstance<Ability>()
                .collect(::cancel)
        }
    }

    private fun trigger(ability: Ability) {
        val listener = when (ability) {
            is DivineAbility -> ClickListener(ability, GameKeys.Gui.Using.DIVINE_DESCRIPTION) { user: ParticipantId, target: ParticipantId ->
                mainScope.launch { divineExecutor.divine(ability, user, target) }
            }

            is CommuneAbility -> ClickListener(ability, GameKeys.Gui.Using.COMMUNE_DESCRIPTION) { user: ParticipantId, target: ParticipantId ->
                mainScope.launch { communeExecutor.commune(ability, user, target) }
            }

            is ProtectAbility -> ClickListener(ability, GameKeys.Gui.Using.PROTECT_DESCRIPTION) { user: ParticipantId, target: ParticipantId ->
                mainScope.launch { protectExecutor.protect(ability, user, target) }
            }
        }

        listeners[ability] = listener
        listener.registerAll()
    }

    private fun cancel(ability: Ability) {
        listeners.remove(ability)?.unregisterAll()
    }

    private inner class ClickListener<T: Ability>(
        ability: T, description: GameKeys.Gui.Using, executor: (user: ParticipantId, target: ParticipantId) -> Unit
    ): LifecycleEventListener(plugin, {
        register<PlayerInteractEvent> { event ->
            if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return@register
            if (event.hand != EquipmentSlot.HAND) return@register
            if (event.item?.getMethodId() != ability.methodId) return@register

            AbilityGUI(translator, game.getCurrentParticipants().excludeSpectators(), description) { clicker, target ->
                mainScope.launch {
                    executor(clicker, target)
                }
            }.open(event.player)
        }
    })
}