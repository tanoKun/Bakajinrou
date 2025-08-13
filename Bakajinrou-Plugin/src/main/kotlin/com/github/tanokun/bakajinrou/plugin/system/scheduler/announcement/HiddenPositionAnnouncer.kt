package com.github.tanokun.bakajinrou.plugin.system.scheduler.announcement

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.moment
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.formatter.ParticipantsFormatter
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
class HiddenPositionAnnouncer(
    private val scheduler: GameScheduler,
    private val game: JinrouGame,
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
                reveal(game.getCurrentParticipants())
            }

        scheduler.observe(mainScope)
            .moment(5.minutes)
            .collect { state ->
                announceRevelation(game.getCurrentParticipants())
            }
    }
}