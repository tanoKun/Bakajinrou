package com.github.tanokun.bakajinrou.plugin.logger

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.bukkit.logger.GameActionLogger

class JinrouActionLogger: GameActionLogger {
    override fun logKillParticipantToSpectator(
        victim: Participant,
        by: Participant,
    ) {
    }
}