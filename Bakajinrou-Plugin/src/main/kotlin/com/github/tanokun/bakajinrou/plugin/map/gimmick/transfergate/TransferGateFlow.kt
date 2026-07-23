package com.github.tanokun.bakajinrou.plugin.map.gimmick.transfergate

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.isFox
import com.github.tanokun.bakajinrou.plugin.map.gimmick.GimmickMarkerTags
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import kotlin.random.Random

class TransferGateFlow(
    private val context: MapGimmickContext,
    private val state: TransferGateState,
    private val random: Random,
) {
    fun transfer(
        participant: Participant,
        player: Player,
        source: ArmorStand,
        gates: List<ArmorStand>,
        now: Long,
    ) {
        if (!state.isAvailable(source.uniqueId, now)) {
            notifyFailure(player, "このゲートは現在使用できません。", now)
            return
        }
        if (GimmickMarkerTags.TRANSFER_GATE_FOX_ONLY in source.scoreboardTags && !isFox(participant)) {
            notifyFailure(player, "このゲートは妖狐専用です。", now)
            return
        }

        val destinations = gates.filter { it.uniqueId != source.uniqueId }
        if (destinations.isEmpty()) return
        if (!context.consumeQuartz(player, QUARTZ_COST)) {
            notifyFailure(player, "実行できません。クォーツが${QUARTZ_COST}個必要です。", now)
            return
        }

        val destination = destinations.random(random)
        state.disable(destination.uniqueId, now + DESTINATION_COOLDOWN_MILLIS)
        player.teleport(destination.location)
    }

    private fun notifyFailure(player: Player, message: String, now: Long) {
        if (!state.shouldNotify(player.uniqueId, now)) return
        player.sendMessage(Component.text(message, NamedTextColor.RED))
    }

    private companion object {
        const val QUARTZ_COST = 1
        const val DESTINATION_COOLDOWN_MILLIS = 20_000L
    }
}
