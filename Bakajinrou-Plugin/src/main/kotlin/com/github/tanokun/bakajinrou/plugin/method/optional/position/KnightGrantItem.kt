package com.github.tanokun.bakajinrou.plugin.method.optional.position

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.formatter.Positions
import com.github.tanokun.bakajinrou.plugin.gui.AbilityGUI
import com.github.tanokun.bakajinrou.plugin.gui.ability.knight.KnightUsableAbility
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.optional.OptionalMethod
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.italic
import plutoproject.adventurekt.text.text
import plutoproject.adventurekt.text.with
import plutoproject.adventurekt.text.without
import java.util.*

class KnightGrantItem(
    private val ability: KnightUsableAbility,
    private val participants: ParticipantScope.NonSpectators
): OptionalMethod.ClickMethod, AsBukkitItem {
    override val uniqueId: UUID = UUID.randomUUID()

    override val transportable: Boolean = false

    override val isVisible: Boolean = false

    override fun onConsume(consumer: Participant) {
        val player = Bukkit.getPlayer(consumer.uniqueId) ?: return

        AbilityGUI(ability, consumer, participants, this).open(player)
    }

    override fun createBukkitItem(): ItemStack {
        val item = ItemStack.of(Material.DIAMOND_HORSE_ARMOR)

        item.editMeta { meta ->
            meta.displayName(component { text("騎士の加護") color Positions.Knight.color.toString() without italic with bold })

            setPersistent(meta.persistentDataContainer)
        }

        return item
    }
}