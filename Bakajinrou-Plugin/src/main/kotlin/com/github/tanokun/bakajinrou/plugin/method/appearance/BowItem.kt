package com.github.tanokun.bakajinrou.plugin.method.appearance

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.italic
import plutoproject.adventurekt.text.style.white
import plutoproject.adventurekt.text.text
import plutoproject.adventurekt.text.with
import plutoproject.adventurekt.text.without
import java.util.*

class BowItem: AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    override val transportable: Boolean = false

    override val isVisible: Boolean = true

    override fun onConsume(consumer: Participant) {}

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.BOW)

        item.editMeta { meta ->
            meta.displayName(component { text("強い弓") color white without italic with bold })

            setPersistent(meta.persistentDataContainer)
        }

        return item
    }
}