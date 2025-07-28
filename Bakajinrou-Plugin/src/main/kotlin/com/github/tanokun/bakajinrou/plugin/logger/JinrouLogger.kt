package com.github.tanokun.bakajinrou.plugin.logger

import com.github.tanokun.bakajinrou.game.logger.GameLogger
import org.bukkit.plugin.Plugin
import java.util.*

class JinrouLogger(private val plugin: Plugin): GameLogger {
    override fun logKillParticipantToSpectator(victim: UUID, by: UUID) {
    }

    override fun logException(throwable: Throwable) {
        plugin.logger.severe(throwable.stackTraceToString())
    }
}