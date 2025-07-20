package com.github.tanokun.bakajinrou.bukkit.logger

import java.util.*

interface GameActionLogger {
    /**
     * 観戦者に、キルログを出力します。
     */
    fun logKillParticipantToSpectator(victim: UUID, by: UUID)
}