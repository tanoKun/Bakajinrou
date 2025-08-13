package com.github.tanokun.bakajinrou.plugin.logger

import com.github.tanokun.bakajinrou.game.logger.GameLogger
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import java.util.*

@Scoped(binds = [GameLogger::class])
@Scope(value = GameComponents::class)
class JinrouLogger: GameLogger {
    override fun logKillParticipantToSpectator(victim: UUID, by: UUID) {
    }
}