package com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.ability.sync

import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemViewer
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.SyncGrantInventoryObserver
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SyncGrantCommuneAbilityObserver(
    grantedStrategiesPublisher: GrantedStrategiesPublisher,
    mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
    crafting: Crafting,
    logger: DebugLogger,
    private val translator: JinrouTranslator,
): SyncGrantInventoryObserver(grantedStrategiesPublisher, mainScope, playerProvider, crafting, logger, MethodAssetKeys.Ability.COMMUNE) {
    override fun createItem(player: Player, add: MethodDifference.Granted): ItemStack =
        ItemViewer.createBasicItem(
            Material.HEART_OF_THE_SEA,
            isGlowing = true,
            isVisible = false,
            method = add.grantedMethod,
            translator = translator,
            locale = player.locale()
        )
}