package com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.ability.grant.observe

import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.plugin.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.interaction.method.adapter.ItemViewer
import com.github.tanokun.bakajinrou.plugin.interaction.method.strategy.GrantSyncInventoryObserver
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class AddSyncProtectAbilityObserver(
    grantedStrategiesPublisher: GrantedStrategiesPublisher,
    mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
    crafting: Crafting,
    logger: DebugLogger,
    private val translator: JinrouTranslator,
): GrantSyncInventoryObserver(grantedStrategiesPublisher, mainScope, playerProvider, crafting, logger, MethodAssetKeys.Ability.PROTECT) {
    override fun createItem(player: Player, add: MethodDifference.Granted): ItemStack =
        ItemViewer.createBasicItem(
            Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
            isGlowing = true,
            isVisible = false,
            method = add.grantedMethod,
            translator = translator,
            locale = player.locale()
        )
}