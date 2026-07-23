package com.github.tanokun.bakajinrou.plugin.presentation.item

import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.plugin.presentation.item.ItemViewer.createBasicItem
import com.github.tanokun.bakajinrou.plugin.common.formatter.toTick
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import org.koin.core.annotation.Single
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@Single
class MethodItemPresenter(
    private val translator: JinrouTranslator,
) {
    fun gas(locale: Locale): ItemStack =
        basic(Material.SPLASH_POTION, MethodAssetKeys.Attack.GAS, locale).apply {
            editMeta(PotionMeta::class.java) { it.basePotionType = PotionType.HARMING }
        }

    fun sword(locale: Locale): ItemStack =
        basic(Material.DIAMOND_SWORD, MethodAssetKeys.Attack.SWORD, locale, isGlowing = true)

    fun arrow(locale: Locale): ItemStack =
        basic(Material.ARROW, MethodAssetKeys.Attack.ARROW, locale, isGlowing = true)

    fun scatterCrossbow(locale: Locale): ItemStack =
        basic(Material.CROSSBOW, MethodAssetKeys.Attack.SCATTER_CROSSBOW, locale).apply {
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

    fun totem(locale: Locale): ItemStack =
        basic(Material.TOTEM_OF_UNDYING, MethodAssetKeys.Protective.TOTEM, locale, isGlowing = true)

    fun fakeTotem(locale: Locale): ItemStack =
        basic(Material.TOTEM_OF_UNDYING, MethodAssetKeys.Protective.FAKE_TOTEM, locale, isGlowing = true)

    fun shield(locale: Locale): ItemStack =
        basic(Material.SHIELD, MethodAssetKeys.Protective.SHIELD, locale, isGlowing = true)

    fun resistance(locale: Locale): ItemStack =
        basic(Material.POTION, MethodAssetKeys.Protective.RESISTANCE, locale).apply {
            editMeta(PotionMeta::class.java) {
                it.color = Color.fromRGB(0x339900)
                it.addCustomEffect(
                    PotionEffect(PotionEffectType.RESISTANCE, RESISTANCE_DURATION.toTick(), 1, false, true),
                    true,
                )
            }
        }

    fun exchange(locale: Locale): ItemStack =
        basic(Material.ENDER_PEARL, MethodAssetKeys.Advantage.EXCHANGE, locale)

    fun speed(locale: Locale): ItemStack =
        basic(Material.SPLASH_POTION, MethodAssetKeys.Advantage.SPEED, locale).apply {
            editMeta(PotionMeta::class.java) {
                it.color = Color.fromRGB(0xCDFFF3)
                it.addCustomEffect(
                    PotionEffect(PotionEffectType.SPEED, ADVANTAGE_DURATION.toTick(), 2, false, true),
                    true,
                )
            }
        }

    fun invisibility(locale: Locale): ItemStack =
        basic(Material.POTION, MethodAssetKeys.Advantage.INVISIBILITY, locale).apply {
            editMeta(PotionMeta::class.java) {
                it.color = Color.fromRGB(0x7F8392)
                it.addCustomEffect(
                    PotionEffect(PotionEffectType.INVISIBILITY, ADVANTAGE_DURATION.toTick(), 1, false, true),
                    true,
                )
            }
        }

    fun divine(locale: Locale): ItemStack =
        basic(Material.ENCHANTED_BOOK, MethodAssetKeys.Ability.DIVINE, locale, isGlowing = true)

    fun commune(locale: Locale): ItemStack =
        basic(Material.HEART_OF_THE_SEA, MethodAssetKeys.Ability.COMMUNE, locale, isGlowing = true)

    fun protect(locale: Locale): ItemStack =
        basic(
            Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
            MethodAssetKeys.Ability.PROTECT,
            locale,
            isGlowing = true,
        )

    private fun basic(
        material: Material,
        assetKey: MethodAssetKeys,
        locale: Locale,
        isGlowing: Boolean = false,
    ): ItemStack = createBasicItem(
        material = material,
        isGlowing = isGlowing,
        assetKey = assetKey,
        translator = translator,
        locale = locale,
    )

    private companion object {
        val RESISTANCE_DURATION = 20.seconds
        val ADVANTAGE_DURATION = 30.seconds
        const val SCATTER_PROJECTILE_COUNT = 3
    }
}
