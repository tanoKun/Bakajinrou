package com.github.tanokun.bakajinrou.game.logger

import java.util.*

interface GameActionLogger {
    /**
     * 観戦者に、キルログを出力します。
     */
    fun logKillParticipantToSpectator(victim: UUID, by: UUID)
}