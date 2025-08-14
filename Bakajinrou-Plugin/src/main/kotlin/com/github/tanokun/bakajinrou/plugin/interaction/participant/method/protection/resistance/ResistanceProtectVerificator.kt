package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.protection.resistance

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protection.ProtectVerificator

class ResistanceProtectVerificator(private val isValid: Boolean): ProtectVerificator {
    override fun isValid(): Boolean = isValid

    override fun copy(participantId: ParticipantId): ResistanceProtectVerificator =
        ResistanceProtectVerificator(isValid)
}