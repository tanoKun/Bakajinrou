package com.github.tanokun.bakajinrou.plugin.listener.always

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.InventoryHolder

class NonLifecycleEventListener: Listener {
    @EventHandler
    fun onFall(e: EntityDamageEvent) {
        if (e.entity !is Player) return
        if (e.cause == EntityDamageEvent.DamageCause.FALL) e.isCancelled = true
    }

    @EventHandler
    fun onClickAtInventoryHolder(e: PlayerInteractEvent) {
        if (e.player.isOp) return
        if (e.action != Action.RIGHT_CLICK_BLOCK) return

        if (e.clickedBlock?.state is InventoryHolder) e.isCancelled = true
    }
}