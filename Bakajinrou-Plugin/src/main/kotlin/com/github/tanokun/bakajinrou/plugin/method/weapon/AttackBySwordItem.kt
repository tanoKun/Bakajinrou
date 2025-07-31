package com.github.tanokun.bakajinrou.plugin.method.weapon

import com.github.tanokun.bakajinrou.api.attack.method.item.SwordItem
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.italic
import plutoproject.adventurekt.text.text
import plutoproject.adventurekt.text.with
import plutoproject.adventurekt.text.without
import java.util.*

class AttackBySwordItem: SwordItem(), AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    override val transportable: Boolean = true

    override val isVisible: Boolean = true

    override fun onConsume(consumer: Participant) {}

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.DIAMOND_SWORD)

        item.editMeta { meta ->
            meta.lore(listOf(
                component { text("参加者を近接戦闘で殺害できる。") color gray without italic with bold },
                component { text("ポーション、トーテムに対しては無効") color gray without italic with bold }
            ))

            setPersistent(meta.persistentDataContainer)
        }

        return item
    }
}