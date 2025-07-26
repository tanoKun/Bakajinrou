package com.github.tanokun.bakajinrou.plugin.logger

import com.github.tanokun.bakajinrou.game.logger.GameActionLogger
import java.util.*

class JinrouActionLogger: GameActionLogger {
    override fun logKillParticipantToSpectator(victim: UUID, by: UUID) {
    }
}