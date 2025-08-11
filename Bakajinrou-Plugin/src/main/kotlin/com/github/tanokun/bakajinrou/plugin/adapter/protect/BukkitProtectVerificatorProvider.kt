package com.github.tanokun.bakajinrou.plugin.adapter.protect

import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protect.ProtectVerificator
import com.github.tanokun.bakajinrou.game.protect.ProtectVerificatorProvider
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider

class BukkitProtectVerificatorProvider(private val playerProvider: BukkitPlayerProvider): ProtectVerificatorProvider {
    override fun getTotemVerificator(participantId: ParticipantId, methodId: MethodId): ProtectVerificator =
        TotemProtectVerificator(methodId, participantId, playerProvider)

    override fun getShieldVerificator(participantId: ParticipantId, methodId: MethodId): ProtectVerificator =
        ShieldProtectVerificator(methodId, participantId, playerProvider)


    override fun getResistanceVerificator(isValid: Boolean): ProtectVerificator =
        ResistanceProtectVerificator(isValid)
}