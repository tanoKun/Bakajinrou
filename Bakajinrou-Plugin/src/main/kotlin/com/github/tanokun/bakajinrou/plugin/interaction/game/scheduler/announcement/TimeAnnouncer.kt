package com.github.tanokun.bakajinrou.plugin.interaction.game.scheduler.announcement

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.every
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class TimeAnnouncer(
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
     * 残り時間をアクションバーに表示します。
     *
     * 表示フォーマットは `"残り時間: ${minutes}分 ${seconds}秒"` です。
     *
     * @param participantId 表示される参加者のUniqueId
     * @param remainTime 残り時間
     *
     * @throws IllegalArgumentException 残り時間が0未満の場合
     */
    fun showRemainingTimeActionBar(participantId: ParticipantId, remainTime: Duration) {
        if (remainTime < Duration.ZERO) throw IllegalArgumentException("残り時間は0秒以上である必要があります。")


        val bukkitPlayer = playerProvider.getAllowNull(participantId) ?: return

        remainTime.toComponents { _, minutes, seconds, _ ->
            val minutes = Component.text(minutes)
            val seconds = Component.text(seconds)

            bukkitPlayer.sendActionBar(
                translator.translate(GameKeys.Announcement.REMAINING_TIME, bukkitPlayer.locale(), minutes, seconds)
            )
        }
    }

    private fun showRemainingTimeToAll(passedTime: Duration) {
        game.getCurrentParticipants()
            .map { it.participantId }
            .forEach { showRemainingTimeActionBar(it, passedTime) }
    }

    private suspend fun collectRemainingTime() {
        scheduler.observe(mainScope)
            .every(1.seconds)
            .collect { state ->
                showRemainingTimeToAll(state.startTime - state.passedTime)
            }
    }
}