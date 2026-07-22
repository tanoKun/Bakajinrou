package com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.refresher

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.state.distinctUntilChangedByParticipantOf
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.DummyPlayers
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GameTabRefresherOnComingOut(
    game: GameStore,
    tabHandler: TabHandler,
    dummyPlayers: DummyPlayers,
    playerProvider: BukkitPlayerProvider,
    jinrouTranslator: JinrouTranslator,
    mainScope: CoroutineScope,
): GameTabRefresher(game, tabHandler, dummyPlayers, playerProvider, jinrouTranslator) {
    init {
        mainScope.launch {
            game.participantChanges
                .distinctUntilChangedByParticipantOf(Participant::comingOut)
                .map { it.after }
                .collect(::rerender)
        }
    }
}
