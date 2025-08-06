package com.github.tanokun.bakajinrou.plugin.scheduler.schedule

import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class QuartzDistribute(
    private val playerProvider: BukkitPlayerProvider
) {

    /**
     * 生存状態の参加者にクオーツを1個配布します。
     *
     * @param participants ゲームの全ての参加者
     */
     fun distributeQuartzToSurvivors(participants: ParticipantScope.All) {
        participants
            .survivedOnly()
            .forEach {
                val bukkitPlayer = playerProvider.get(it) ?: return@forEach

                bukkitPlayer.inventory.addItem(ItemStack(Material.QUARTZ))
            }
    }
}