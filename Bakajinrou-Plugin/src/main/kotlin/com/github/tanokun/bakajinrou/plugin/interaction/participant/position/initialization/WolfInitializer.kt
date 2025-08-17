package com.github.tanokun.bakajinrou.plugin.interaction.participant.position.initialization

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import com.github.tanokun.bakajinrou.game.participant.initialization.ParticipantInitializer
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.formatter.ParticipantsFormatter
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.FormatKeys
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class WolfInitializer(
    private val game: JinrouGame,
    private val translator: JinrouTranslator,
    gameSession: JinrouGameSession,
    private val playerProvider: BukkitPlayerProvider,
) : ParticipantInitializer(game, gameSession, ::isWolf) {
    override suspend fun initialize(selfId: ParticipantId) {
        val player = playerProvider.waitPlayerOnline(selfId)

        val formatter = ParticipantsFormatter(game.getCurrentParticipants().excludeSpectators(), translator)

        player.sendMessage(formatter.format(player.locale(), FormatKeys.Category.WOLF, ::isWolf to FormatKeys.Participant.WOLF))
    }
}