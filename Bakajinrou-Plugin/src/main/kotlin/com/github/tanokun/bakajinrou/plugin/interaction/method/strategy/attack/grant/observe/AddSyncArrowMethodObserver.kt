package com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.attack.grant.observe

import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.plugin.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.interaction.method.adapter.ItemViewer.createBasicItem
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.GrantSyncInventoryObserver
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class AddSyncArrowMethodObserver(
    grantedStrategiesPublisher: GrantedStrategiesPublisher,
    mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
    crafting: Crafting,
    logger: DebugLogger,
    private val translator: JinrouTranslator,
): GrantSyncInventoryObserver(grantedStrategiesPublisher, mainScope, playerProvider, crafting, logger, MethodAssetKeys.Attack.ARROW) {
    override fun createItem(player: Player, add: MethodDifference.Granted): ItemStack =
        createBasicItem(Material.ARROW,
            isGlowing = true,
            isVisible = true,
            method = add.grantedMethod,
            translator = translator,
            locale = player.locale()
        )
}