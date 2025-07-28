package com.github.tanokun.bakajinrou.plugin.method.weapon

import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.attack.method.effect.DamagePotionEffect
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.itemKey
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionType
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.italic
import plutoproject.adventurekt.text.text
import plutoproject.adventurekt.text.with
import plutoproject.adventurekt.text.without
import java.util.*

class AttackByDamagePotionEffect(
    private val controller: JinrouGameController
): DamagePotionEffect, AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    override val transportable: Boolean = true

    override fun onSuccessAttack(by: Participant, victim: Participant) = controller.killed(victim = victim, by = by)

    override fun onConsume(consumer: Participant) {}

    override fun attack(by: Participant, victim: Participant) {
        for (protectItem in victim.getActiveProtectiveMethods()) {
            protectItem.onConsume(consumer = victim)

            when (protectItem.verifyProtect(method = this)) {
                is AttackResult.Protected -> return
                AttackResult.SuccessAttack -> continue
            }
        }

        onSuccessAttack(by, victim)
    }

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.SPLASH_POTION)

        item.editMeta { meta ->
            meta as PotionMeta

            meta.lore(listOf(
                component { text("プレイヤーを毒殺できる。") color gray without italic with bold },
                component { text("ポーション、トーテムに対しては無効") color gray without italic with bold }
            ))

            meta.basePotionType = PotionType.HARMING
            meta.persistentDataContainer.set(itemKey, PersistentDataType.STRING, uniqueId.toString())
        }

        return item
    }
}