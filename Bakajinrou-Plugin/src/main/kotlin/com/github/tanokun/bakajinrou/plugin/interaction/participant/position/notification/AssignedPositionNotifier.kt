package com.github.tanokun.bakajinrou.plugin.interaction.participant.position.notification


import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.game.participant.initialization.ParticipantInitializer
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class AssignedPositionNotifier(
    private val game: JinrouGame,
    private val translator: JinrouTranslator,
    gameSession: JinrouGameSession,
    private val playerProvider: BukkitPlayerProvider,
) : ParticipantInitializer(game, gameSession, { true }) {
    override suspend fun initialize(selfId: ParticipantId) {
        val self = game.getParticipant(selfId) ?: return
        val player = playerProvider.waitPlayerOnline(selfId)

        val prefix = self.getPrefix(self) ?: return
        val positionName = translator.translate(prefix, player.locale())
        val message = translator.translate(GameKeys.Start.Notification.POSITION, player.locale(), positionName)

        player.sendMessage(message)
    }
}