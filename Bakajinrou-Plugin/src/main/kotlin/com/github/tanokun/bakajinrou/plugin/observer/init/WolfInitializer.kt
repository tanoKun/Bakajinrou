package com.github.tanokun.bakajinrou.plugin.observer.init

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.isWolf
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.game.observer.initializer.ParticipantInitializer
import com.github.tanokun.bakajinrou.plugin.formatter.ParticipantsFormatter
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider

class WolfInitializer(
    private val jinrouGame: JinrouGame,
    private val translator: JinrouTranslator,
    gameController: JinrouGameController,
) : ParticipantInitializer(jinrouGame, gameController, ::isWolf) {
    override fun initialize(self: Participant, participants: ParticipantScope) {
        val player = BukkitPlayerProvider.get(self) ?: return

        val formatter = ParticipantsFormatter(jinrouGame.getAllParticipants().excludeSpectators(), player.locale(), translator)

        player.sendMessage(formatter.formatWolf())
    }
}