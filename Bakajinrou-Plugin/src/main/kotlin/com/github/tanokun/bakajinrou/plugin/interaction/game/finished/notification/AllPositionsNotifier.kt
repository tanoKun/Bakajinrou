package com.github.tanokun.bakajinrou.plugin.interaction.game.finished.notification

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.formatter.ParticipantsFormatter
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class AllPositionsNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val gameSession: JinrouGameSession,
    private val topScope: CoroutineScope,
): Observer {
    init {
        topScope.launch {
            gameSession.observeWin(topScope)
                .take(1)
                .collect(::notifyAllPositions)
        }
    }

    private fun notifyAllPositions(wonInfo: WonInfo) {
        val formatter = ParticipantsFormatter(wonInfo.participants.excludeSpectators(), translator)

        wonInfo.participants.forEach { participant ->
            val player = playerProvider.getAllowNull(participant.participantId) ?: return@forEach
            val locale = player.locale()

            player.sendMessage(formatter.formatWolf(locale))
            player.sendMessage(formatter.formatMadman(locale))
            player.sendMessage(formatter.formatFortune(locale))
            player.sendMessage(formatter.formatMedium(locale))
            player.sendMessage(formatter.formatKnight(locale))
            player.sendMessage(formatter.formatCitizen(locale))
            player.sendMessage(formatter.formatFox(locale))
        }
    }
}