package com.github.tanokun.bakajinrou.plugin.participant.method.attack.synchronization

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemViewer.createBasicItem
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.participant.method.GrantedInventorySynchronizer
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta
import org.bukkit.inventory.meta.Damageable
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GrantedScatterCrossbowMethodSynchronizer(
    gameChanges: GameChanges,
    mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
    crafting: Crafting,
    private val translator: JinrouTranslator,
) : GrantedInventorySynchronizer(
    gameChanges,
    mainScope,
    playerProvider,
    crafting,
    MethodAssetKeys.Attack.SCATTER_CROSSBOW,
) {
    override fun createItem(player: Player, add: MethodDifference.Granted): ItemStack =
        createBasicItem(
            material = Material.CROSSBOW,
            isGlowing = false,
            isVisible = true,
            method = add.grantedMethod,
            translator = translator,
            locale = player.locale(),
        ).apply {
            editMeta { meta ->
                (meta as CrossbowMeta).apply {
                    addEnchant(Enchantment.MULTISHOT, 1, true)
                    setChargedProjectiles(
                        List(SCATTER_PROJECTILE_COUNT) { ItemStack(Material.ARROW) }
                    )
                    setEnchantmentGlintOverride(true)
                }

                // Paper は拡散した矢ごとに耐久を1消費するため、3本目で壊れる値にする。
                (meta as Damageable).damage =
                    type.maxDurability.toInt() - SCATTER_PROJECTILE_COUNT
            }
        }

    private companion object {
        const val SCATTER_PROJECTILE_COUNT = 3
    }
}
