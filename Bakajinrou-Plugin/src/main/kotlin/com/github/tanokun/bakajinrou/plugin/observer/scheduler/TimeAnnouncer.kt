package com.github.tanokun.bakajinrou.plugin.observer.scheduler

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.every
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.yellow
import plutoproject.adventurekt.text.text
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimeAnnouncer(
    private val scheduler: GameScheduler,
    private val jinrouGame: JinrouGame,
    private val mainScope: CoroutineScope
) {
    init {
        mainScope.launch {
            collectRemainingTime()
        }
    }

    /**
     * 残り時間をアクションバーに表示します。
     *
     * 表示フォーマットは `"残り時間: ${minutes}分 ${seconds}秒"` です。
     *
     * @param participants 表示される参加者のUniqueId
     * @param remainTime 残り時間
     *
     * @throws IllegalArgumentException 残り時間が0未満の場合
     */
    fun showRemainingTimeActionBar(participants: UUID, remainTime: Duration) {
        if (remainTime < Duration.ZERO) throw IllegalArgumentException("残り時間は0秒以上である必要があります。")

        val formattedTime = remainTime.toComponents { _, minutes, seconds, _ ->
            "残り時間: ${minutes}分 ${seconds}秒"
        }

        val bukkitPlayer = BukkitPlayerProvider.get(participants) ?: return

        bukkitPlayer.sendActionBar(
            component { text(formattedTime) color yellow deco bold }
        )
    }

    private fun showRemainingTimeToAll(passedTime: Duration) {
        jinrouGame.getAllParticipants()
            .map { it.uniqueId }
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