package com.github.tanokun.bakajinrou.plugin.interaction.method.hidden

import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemViewer.isVisible
import net.minecraft.network.protocol.game.ClientboundAnimatePacket
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

typealias ItemList = List<Pair<EnumWrappers.ItemSlot, ItemStack>>

object HideItem {
    fun modifyEquipment(origin: ItemList, receiver: Participant): ItemList = origin.map { pair ->
        val item = pair.second

        if (item.isVisible()) return@map pair
        if (receiver.isDead()) return@map pair

        return@map Pair(pair.first, ItemStack(Material.AIR))
    }

    fun isAttackMotion(animation: Int): Boolean =
        (animation == ClientboundAnimatePacket.SWING_OFF_HAND || animation == ClientboundAnimatePacket.SWING_MAIN_HAND)

    fun isAttack(action: WrappedEnumEntityUseAction) =
        action == WrappedEnumEntityUseAction.attack()
}