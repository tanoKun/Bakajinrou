package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.refresher

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.modifier.TabListModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class TabRefresherOnComingOut(
    private val game: JinrouGame,
    private val mainScope: CoroutineScope,
    private val tabListModifier: TabListModifier,
): Observer {
    init {
        mainScope.launch {
            game.observeParticipants(mainScope)
                .distinctUntilChangedBy { it.after.comingOut }
                .map { it.after }
                .collect(::onComingOut)
        }
    }

    private fun onComingOut(participant: Participant) {
        tabListModifier.updateDisplayNameToAll(participant.participantId)
    }
}