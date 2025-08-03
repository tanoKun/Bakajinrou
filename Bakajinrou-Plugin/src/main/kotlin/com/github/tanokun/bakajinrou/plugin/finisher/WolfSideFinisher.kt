package com.github.tanokun.bakajinrou.plugin.finisher

import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

class WolfSideFinisher(
    private val participants: ParticipantScope.All
): EachSideFinisher() {
    override fun notifyFinish() {
        participants.forEach { participant ->
            val bukkitPlayer = getBukkitPlayer(participant) ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = Component.text("人狼の勝利")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(0x8B0000))
            )

            if (participant.isPosition<WolfPosition>() || participant.isPosition<MadmanPosition>()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)
        }
    }
}