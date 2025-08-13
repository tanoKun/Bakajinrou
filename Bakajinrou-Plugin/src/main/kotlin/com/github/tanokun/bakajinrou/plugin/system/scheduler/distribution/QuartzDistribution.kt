package com.github.tanokun.bakajinrou.plugin.system.scheduler.distribution

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState
import com.github.tanokun.bakajinrou.game.scheduler.every
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration.Companion.minutes

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class QuartzDistribution(
    private val scheduler: GameScheduler,
    private val game: JinrouGame,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider
): Observer {
    init {
        mainScope.launch { collectRemainingTime() }
    }


    /**
     * 参加者にクオーツを配布します。
     *
     * @param participants ゲームの全ての参加者
     */
    fun distributeQuartz(participants: ParticipantScope.NonSpectators) {
        participants
            .excludes(Participant::isDead)
            .forEach {
                mainScope.launch { distribute(it.participantId) }
            }
    }

    private suspend fun distribute(participantId: ParticipantId) {
        val player = playerProvider.waitPlayerOnline(participantId)

        player.inventory.addItem(ItemStack(Material.QUARTZ))
    }

    private suspend fun collectRemainingTime() {
        scheduler.observe(mainScope)
            .filterIsInstance<ScheduleState.Active>()
            .filter { it.passedTime > 1.minutes }
            .every(2.minutes)
            .collect { state ->
                distributeQuartz(game.getCurrentParticipants().excludeSpectators())
            }
    }
}