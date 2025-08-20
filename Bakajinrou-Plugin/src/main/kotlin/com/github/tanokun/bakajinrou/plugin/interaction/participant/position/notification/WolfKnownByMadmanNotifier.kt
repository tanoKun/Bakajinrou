package com.github.tanokun.bakajinrou.plugin.interaction.participant.position.notification

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.position.isMadman
import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.game.participant.initialization.ParticipantInitializer
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import net.kyori.adventure.text.Component
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class WolfKnownByMadmanNotifier(
    private val game: JinrouGame,
    private val translator: JinrouTranslator,
    gameSession: JinrouGameSession,
    private val playerProvider: BukkitPlayerProvider,
) : ParticipantInitializer(game, gameSession, ::isMadman) {
    override suspend fun initialize(selfId: ParticipantId) {
        val known = game.getCurrentParticipants()
            .includes(::isWolf)
            .includes {
                val position = it.position as WolfPosition
                position.knownByMadmans.contains(selfId)
            }
            .firstOrNull() ?: return

        val knownName = PlayerNameCache.get(known) ?: return

        playerProvider.waitPlayerOnline(selfId) { player ->
            player.sendMessage(translator.translate(GameKeys.Start.Notification.KNOWN_WOLF, player.locale(), Component.text(knownName)))
        }
    }
}