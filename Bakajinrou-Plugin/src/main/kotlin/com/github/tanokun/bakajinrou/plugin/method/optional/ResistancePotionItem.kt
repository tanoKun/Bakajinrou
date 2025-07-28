package com.github.tanokun.bakajinrou.plugin.method.optional

import com.github.tanokun.bakajinrou.api.method.optional.OptionalMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.itemKey
import com.github.tanokun.bakajinrou.plugin.method.protective.ResistanceProtectiveEffect
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
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
 * 耐性効果を付与するポーション。
 * 飲むことをトリガーに「耐性」を付与するが、
 * 重複する場合、古い「耐性」を参加者から剝奪します。
 */
class ResistancePotionItem(private val plugin: Plugin): OptionalMethod.DrinkingMethod, AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    private val effectTime = 400

    override fun onConsume(consumer: Participant) {
        consumer.removeMethod(this)

        consumer.getActiveProtectiveMethods()
            .filter { it is ResistanceProtectiveEffect }
            .forEach { consumer.removeMethod(it) }

        val protective = ResistanceProtectiveEffect()
        consumer.grantMethod(protective)

        Bukkit.getPlayer(consumer.uniqueId)?.apply {
            val resistanceEffect = PotionEffect(PotionEffectType.RESISTANCE, effectTime, 1, false, true)

            addPotionEffect(resistanceEffect)
        }

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (consumer.getGrantedMethod(protective.uniqueId) != null) consumer.removeMethod(protective)
        }, effectTime.toLong())
    }

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.POTION)

        item.editMeta { meta ->
            meta as PotionMeta

            meta.displayName(component { text("耐性ポーション") color "#339900" without italic with bold })

            meta.color = Color.fromRGB(0x339900)
            meta.addCustomEffect(
                PotionEffect(PotionEffectType.RESISTANCE, effectTime, 1, false, true),
                true
            )
            meta.persistentDataContainer.set(itemKey, PersistentDataType.STRING, uniqueId.toString())
        }

        return item
    }
}