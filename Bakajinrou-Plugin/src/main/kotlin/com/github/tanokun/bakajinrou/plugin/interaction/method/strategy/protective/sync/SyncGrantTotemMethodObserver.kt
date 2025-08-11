package com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.protective.sync

import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemViewer.createBasicItem
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.SyncGrantInventoryObserver
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SyncGrantTotemMethodObserver(
    grantedStrategiesPublisher: GrantedStrategiesPublisher,
    mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
    crafting: Crafting,
    logger: DebugLogger,
    private val translator: JinrouTranslator,
): SyncGrantInventoryObserver(grantedStrategiesPublisher, mainScope, playerProvider, crafting, logger, MethodAssetKeys.Protective.TOTEM) {
    override fun createItem(player: Player, add: MethodDifference.Granted): ItemStack =
        createBasicItem(Material.TOTEM_OF_UNDYING,
            isGlowing = true,
            isVisible = true,
            method = add.grantedMethod,
            translator = translator,
            locale = player.locale()
        )
}