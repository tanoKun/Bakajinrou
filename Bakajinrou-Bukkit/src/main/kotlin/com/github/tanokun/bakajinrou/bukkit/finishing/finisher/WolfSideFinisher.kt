package com.github.tanokun.bakajinrou.bukkit.finishing.finisher

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.bukkit.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.bukkit.position.wolf.WolfPosition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

class WolfSideFinisher(
    private val participants: List<Participant>
): EachSideFinisher() {
    override fun notifyFinish() {
        participants.forEach { participant ->
            val bukkitPlayer = participant.bukkitPlayerProvider() ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = Component.text("人狼の勝利")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(0x8B0000))
            )

            if (participant.isWolf() || participant.isMadman()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)
        }
    }
}

fun Participant.isWolf(): Boolean =
    this.position is WolfPosition

fun Participant.isMadman(): Boolean =
    this.position is MadmanPosition