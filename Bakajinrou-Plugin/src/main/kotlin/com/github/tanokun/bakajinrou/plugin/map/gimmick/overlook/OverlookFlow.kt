package com.github.tanokun.bakajinrou.plugin.map.gimmick.overlook

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.game.initialization.asBukkit
import com.github.tanokun.bakajinrou.plugin.map.GameMap
import com.github.tanokun.bakajinrou.plugin.map.gimmick.GimmickMarkerTags
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.time.Duration.Companion.seconds

class OverlookFlow(
    private val context: MapGimmickContext,
    private val playerProvider: BukkitPlayerProvider,
    private val gameMap: GameMap,
    private val state: OverlookState,
) {
    suspend fun enter(participantId: ParticipantId, player: Player) {
        if (!state.enter(participantId)) return
        if (!context.consumeQuartz(player, QUARTZ_COST)) {
            state.leave(participantId)
            if (state.shouldNotifyFailure(participantId, System.currentTimeMillis())) {
                player.sendMessage(Component.text("水晶が足りません。", NamedTextColor.RED))
            }
            return
        }

        try {
            val destination = context.markerLocation(
                GimmickMarkerTags.OVERLOOK_DESTINATION,
                context.legacyLocation(-148.0, 28.0, 93.0),
            )
            player.teleport(destination)

            delay(19.seconds)
            playerProvider.waitPlayerOnline(participantId).addPotionEffect(
                PotionEffect(PotionEffectType.INVISIBILITY, 16 * 20, 1, true, false)
            )

            delay(1.seconds)
            playerProvider.waitPlayerOnline(participantId).teleport(gameMap.spawnPoint.asBukkit())
        } finally {
            state.leave(participantId)
        }
    }

    private companion object {
        const val QUARTZ_COST = 1
    }
}
