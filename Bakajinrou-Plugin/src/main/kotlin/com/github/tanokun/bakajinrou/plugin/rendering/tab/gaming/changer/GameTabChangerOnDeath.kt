package com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.changer

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.distinctUntilChangedByParticipantOf
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandlerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GameTabChangerOnDeath(
    game: JinrouGame,
    private val tabHandler: TabHandler,
    private val playerProvider: BukkitPlayerProvider,
    private val mainScope: CoroutineScope,
): Observer {
    init {
        mainScope.launch {
            game.observeParticipants(mainScope)
                .filter { it.after.isDead() }
                .distinctUntilChangedByParticipantOf(Participant::isDead)
                .map { it.after }
                .collect(::changer)
        }
    }

    private fun changer(target: Participant) {
        val player = playerProvider.getAllowNull(target) ?: return

        tabHandler.joinEngine(TabHandlerType.SharedBySpectators, player)
    }
}
