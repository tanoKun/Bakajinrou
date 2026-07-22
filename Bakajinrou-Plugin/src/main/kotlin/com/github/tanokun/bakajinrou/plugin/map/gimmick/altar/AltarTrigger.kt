package com.github.tanokun.bakajinrou.plugin.map.gimmick.altar

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.map.gimmick.GimmickMarkerTags
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.bukkit.block.Dispenser
import kotlin.time.Duration.Companion.milliseconds

class AltarTrigger(
    private val context: MapGimmickContext,
    private val scheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    private val flow: AltarFlow,
) {
    fun start() {
        mainScope.launch {
            scheduler.observe(mainScope).whenLaunched().take(1).collect {
                while (scheduler.isActive()) {
                    findAltar()?.let { flow.activateIfOffered(it) }
                    delay(250.milliseconds)
                }
            }
        }
    }

    private fun findAltar(): Dispenser? {
        val legacy = context.legacyLocation(123.0, 13.0, 107.0)
        val center = context.markerLocation(GimmickMarkerTags.ALTAR, legacy).block
        val candidates = sequenceOf(center, center.getRelative(0, -1, 0)) + sequence {
            for (x in -1..1) for (y in -1..1) for (z in -1..1) {
                yield(center.getRelative(x, y, z))
            }
        }

        return candidates.map { it.state }.filterIsInstance<Dispenser>().firstOrNull()
    }
}
