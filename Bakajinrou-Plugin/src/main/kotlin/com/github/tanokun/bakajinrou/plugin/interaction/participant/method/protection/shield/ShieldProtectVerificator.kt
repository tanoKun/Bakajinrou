package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.protection.shield

import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protection.ProtectVerificator
import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider

class ShieldProtectVerificator(
    private val methodId: MethodId,
    private val participantId: ParticipantId,
    private val playerProvider: BukkitPlayerProvider
): ProtectVerificator {
    override fun isValid(): Boolean {
        val player = playerProvider.getAllowNull(participantId) ?: return false

        return player.activeItem.getMethodId() == methodId
    }

    override fun copy(participantId: ParticipantId): ShieldProtectVerificator = ShieldProtectVerificator(methodId, participantId, playerProvider)
}