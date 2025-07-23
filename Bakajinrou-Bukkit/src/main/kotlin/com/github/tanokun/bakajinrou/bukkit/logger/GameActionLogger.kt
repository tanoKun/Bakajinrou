package com.github.tanokun.bakajinrou.bukkit.logger

import com.github.tanokun.bakajinrou.api.participant.Participant
import java.util.*

interface GameActionLogger {
    /**
     * 観戦者に、キルログを出力します。
     */
    fun logKillParticipantToSpectator(victim: Participant, by: Participant)
}