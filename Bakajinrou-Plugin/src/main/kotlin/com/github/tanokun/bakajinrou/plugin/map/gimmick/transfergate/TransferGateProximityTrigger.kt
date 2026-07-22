package com.github.tanokun.bakajinrou.plugin.map.gimmick.transfergate

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.map.gimmick.GimmickMarkerTags
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.bukkit.Particle
import kotlin.time.Duration.Companion.milliseconds

class TransferGateProximityTrigger(
    private val context: MapGimmickContext,
    private val scheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider,
    private val state: TransferGateState,
    private val flow: TransferGateFlow,
) {
    fun start() {
        mainScope.launch {
            scheduler.observe(mainScope).whenLaunched().take(1).collect {
                while (scheduler.isActive()) {
                    tick()
                    delay(100.milliseconds)
                }
            }
        }
    }

    private fun tick() {
        val now = System.currentTimeMillis()
        state.refresh(now)
        val gates = context.markers(GimmickMarkerTags.TRANSFER_GATE)

        gates.filter { state.isAvailable(it.uniqueId, now) }.forEach { gate ->
            gate.world.spawnParticle(Particle.PORTAL, gate.location, 5, 1.0, 3.0, 1.0, 1.0)
        }

        context.game.getCurrentParticipants()
            .filter { it.isAlive() }
            .forEach { participant ->
                val player = playerProvider.getAllowNull(participant) ?: return@forEach
                val source = gates
                    .filter { it.world == player.world }
                    .minByOrNull { it.location.distanceSquared(player.location) }
                    ?.takeIf { it.location.distanceSquared(player.location) <= 4.0 }
                    ?: return@forEach

                flow.transfer(participant, player, source, gates, now)
            }
    }
}
