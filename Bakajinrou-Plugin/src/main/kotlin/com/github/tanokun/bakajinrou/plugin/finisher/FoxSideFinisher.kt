package com.github.tanokun.bakajinrou.plugin.finisher

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

class FoxSideFinisher(
    jinrouGame: JinrouGame,
    scope: CoroutineScope
): EachSideFinisher(jinrouGame, scope) {
    override fun notify(wonInfo: WonInfo) {
        if (wonInfo !is WonInfo.Fox) return

        wonInfo.participants.forEach { participant ->
            val bukkitPlayer = BukkitPlayerProvider.get(participant) ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = Component.text("妖狐の勝利")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(0x800080))
            )

            if (participant.isPosition<SpectatorPosition>()) return@forEach

            if (participant.isPosition<FoxPosition>()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)
        }
    }
}