package com.github.tanokun.bakajinrou.plugin.finisher

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.bukkit.position.fox.FoxPosition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

class FoxSideFinisher(
    private val participants: List<Participant>
): EachSideFinisher() {
    override fun notifyFinish() {
        participants.forEach { participant ->
            val bukkitPlayer = participant.bukkitPlayerProvider() ?: return@forEach

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