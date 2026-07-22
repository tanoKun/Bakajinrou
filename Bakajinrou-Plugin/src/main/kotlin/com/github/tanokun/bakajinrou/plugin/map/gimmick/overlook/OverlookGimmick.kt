package com.github.tanokun.bakajinrou.plugin.map.gimmick.overlook

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.map.GameMap
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmick
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickId
import com.github.tanokun.bakajinrou.plugin.participant.method.advantage.ExchangeTargetExclusions
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [MapGimmick::class])
@Scope(value = GameComponents::class)
class OverlookGimmick(
    context: MapGimmickContext,
    scheduler: GameScheduler,
    mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
    gameMap: GameMap,
    exchangeTargetExclusions: ExchangeTargetExclusions,
) : MapGimmick {
    override val id = MapGimmickId.OVERLOOK

    private val state = OverlookState(exchangeTargetExclusions)
    private val flow = OverlookFlow(context, playerProvider, gameMap, state)
    private val trigger = OverlookTrigger(context, scheduler, mainScope, playerProvider, state, flow)

    override fun start() = trigger.start()

    override fun close() = state.clear()
}
