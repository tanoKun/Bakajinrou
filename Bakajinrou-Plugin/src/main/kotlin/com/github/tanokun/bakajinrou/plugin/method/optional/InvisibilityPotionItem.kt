package com.github.tanokun.bakajinrou.plugin.method.optional

import com.github.tanokun.bakajinrou.api.method.optional.OptionalMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.itemKey
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.italic
import plutoproject.adventurekt.text.text
import plutoproject.adventurekt.text.with
import plutoproject.adventurekt.text.without
import java.util.*

/**
 * 透明化を付与するポーション。
 * 飲むことをトリガーに「透明化」を付与します。
 * 連続で飲んでも、効果時間は加算ではなく置き換えでの変更です。
 */
class InvisibilityPotionItem: OptionalMethod.DrinkingMethod, AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    private val effectTime = 600

    override fun onConsume(consumer: Participant) {
        consumer.removeMethod(this)
    }

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.POTION)

        item.editMeta { meta ->
            meta as PotionMeta

            meta.displayName(component { text("透明化ポーション") color "#7F8392" without italic with bold })

            meta.color = Color.fromRGB(0x7F8392)
            meta.addCustomEffect(
                PotionEffect(PotionEffectType.INVISIBILITY, effectTime, 1, false, true),
                true
            )
            meta.persistentDataContainer.set(itemKey, PersistentDataType.STRING, uniqueId.toString())
        }

        return item
    }
}