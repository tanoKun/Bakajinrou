package com.github.tanokun.bakajinrou.plugin.game.finished.finisher.each

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.participant.position.Side
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.game.audience.GameAudienceStore
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys

class CitizenSideFinishNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val audience: GameAudienceStore,
): EachSideFinishNotifier(translator) {
    override fun notify(wonInfo: WonInfo) {
        if (wonInfo !is WonInfo.Won || wonInfo.side != Side.VILLAGE) return

        wonInfo.participants.forEach { participant ->
            val bukkitPlayer = playerProvider.getAllowNull(participant) ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = translator.translate(GameKeys.Finish.Citizen.TITLE, bukkitPlayer.locale())
            )

            if (participant.isPosition<CitizensPosition>()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)

            bukkitPlayer.sendMessage(translator.translate(GameKeys.Finish.Citizen.MESSAGE, bukkitPlayer.locale()))
        }

        audience.current.spectators.forEach { playerId ->
            val player = playerProvider.getAllowNull(playerId) ?: return@forEach
            showVictorySideTitle(player, translator.translate(GameKeys.Finish.Citizen.TITLE, player.locale()))
        }
    }
}
