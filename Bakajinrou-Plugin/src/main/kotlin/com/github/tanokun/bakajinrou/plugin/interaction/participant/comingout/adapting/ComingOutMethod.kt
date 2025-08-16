package com.github.tanokun.bakajinrou.plugin.interaction.participant.comingout.adapting

import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.koin.core.annotation.Scope
import java.util.*

val COMING_OUT_ADAPTER_KEY = NamespacedKey("jinrou", "coming.out")

@Scope(value = GameComponents::class)
class ComingOutMethod(private val translator: JinrouTranslator) {
    fun getAdapter(locale: Locale) =
        ItemStack.of(Material.ARMOR_STAND).apply {
            editMeta {
                val displayName = translator.translate(GameKeys.ComingOut.DISPLAY_NAME, locale)
                it.displayName(displayName)

                it.addItemFlags(*ItemFlag.entries.toTypedArray())
                it.addEnchant(Enchantment.UNBREAKING, 1, true)

                it.persistentDataContainer.set(COMING_OUT_ADAPTER_KEY, PersistentDataType.BOOLEAN, true)
            }
        }
}