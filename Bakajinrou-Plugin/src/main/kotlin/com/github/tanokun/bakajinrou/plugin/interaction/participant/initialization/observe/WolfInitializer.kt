package com.github.tanokun.bakajinrou.plugin.interaction.participant.initialization.observe

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import com.github.tanokun.bakajinrou.game.participant.observer.initialization.ParticipantInitializer
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.formatter.ParticipantsFormatter
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator

class WolfInitializer(
    private val jinrouGame: JinrouGame,
    private val translator: JinrouTranslator,
    gameController: JinrouGameSession,
    private val playerProvider: BukkitPlayerProvider,
) : ParticipantInitializer(jinrouGame, gameController, ::isWolf) {
    override suspend fun initialize(selfId: ParticipantId, participants: ParticipantScope) {
        val player = playerProvider.waitPlayerOnline(selfId)

        val formatter = ParticipantsFormatter(jinrouGame.getCurrentParticipants().excludeSpectators(), translator)

        player.sendMessage(formatter.formatWolf(player.locale()))
    }
}