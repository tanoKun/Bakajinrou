package com.github.tanokun.bakajinrou.plugin.method.protective

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.protect.method.item.FakeTotemItem
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.getGrantedMethodByItemStack
import com.github.tanokun.bakajinrou.plugin.method.itemKey
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class FakeTotemProtectiveItem: FakeTotemItem(), AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    override fun isActive(of: Participant): Boolean {
        val player = Bukkit.getPlayer(of.uniqueId) ?: return false

        if (of.getGrantedMethodByItemStack(player.inventory.itemInMainHand) === this) return true
        if (of.getGrantedMethodByItemStack(player.inventory.itemInOffHand) === this) return true

        return false
    }

    override fun onConsume(consumer: Participant) {
        consumer.removeMethod(this)

        Bukkit.getPlayer(consumer.uniqueId)?.apply {
            world.playSound(Sound.sound(Key.key("item.totem.use"), Sound.Source.PLAYER, 1.0f, 1.0f))
            world.spawnParticle(Particle.TOTEM_OF_UNDYING, location, 30, 0.5, 0.5, 0.5, 0.1)
        }
    }

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.TOTEM_OF_UNDYING)

        item.editMeta { meta ->
            meta.persistentDataContainer.set(itemKey, PersistentDataType.STRING, uniqueId.toString())
        }

        return item
    }
}