package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.advantage.synchronization

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting

import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemViewer.createBasicItem
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.formatter.toTick
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.method.GrantedInventorySynchronizer
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration.Companion.seconds

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GrantedInvisibilityMethodSynchronizer(
    grantedStrategiesPublisher: GrantedStrategiesPublisher,
    mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
    crafting: Crafting,
    private val translator: JinrouTranslator,
): GrantedInventorySynchronizer(grantedStrategiesPublisher, mainScope, playerProvider, crafting, MethodAssetKeys.Advantage.INVISIBILITY) {
    private val effectTime = 30.seconds

    override fun createItem(player: Player, add: MethodDifference.Granted): ItemStack {
        val item = createBasicItem(Material.POTION,
            isGlowing = false,
            isVisible = true,
            method = add.grantedMethod,
            translator = translator,
            locale = player.locale()
        )

        item.editMeta {
            it as PotionMeta
            it.color = Color.fromRGB(0x7F8392)
            it.addCustomEffect(
                PotionEffect(PotionEffectType.INVISIBILITY, effectTime.toTick(), 1, false, true),
                true
            )
        }

        return item
    }
}