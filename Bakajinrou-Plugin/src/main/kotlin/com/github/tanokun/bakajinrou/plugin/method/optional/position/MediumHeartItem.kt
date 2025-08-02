package com.github.tanokun.bakajinrou.plugin.method.optional.position

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.gui.AbilityGUI
import com.github.tanokun.bakajinrou.plugin.gui.ability.medium.MediumUsableAbility
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.optional.OptionalMethod
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
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

class MediumHeartItem(
    private val ability: MediumUsableAbility,
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
        val item = ItemStack.of(Material.HEART_OF_THE_SEA)

        item.editMeta { meta ->
            meta.displayName(component { text("霊媒の水晶") color Positions.Medium.color.toString() without italic with bold })

            setPersistent(meta.persistentDataContainer)
        }

        return item
    }
}