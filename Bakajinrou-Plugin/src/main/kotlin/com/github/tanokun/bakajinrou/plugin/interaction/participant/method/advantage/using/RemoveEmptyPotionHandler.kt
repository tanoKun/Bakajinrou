package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.advantage.using

import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import org.bukkit.Material
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class RemoveEmptyPotionHandler(plugin: Plugin): LifecycleEventListener(plugin, {
    register<PlayerItemConsumeEvent> { event ->
        if (event.replacement?.type != Material.GLASS_BOTTLE) return@register
        event.replacement = ItemStack.of(Material.AIR)
    }
})