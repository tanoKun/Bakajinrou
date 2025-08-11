package com.github.tanokun.bakajinrou.plugin.system.scheduler.observe

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.moment
import com.github.tanokun.bakajinrou.plugin.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.formatter.ParticipantsFormatter
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class HiddenPositionAnnouncer(
    private val scheduler: GameScheduler,
    private val jinrouGame: JinrouGame,
    private val translator: JinrouTranslator,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider
): Observer {
    init {
        mainScope.launch { collectRemainingTime() }
    }

    /**
     * 以下の役職の付与者一覧を、全ての参加者に表示します。
     * - 人狼
     * - 妖狐
     *
     * @param participants ゲームの全ての参加者
     *
     * @see ParticipantsFormatter
     */
    fun reveal(participants: ParticipantScope.All) {
        val formatter = ParticipantsFormatter(participants.excludeSpectators(), translator)
        participants.forEach {
            val player = playerProvider.getAllowNull(it) ?: return@forEach

            player.sendMessage(formatter.formatWolf(player.locale()))
            player.sendMessage(formatter.formatFox(player.locale()))
        }
    }

    /**
     * 役職開示の予告をします。
     *
     * @param participants ゲームの全ての参加者
     */
    fun announceRevelation(participants: ParticipantScope.All) {
        participants.forEach {
            val player = playerProvider.getAllowNull(it) ?: return@forEach

            player.sendMessage(translator.translate(GameKeys.Announcement.REVELATION, player.locale()))
        }
    }

    private suspend fun collectRemainingTime() {
        scheduler.observe(mainScope)
            .moment(3.minutes)
            .collect { state ->
                reveal(jinrouGame.getCurrentParticipants())
            }

        scheduler.observe(mainScope)
            .moment(5.minutes)
            .collect { state ->
                announceRevelation(jinrouGame.getCurrentParticipants())
            }
    }
}