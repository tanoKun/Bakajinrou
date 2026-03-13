package com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.button

import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.ParticipantCandidates
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.AbstractItemBuilder
import xyz.xenondevs.invui.item.builder.SkullBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

const val WHEN_BE_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQyMDY1ZjJkMjA2YzBiNWQwMmMzNzY5ZGVmYzg5ZWU5YzUwMGZjMjYwMGY4NzA1NmQxYWEwNDk4NmRjZWEyMSJ9fX0="

class PlayerButton(private val target: Player, private val participantCandidates: ParticipantCandidates): AbstractItem() {

    override fun getItemProvider(): ItemProvider {
        val builder: AbstractItemBuilder<SkullBuilder> = try {
            SkullBuilder(SkullBuilder.HeadTexture.of(target))
        } catch (_: Exception) {
            SkullBuilder(SkullBuilder.HeadTexture(WHEN_BE_HEAD_TEXTURE))
        }

        builder.setItemFlags(ItemFlag.entries)

        if (participantCandidates.isParticipant(target.uniqueId)) {
            builder.setDisplayName("§f§l${target.name} (参加者)")
            builder.addEnchantment(Enchantment.UNBREAKING, 1, true)
        } else {
            builder.setDisplayName("§b§l${target.name} (観戦者)")
        }

        return builder
    }

    override fun handleClick(clickType: ClickType, cilcker: Player, event: InventoryClickEvent) {
        cilcker.playSound(cilcker, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F)

        participantCandidates.toggleParticipant(target.uniqueId)

        notifyWindows()
    }
}