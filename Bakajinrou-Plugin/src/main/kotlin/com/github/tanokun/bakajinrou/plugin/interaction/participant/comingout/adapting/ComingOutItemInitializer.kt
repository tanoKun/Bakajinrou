package com.github.tanokun.bakajinrou.plugin.interaction.participant.comingout.adapting

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class ComingOutItemInitializer(
    private val playerProvider: BukkitPlayerProvider,
    private val game: JinrouGame,
    private val gameScheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    private val comingOutMethod: ComingOutMethod
): Observer {
    init {
        mainScope.launch {
            gameScheduler
                .observe(mainScope)
                .whenLaunched()
                .take(1)
                .collect { atStarted() }
        }
    }

    private fun atStarted() = game.getCurrentParticipants().forEach { participant ->
        mainScope.launch {
            playerProvider.waitPlayerOnline(participant.participantId) {
                it.inventory.setItem(9, comingOutMethod.getAdapter(it.locale()))
            }
        }
    }
}