package com.github.tanokun.bakajinrou.plugin.participant.method.attack.synchronization
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting

import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemViewer.createBasicItem
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.participant.method.GrantedInventorySynchronizer
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionType
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GrantedGasMethodSynchronizer(
    gameChanges: GameChanges,
    mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
    crafting: Crafting,
    private val translator: JinrouTranslator,
): GrantedInventorySynchronizer(gameChanges, mainScope, playerProvider, crafting, MethodAssetKeys.Attack.GAS) {

    override fun createItem(player: Player, add: MethodDifference.Granted): ItemStack {
        val item = createBasicItem(Material.SPLASH_POTION,
            isGlowing = false,
            isVisible = true,
            method = add.grantedMethod,
            translator = translator,
            locale = player.locale()
        )

        item.editMeta {
            it as PotionMeta
            it.basePotionType = PotionType.HARMING
        }

        return item
    }
}
