package com.github.tanokun.bakajinrou.plugin.map.gimmick.hijack

import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext

class HijackItemInitializer(
    private val context: MapGimmickContext,
    private val playerProvider: BukkitPlayerProvider,
    private val items: HijackItems,
) {
    fun initialize() {
        context.game.getCurrentParticipants()
            .filter(::isWolf)
            .mapNotNull(playerProvider::getAllowNull)
            .forEach { player ->
                HijackFloor.entries.forEach { floor ->
                    player.inventory.setItem(floor.slot, items.create(floor))
                }
            }
    }

    fun clear() {
        context.game.getCurrentParticipants()
            .mapNotNull(playerProvider::getAllowNull)
            .forEach { player ->
                HijackFloor.entries.forEach { floor ->
                    if (items.floorOf(player.inventory.getItem(floor.slot)) != null) {
                        player.inventory.setItem(floor.slot, null)
                    }
                }
            }
    }
}
