package com.github.tanokun.bakajinrou.plugin.method.weapon

import com.github.tanokun.bakajinrou.api.attack.method.item.SwordItem
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.itemKey
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.italic
import plutoproject.adventurekt.text.text
import plutoproject.adventurekt.text.with
import plutoproject.adventurekt.text.without
import java.util.*

class AttackBySwordItem(
    private val controller: JinrouGameController
): SwordItem, AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    override fun onSuccessAttack(by: Participant, victim: Participant) = controller.killed(victim = victim, by = by)

    override fun onConsume(consumer: Participant) {
        consumer.removeMethod(this)
    }

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.DIAMOND_SWORD)

        item.editMeta { meta ->
            meta.lore(listOf(
                component { text("プレイヤーを近接戦闘で殺害できる。") color gray without italic with bold },
                component { text("ポーション、トーテムに対しては無効") color gray without italic with bold }
            ))

            meta.persistentDataContainer.set(itemKey, PersistentDataType.STRING, uniqueId.toString())
        }

        return item
    }
}