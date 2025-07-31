package com.github.tanokun.bakajinrou.plugin.setting.factory

import com.comphenix.protocol.ProtocolManager
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.game.controller.AttackController
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onCancellation
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onLaunching
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.PlayerConnectionEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.attack.OnAttackByBowEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.attack.OnAttackByPotionEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.attack.OnAttackBySwordEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.item.OnCraftEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.item.OptionalMethodEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.item.SecretItemPacketListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.item.TransportMethodEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.tab.TabListPacketListener
import com.github.tanokun.bakajinrou.plugin.method.BukkitItemFactory
import org.bukkit.plugin.Plugin

class GameListenerRegistry(
    plugin: Plugin,
    jinrouGame: JinrouGame,
    attackController: AttackController,
    itemFactory: BukkitItemFactory,
    protocolManager: ProtocolManager
) {
    private val listeners: List<LifecycleListener> =
        listOf(
            OnAttackByBowEventListener(plugin, jinrouGame, attackController),
            OnAttackByPotionEventListener(plugin, jinrouGame, attackController),
            OnAttackBySwordEventListener(plugin, jinrouGame, attackController),
            PlayerConnectionEventListener(plugin, jinrouGame),
            OnCraftEventListener(plugin, jinrouGame, itemFactory),
            OptionalMethodEventListener(plugin, jinrouGame),
            TransportMethodEventListener(plugin, jinrouGame),
            TabListPacketListener(plugin, jinrouGame, protocolManager),
            SecretItemPacketListener(plugin, jinrouGame, protocolManager)
        )

    fun asRegisterSchedule() = onLaunching {
        listeners.forEach(LifecycleListener::registerAll)
    }

    fun asUnRegisterSchedule() = onCancellation {
        listeners.forEach(LifecycleListener::unregisterAll)
    }
}