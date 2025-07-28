package com.github.tanokun.bakajinrou.game.logger

import java.util.*

interface GameLogger {
    /**
     * 観戦者に、キルログを出力します。
     */
    fun logKillParticipantToSpectator(victim: UUID, by: UUID)

    /**
     * 内部的例外をコンソールに出力します
     */
    fun logException(throwable: Throwable)
}