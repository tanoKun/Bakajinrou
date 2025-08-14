package com.github.tanokun.bakajinrou.plugin.interaction.method.advantage.trigger

import com.github.tanokun.bakajinrou.api.advantage.ExchangeMethod
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.game.method.advantage.using.LocationExchanger
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
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

/**
 * 交換能力([ExchangeMethod])の付与・削除を監視し、プレイヤーの右クリック操作と結びつけます。
 *
 * このクラスは、能力がプレイヤーに付与された際に、対応するクリックリスナーを動的に登録し、
 * 能力が削除された際に、そのリスナーを登録解除するライフサイクル管理を担当します。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class TriggerExchangeAdvantage(
    private val plugin: Plugin,
    private val grantedStrategiesPublisher: GrantedStrategiesPublisher,
    private val locationExchanger: LocationExchanger,
    private val mainScope: CoroutineScope,
): Observer {
    private val listeners = hashMapOf<ExchangeMethod, ClickListener>()

    init {
        mainScope.launch {
            grantedStrategiesPublisher.observeDifference()
                .filterIsInstance<MethodDifference.Granted>()
                .map { it.grantedMethod }
                .filterIsInstance<ExchangeMethod>()
                .collect(::trigger)
        }

        mainScope.launch {
            grantedStrategiesPublisher.observeDifference()
                .filterIsInstance<MethodDifference.Removed>()
                .map { it.removedMethod }
                .filterIsInstance<ExchangeMethod>()
                .collect(::cancel)
        }
    }

    private fun trigger(ability: ExchangeMethod) {

        listeners[ability] = ClickListener(ability).apply { registerAll() }
    }

    private fun cancel(ability: ExchangeMethod) {
        listeners.remove(ability)?.unregisterAll()
    }

    private inner class ClickListener(
        exchange: ExchangeMethod,
    ): LifecycleEventListener(plugin, {
        register<PlayerInteractEvent> { event ->
            if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return@register
            if (event.hand != EquipmentSlot.HAND) return@register
            if (event.item?.getMethodId() != exchange.methodId) return@register

            event.isCancelled = true

            mainScope.launch { locationExchanger.exchange(exchange, event.player.uniqueId.asParticipantId()) }
        }
    })
}