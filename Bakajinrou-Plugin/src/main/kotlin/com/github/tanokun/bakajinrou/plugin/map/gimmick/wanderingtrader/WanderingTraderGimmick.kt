package com.github.tanokun.bakajinrou.plugin.map.gimmick.wanderingtrader

import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickId
import com.github.tanokun.bakajinrou.plugin.map.gimmick.lifecycle.MapGimmick
import com.github.tanokun.bakajinrou.plugin.presentation.item.MethodItemPresenter
import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.random.Random

@Scoped(binds = [MapGimmick::class])
@Scope(value = GameComponents::class)
class WanderingTraderGimmick(
    plugin: Plugin,
    context: MapGimmickContext,
    methodItemPresenter: MethodItemPresenter,
    crafting: Crafting,
    mainScope: CoroutineScope,
    random: Random,
) : MapGimmick {
    override val id = MapGimmickId.WANDERING_TRADER

    private val flow = WanderingTraderFlow(context, methodItemPresenter, random)
    private val craftListener = CraftWanderingTraderProductByTradeListener(
        plugin,
        context,
        crafting,
        mainScope,
    )

    override fun start() {
        craftListener.registerAll()
        flow.spawnTrader()
    }

    override fun close() {
        craftListener.unregisterAll()
        flow.removeTrader()
    }
}
