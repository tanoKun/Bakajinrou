package com.github.tanokun.bakajinrou.plugin.map.gimmick.overlook

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.map.gimmick.GimmickMarkerTags
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.bukkit.Location
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

class OverlookTrigger(
    private val context: MapGimmickContext,
    private val scheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider,
    private val state: OverlookState,
    private val flow: OverlookFlow,
) {
    fun start() {
        mainScope.launch {
            scheduler.observe(mainScope).whenLaunched().take(1).collect {
                while (scheduler.isActive()) {
                    scanEntrants()
                    delay(100.milliseconds)
                }
            }
        }
    }

    private fun scanEntrants() {
        val entry = context.markerLocation(
            GimmickMarkerTags.OVERLOOK_ENTRY,
            context.legacyLocation(-151.5, 13.0, 90.5),
        )

        context.game.getCurrentParticipants()
            .filter { it.isAlive() && !state.isActive(it.participantId) }
            .forEach { participant ->
                val player = playerProvider.getAllowNull(participant) ?: return@forEach
                if (!player.location.isNear(entry, 1.5)) return@forEach
                mainScope.launch { flow.enter(participant.participantId, player) }
            }
    }

    private fun Location.isNear(other: Location, horizontal: Double): Boolean =
        world == other.world &&
            abs(x - other.x) <= horizontal &&
            abs(z - other.z) <= horizontal &&
            abs(y - other.y) <= 1.5
}
