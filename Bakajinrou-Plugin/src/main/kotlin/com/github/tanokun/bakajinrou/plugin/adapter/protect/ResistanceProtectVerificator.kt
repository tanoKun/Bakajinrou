package com.github.tanokun.bakajinrou.plugin.adapter.protect

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protect.ProtectVerificator

class ResistanceProtectVerificator(private val isValid: Boolean): ProtectVerificator {
    override fun isValid(): Boolean = isValid

    override fun copy(participantId: ParticipantId): ResistanceProtectVerificator =
        ResistanceProtectVerificator(isValid)
}