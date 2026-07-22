package com.github.tanokun.bakajinrou.plugin.map.gimmick.altar

import com.github.tanokun.bakajinrou.api.participant.position.isMadman
import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.block.Dispenser
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class AltarFlow(
    private val context: MapGimmickContext,
    private val random: Random,
) {
    suspend fun activateIfOffered(dispenser: Dispenser) {
        val quartz = dispenser.inventory.contents
            .filterNotNull()
            .filter { it.type == Material.QUARTZ }
            .sumOf(ItemStack::getAmount)
        if (quartz < QUARTZ_COST) return

        val candidates = context.game.getCurrentParticipants()
            .filter { it.isAlive() && !isWolf(it) && !isMadman(it) }
        if (candidates.isEmpty()) return

        dispenser.inventory.removeItem(ItemStack(Material.QUARTZ, QUARTZ_COST))
        val victim = candidates.random(random)
        context.game.updateParticipant(victim.participantId) { it.dead() }
        context.broadcast(Component.text("何者かが祭壇に祈りを捧げた……。", NamedTextColor.DARK_RED))
    }

    private companion object {
        const val QUARTZ_COST = 9
    }
}
