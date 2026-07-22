package com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.button

import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.ParticipantCandidates
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class SelectByLoginTimeButton(private val participantCandidates: ParticipantCandidates, private val updater: () -> Unit): AbstractItem() {

    override fun getItemProvider(): ItemProvider {
        return ItemBuilder(Material.CLOCK)
            .setDisplayName("§b§lログイン時間順で選択")
            .addLoreLines(
                "§7足りない人数分、ログインが早い順に参加者を選択します。",
                "§7最大で、十五人選択されます。"
            )
    }

    override fun handleClick(clickType: ClickType, cilcker: Player, event: InventoryClickEvent) {
        cilcker.playSound(cilcker, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F)

        val sorted = participantCandidates.getSpectators()
            .mapNotNull { Bukkit.getPlayer(it) }
            .sortedByDescending { it.lastLogin }

        for (i in sorted.indices) {
            if (i >= 15) break
            participantCandidates.toggleParticipant(sorted[i].uniqueId)
        }

        updater()
    }
}