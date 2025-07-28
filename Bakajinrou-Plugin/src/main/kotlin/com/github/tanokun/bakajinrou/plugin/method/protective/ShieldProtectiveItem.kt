package com.github.tanokun.bakajinrou.plugin.method.protective

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.protect.method.item.ShieldItem
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.getGrantedMethodByItemStack
import com.github.tanokun.bakajinrou.plugin.method.itemKey
import org.bukkit.*
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ShieldProtectiveItem: ShieldItem(), AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    override val transportable: Boolean = true

    override fun isActive(of: Participant): Boolean {
        val player = Bukkit.getPlayer(of.uniqueId) ?: return false

        player.activeItem.let {
            if (it.type != Material.SHIELD) return false

            return of.getGrantedMethodByItemStack(it) === this
        }
    }

    override fun onConsume(consumer: Participant) {
        Bukkit.getPlayer(consumer.uniqueId)?.apply {
            world.playSound(location, Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0f, 1.0f)
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