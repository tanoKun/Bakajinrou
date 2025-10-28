package com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.refresher

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.distinctUntilChangedByParticipantOf
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.DummyPlayers
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GameTabRefresherOnComingOut(
    game: JinrouGame,
    tabHandler: TabHandler,
    dummyPlayers: DummyPlayers,
    playerProvider: BukkitPlayerProvider,
    jinrouTranslator: JinrouTranslator,
    mainScope: CoroutineScope,
): GameTabRefresher(game, tabHandler, dummyPlayers, playerProvider, jinrouTranslator) {
    init {
        mainScope.launch {
            game.observeParticipants(mainScope)
                .distinctUntilChangedByParticipantOf(Participant::comingOut)
                .map { it.after }
                .collect(::rerender)
        }
    }
}
