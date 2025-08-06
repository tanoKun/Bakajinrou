package com.github.tanokun.bakajinrou.plugin.finisher

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

class CitizenSideFinisher(
    jinrouGame: JinrouGame,
    scope: CoroutineScope
): EachSideFinisher(jinrouGame, scope) {
    override fun notify(wonInfo: WonInfo) {
        if (wonInfo !is WonInfo.Citizens) return

        wonInfo.participants.forEach { participant ->
            val bukkitPlayer = BukkitPlayerProvider.get(participant) ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = Component.text("市民の勝利")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(0x00FF00))
            )

            if (participant.isPosition<SpectatorPosition>()) return@forEach

            if (participant.isPosition<CitizensPosition>()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)
        }
    }
}