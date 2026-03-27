package com.github.tanokun.bakajinrou.plugin.setting.start.adapter

import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.adapter.getSelectedData
import com.github.tanokun.bakajinrou.plugin.setting.start.GameStarter
import com.github.tanokun.bakajinrou.plugin.setting.start.adapter.gui.FinalDecisionGui
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class StartGameAdapter(private val starter: GameStarter): Listener {
    @EventHandler
    fun tryStartingGame(e: PlayerInteractEvent) {
        if (e.hand != EquipmentSlot.HAND || e.action.isLeftClick) return

        val item = e.player.inventory.itemInMainHand
        if (item.type != Material.FILLED_MAP) return

        val (map, participants, positions) = getSelectedData(item) ?: return
        FinalDecisionGui(map, participants, positions, starter).open(e.player)
    }
}