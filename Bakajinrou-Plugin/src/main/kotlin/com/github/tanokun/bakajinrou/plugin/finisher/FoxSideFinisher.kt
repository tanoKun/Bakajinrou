package com.github.tanokun.bakajinrou.plugin.finisher

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

class FoxSideFinisher(
    private val game: JinrouGame
): EachSideFinisher() {
    override fun notifyFinish() {
        game.getAllParticipants().forEach { participant ->
            val bukkitPlayer = getBukkitPlayer(participant) ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = Component.text("妖狐の勝利")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(0x800080))
            )

            if (participant.isPosition<FoxPosition>()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)
        }
    }
}