package com.github.tanokun.bakajinrou.plugin.map.gimmick.hijack

import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class HijackFlow(
    private val context: MapGimmickContext,
    private val playerProvider: BukkitPlayerProvider,
) {
    fun activate(player: Player, floor: HijackFloor) {
        if (!context.consumeQuartz(player, QUARTZ_COST)) {
            player.sendMessage(Component.text("水晶が足りません。", NamedTextColor.RED))
            return
        }

        context.game.getCurrentParticipants()
            .filter { it.isAlive() && !isWolf(it) }
            .mapNotNull(playerProvider::getAllowNull)
            .filter { it.location.blockY in floor.yRange }
            .forEach { target ->
                target.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, EFFECT_TICKS, 255, true, false))
                target.sendMessage(Component.text("ハイジャックにより視界を奪われた！", NamedTextColor.DARK_RED))
            }

        context.broadcast(
            Component.text("何者かが${floor.displayName}をハイジャックした。", NamedTextColor.RED)
        )
    }

    private companion object {
        const val QUARTZ_COST = 6
        const val EFFECT_TICKS = 20 * 20
    }
}
