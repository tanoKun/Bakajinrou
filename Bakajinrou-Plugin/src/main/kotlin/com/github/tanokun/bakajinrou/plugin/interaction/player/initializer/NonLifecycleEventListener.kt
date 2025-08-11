package com.github.tanokun.bakajinrou.plugin.interaction.player.initializer

import com.github.tanokun.bakajinrou.api.translate.PrefixKeys
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.cache.PlayerSkinCache
import com.github.tanokun.bakajinrou.plugin.common.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.InventoryHolder
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.text

class NonLifecycleEventListener(
    private val gameSettings: GameSettings, private val translator: JinrouTranslator
): Listener {
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

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val uniqueId = e.player.uniqueId
        PlayerNameCache.put(uniqueId, e.player.name)
        PlayerSkinCache.put(uniqueId, e.player.playerProfile)

        if (!gameSettings.spectators.contains(uniqueId)) {
            gameSettings.addCandidate(uniqueId)
            return
        }

        e.player.playerListName(component {
            raw { translator.translate(PrefixKeys.Companion.SPECTATOR, e.player.locale()) }
            text(" ${e.player.name}")
        })
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val uniqueId = e.player.uniqueId

        gameSettings.removeCandidate(uniqueId)
    }
}