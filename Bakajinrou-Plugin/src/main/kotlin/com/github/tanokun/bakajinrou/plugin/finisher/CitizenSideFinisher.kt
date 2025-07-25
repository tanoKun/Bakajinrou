package com.github.tanokun.bakajinrou.plugin.finisher

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

class CitizenSideFinisher(
    private val game: JinrouGame
): EachSideFinisher() {
    override fun notifyFinish() {
        game.participants.forEach { participant ->
            val bukkitPlayer = getBukkitPlayer(participant) ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = Component.text("市民の勝利")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(0x00FF00))
            )

            if (participant.isPosition<CitizensPosition>()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)
        }
    }
}