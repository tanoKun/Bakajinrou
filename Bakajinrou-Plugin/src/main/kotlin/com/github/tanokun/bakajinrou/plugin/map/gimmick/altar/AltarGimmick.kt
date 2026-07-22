package com.github.tanokun.bakajinrou.plugin.map.gimmick.altar

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
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
class AltarGimmick(
    context: MapGimmickContext,
    scheduler: GameScheduler,
    mainScope: CoroutineScope,
    random: Random,
) : MapGimmick {
    override val id = MapGimmickId.ALTAR

    private val flow = AltarFlow(context, random)
    private val trigger = AltarTrigger(context, scheduler, mainScope, flow)

    override fun start() = trigger.start()

    override fun close() = Unit
}
