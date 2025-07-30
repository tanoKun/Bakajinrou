package com.github.tanokun.bakajinrou.plugin.method.weapon

import com.github.tanokun.bakajinrou.api.attack.method.other.ArrowMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.plugin.formatter.toTick
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class AttackByArrow(
    private val controller: JinrouGameController
): ArrowMethod(), AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    override val transportable: Boolean = false

    override val isVisible: Boolean = true

    private val revivalTime: Duration = 3.seconds

    override fun onConsume(consumer: Participant) {
        controller.scope.launch {
            Bukkit.getPlayer(consumer.uniqueId)?.apply {
                val resistanceEffect = PotionEffect(PotionEffectType.SLOWNESS, revivalTime.toTick(), 1, true, false)
                addPotionEffect(resistanceEffect)
            }

            delay(revivalTime)

            consumer.grantMethod(AttackByArrow(controller))
        }
    }

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.ARROW)

        item.editMeta { meta ->
            setPersistent(meta.persistentDataContainer)
        }

        return item
    }
}