package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.protection.totem

import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protection.ProtectVerificator
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider

class TotemProtectVerificator(
    private val methodId: MethodId,
    private val participantId: ParticipantId,
    private val playerProvider: BukkitPlayerProvider
): ProtectVerificator {
    override fun isValid(): Boolean {
        val player = playerProvider.getAllowNull(participantId) ?: return false

        val mainHand = player.inventory.itemInMainHand
        val offHand = player.inventory.itemInOffHand

        if (mainHand.getMethodId() == methodId) return true
        if (offHand.getMethodId() == methodId) return true

        return false
    }

    override fun copy(participantId: ParticipantId): TotemProtectVerificator = TotemProtectVerificator(methodId, participantId, playerProvider)
}