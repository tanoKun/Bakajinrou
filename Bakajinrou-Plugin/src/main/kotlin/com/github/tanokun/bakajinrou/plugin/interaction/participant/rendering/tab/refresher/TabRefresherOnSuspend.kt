package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.refresher

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.modifier.TabListModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class TabRefresherOnSuspend(
    private val game: JinrouGame,
    private val tabListModifier: TabListModifier,
    private val playerProvider: BukkitPlayerProvider,
    private val mainScope: CoroutineScope,
): Observer {
    init {
        mainScope.launch {
            game.observeParticipants(mainScope)
                .filter { it.before?.isSuspended() == true }
                .map { it.after }
                .collect(::onSuspended)
        }
    }

    private fun onSuspended(suspended: Participant) {
        val player = playerProvider.getAllowNull(suspended) ?: return

        tabListModifier.initializeDisplayName(suspended.participantId, player)
    }
}