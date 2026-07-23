package com.github.tanokun.bakajinrou.plugin.map.gimmick.botanical

import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickId
import com.github.tanokun.bakajinrou.plugin.map.gimmick.lifecycle.MapGimmick
import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.random.Random

@Scoped(binds = [MapGimmick::class])
@Scope(value = GameComponents::class)
class BotanicalGardenGimmick(
    plugin: Plugin,
    context: MapGimmickContext,
    translator: JinrouTranslator,
    crafting: Crafting,
    mainScope: CoroutineScope,
    random: Random,
) : MapGimmick {
    override val id = MapGimmickId.BOTANICAL_GARDEN

    private val flow = BotanicalMerchantFlow(context, translator, random)
    private val craftListener = CraftBotanicalProductByTradeListener(
        plugin,
        context,
        crafting,
        mainScope,
    )

    override fun start() {
        craftListener.registerAll()
        flow.spawnMerchant()
    }

    override fun close() {
        craftListener.unregisterAll()
        flow.removeMerchant()
    }
}
