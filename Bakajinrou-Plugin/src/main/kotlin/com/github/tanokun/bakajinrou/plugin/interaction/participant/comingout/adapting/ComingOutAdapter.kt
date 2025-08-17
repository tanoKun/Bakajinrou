package com.github.tanokun.bakajinrou.plugin.interaction.participant.comingout.adapting

import com.github.tanokun.bakajinrou.game.participant.comingout.ComingOutHandler
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.comingout.gui.ComingOutGUI
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class ComingOutAdapter(
    plugin: Plugin, translator: JinrouTranslator, handler: ComingOutHandler, mainScope: CoroutineScope
): LifecycleEventListener(plugin, {
    register<PlayerInteractEvent> { event ->
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return@register
        if (event.hand != EquipmentSlot.HAND) return@register
        if (event.item?.persistentDataContainer?.has(COMING_OUT_ADAPTER_KEY) != true) return@register

        event.isCancelled = true

        ComingOutGUI(translator, handler, mainScope).open(event.player)
    }

    register<PlayerDropItemEvent> { event ->
        if (!event.itemDrop.itemStack.persistentDataContainer.has(COMING_OUT_ADAPTER_KEY)) return@register

        event.isCancelled = true
    }
})