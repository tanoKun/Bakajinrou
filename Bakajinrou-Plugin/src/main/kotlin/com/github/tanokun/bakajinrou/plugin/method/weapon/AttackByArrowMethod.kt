package com.github.tanokun.bakajinrou.plugin.method.weapon

import com.github.tanokun.bakajinrou.api.attack.method.other.ArrowMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.itemKey
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class AttackByArrowMethod(
    private val controller: JinrouGameController
): ArrowMethod, AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    override fun onSuccessAttack(by: Participant, victim: Participant) = controller.killed(victim = victim, by = by)

    override fun onConsume(consumer: Participant) {
        consumer.removeMethod(this)
    }

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.ARROW)

        item.editMeta { meta ->
            meta.persistentDataContainer.set(itemKey, PersistentDataType.STRING, uniqueId.toString())
        }

        return item
    }
}