package com.github.tanokun.bakajinrou.plugin.interaction.game.scheduler.announcement

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.remaining
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration.Companion.minutes

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GetawayAnnouncer(
    private val scheduler: GameScheduler,
    private val game: JinrouGame,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator
): Observer {
    init {
        mainScope.launch { collectRemainingTime() }
    }

    /**
     * 逃げ切りの情報を予告します。
     *
     * @param participantId 表示される参加者のUniqueId
     */
    fun showGetaway(participantId: ParticipantId) {
        val bukkitPlayer = playerProvider.getAllowNull(participantId) ?: return

        bukkitPlayer.sendActionBar(
            translator.translate(GameKeys.Announcement.GETAWAY, bukkitPlayer.locale())
        )
    }

    private fun showGetawayToAllPlayers() {
        game.getCurrentParticipants()
            .map { it.participantId }
            .forEach { showGetaway(it) }
    }

    private suspend fun collectRemainingTime() {
        scheduler.observe(mainScope)
            .remaining(2.minutes)
            .collect { state -> showGetawayToAllPlayers() }
    }
}