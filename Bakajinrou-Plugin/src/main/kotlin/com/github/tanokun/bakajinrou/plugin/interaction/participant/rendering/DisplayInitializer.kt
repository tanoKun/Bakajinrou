package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.modifier.TabListModifier
import com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.team.modifier.ViewTeamModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class DisplayInitializer(
    private val game: JinrouGame,
    private val gameScheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    private val tabListModifier: TabListModifier,
    private val viewTeamModifier: ViewTeamModifier
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
        tabListModifier.initializeDisplayNameOfAll(participant.participantId)
        viewTeamModifier.applyModification(participant.participantId)
    }
}