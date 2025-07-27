package com.github.tanokun.bakajinrou.plugin.method.protective

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.protect.method.item.ShieldItem
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

class ShieldProtectiveItem: ShieldItem(), AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    override fun isActive(of: Participant): Boolean {
        val player = Bukkit.getPlayer(of.uniqueId) ?: return false

        player.activeItem.let {
            if (it.type != Material.SHIELD) return false

            return of.getGrantedMethodByItemStack(it) === this
        }
    }

    override fun onConsume(consumer: Participant) {
        consumer.removeMethod(this)

        Bukkit.getPlayer(consumer.uniqueId)?.apply {
            world.playSound(Sound.sound(Key.key("item.shield.block"), Sound.Source.PLAYER, 1.0f, 1.0f))
            world.spawnParticle(Particle.BLOCK, location, 10, 0.3, 0.3, 0.3, Material.OAK_WOOD.createBlockData())
        }
    }

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.SHIELD)

        item.editMeta { meta ->
            meta.persistentDataContainer.set(itemKey, PersistentDataType.STRING, uniqueId.toString())
        }

        return item
    }
}