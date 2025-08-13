package com.github.tanokun.bakajinrou.plugin.system.scheduler.announcement

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.or
import com.github.tanokun.bakajinrou.api.participant.position.isFox
import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState
import com.github.tanokun.bakajinrou.game.scheduler.every
import com.github.tanokun.bakajinrou.game.scheduler.moment
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.formatter.toTick
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GlowingAnnouncer(
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
     * 以下の役職を除き、5秒間の発光を付与します。
     * - 人狼
     * - 妖狐
     *
     * @param isInclude 人狼、妖狐の発光を含めます。
     * @param participants ゲームの全ての参加者
     */
    fun glowCitizens(participants: ParticipantScope.NonSpectators, isInclude: Boolean) {
        val filter = if (isInclude) { { false } } else ::isWolf or ::isFox

        participants
            .excludes(filter)
            .forEach {
                val bukkitPlayer = playerProvider.getAllowNull(it) ?: return@forEach

                val glowingEffect = PotionEffect(PotionEffectType.GLOWING, 5.seconds.toTick(), 1, false, false)
                bukkitPlayer.addPotionEffect(glowingEffect)
            }
    }

    /**
     * 5分からの定期発行の予告をします。
     *
     * @param participants ゲームの全ての参加者
     */
    fun announceGlowing(participants: ParticipantScope.All) {
        participants.forEach {
            val player = playerProvider.getAllowNull(it) ?: return@forEach

            player.sendMessage(translator.translate(GameKeys.Announcement.GLOWING, player.locale()))
        }
    }

    private suspend fun collectRemainingTime() {
        scheduler.observe(mainScope)
            .moment(7.minutes)
            .collect { state ->
                announceGlowing(game.getCurrentParticipants())
            }

        scheduler.observe(mainScope)
            .filterIsInstance<ScheduleState.Active>()
            .filter { it.remainingTime in 0.seconds..5.minutes }
            .every(40.seconds)
            .collect { state ->
                glowCitizens(game.getCurrentParticipants().excludeSpectators(), isInclude = state.remainingTime > 3.minutes)
            }
    }
}
