package com.github.tanokun.bakajinrou.plugin.map.gimmick.merchant

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickId
import com.github.tanokun.bakajinrou.plugin.map.gimmick.lifecycle.MapGimmick
import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [MapGimmick::class])
@Scope(value = GameComponents::class)
class SuspiciousMerchantGimmick(
    plugin: Plugin,
    context: MapGimmickContext,
    scheduler: GameScheduler,
    mainScope: CoroutineScope,
    crafting: Crafting,
) : MapGimmick {
    override val id = MapGimmickId.SUSPICIOUS_MERCHANT

    private val flow = SuspiciousMerchantFlow(context)
    private val trigger = SpawnSuspiciousMerchantOnRemainingTime(scheduler, mainScope, flow)
    private val craftListener = CraftSuspiciousMerchantByTradeListener(plugin, crafting, mainScope)

    override fun start() {
        craftListener.registerAll()
        trigger.start()
    }

    override fun close() {
        craftListener.unregisterAll()
        flow.removeMerchant()
    }
}
