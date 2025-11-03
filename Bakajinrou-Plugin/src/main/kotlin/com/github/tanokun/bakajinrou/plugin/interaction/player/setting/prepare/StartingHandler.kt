package com.github.tanokun.bakajinrou.plugin.interaction.player.setting.prepare

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot


class StartingHandler: Listener {

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        if (e.hand != EquipmentSlot.HAND) return
    }
}