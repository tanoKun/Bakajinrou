package com.github.tanokun.bakajinrou.plugin.scheduler.schedule

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.Participant
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class QuartzDistribute(
    private val getBukkitPlayer: (Participant) -> Player?
) {
    /**
     * 生存状態の参加者にクオーツを1個配布します。
     *
     * @param jinrouGame 干渉先のゲーム
     */
    fun distributeQuartzToSurvivors(jinrouGame: JinrouGame) {
        jinrouGame.participants
            .filter { it.state == ParticipantStates.SURVIVED }
            .forEach {
                val bukkitPlayer = getBukkitPlayer(it) ?: return@forEach

                bukkitPlayer.inventory.addItem(ItemStack(Material.QUARTZ))
            }
    }
}