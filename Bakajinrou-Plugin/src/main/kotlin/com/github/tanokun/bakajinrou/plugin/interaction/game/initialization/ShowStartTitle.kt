package com.github.tanokun.bakajinrou.plugin.interaction.game.initialization

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import net.kyori.adventure.title.Title
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class ShowStartTitle(
    private val playerProvider: BukkitPlayerProvider,
    private val game: JinrouGame,
    private val gameScheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    private val translator: JinrouTranslator,
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
            val player = playerProvider.waitPlayerOnline(participant.participantId)
            val startTitle = Title.title(
                translator.translate(GameKeys.Start.TITLE, player.locale()),
                translator.translate(GameKeys.Start.SUB_TITLE, player.locale())
            )

            player.showTitle(startTitle)
        }
    }
}