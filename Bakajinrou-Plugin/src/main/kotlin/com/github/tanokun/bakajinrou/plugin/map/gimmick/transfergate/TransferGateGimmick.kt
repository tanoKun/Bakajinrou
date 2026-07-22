package com.github.tanokun.bakajinrou.plugin.map.gimmick.transfergate

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmick
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickId
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.random.Random

@Scoped(binds = [MapGimmick::class])
@Scope(value = GameComponents::class)
class TransferGateGimmick(
    context: MapGimmickContext,
    scheduler: GameScheduler,
    mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
    random: Random,
) : MapGimmick {
    override val id = MapGimmickId.TRANSFER_GATE

    private val state = TransferGateState()
    private val flow = TransferGateFlow(context, state, random)
    private val trigger = TransferGateProximityTrigger(context, scheduler, mainScope, playerProvider, state, flow)

    override fun start() = trigger.start()

    override fun close() = state.clear()
}
