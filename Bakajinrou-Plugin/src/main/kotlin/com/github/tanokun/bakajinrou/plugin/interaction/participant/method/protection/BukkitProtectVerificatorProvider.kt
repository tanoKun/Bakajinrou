package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.protection

import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protection.ProtectVerificator
import com.github.tanokun.bakajinrou.game.protection.ProtectVerificatorProvider
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.method.protection.resistance.ResistanceProtectVerificator
import com.github.tanokun.bakajinrou.plugin.interaction.participant.method.protection.shield.ShieldProtectVerificator
import com.github.tanokun.bakajinrou.plugin.interaction.participant.method.protection.totem.TotemProtectVerificator
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [ProtectVerificatorProvider::class])
@Scope(value = GameComponents::class)
class BukkitProtectVerificatorProvider(private val playerProvider: BukkitPlayerProvider): ProtectVerificatorProvider {
    override fun getTotemVerificator(participantId: ParticipantId, methodId: MethodId): ProtectVerificator =
        TotemProtectVerificator(methodId, participantId, playerProvider)

    override fun getShieldVerificator(participantId: ParticipantId, methodId: MethodId): ProtectVerificator =
        ShieldProtectVerificator(methodId, participantId, playerProvider)


    override fun getResistanceVerificator(isValid: Boolean): ProtectVerificator =
        ResistanceProtectVerificator(isValid)
}