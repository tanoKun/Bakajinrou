package com.github.tanokun.bakajinrou.plugin.participant.method.ability.protect

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting

import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemViewer
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.participant.method.GrantedInventorySynchronizer
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GrantedProtectAbilitySynchronizer(
    gameChanges: GameChanges,
    mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
    crafting: Crafting,
    private val translator: JinrouTranslator,
): GrantedInventorySynchronizer(gameChanges, mainScope, playerProvider, crafting, MethodAssetKeys.Ability.PROTECT) {
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
