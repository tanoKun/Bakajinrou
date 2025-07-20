package com.github.tanokun.bakajinrou.bukkit.finishing.finisher

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.bukkit.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.FortunePosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.MediumPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.idiot.IdiotPosition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

class CitizenSideFinisher(
    private val participants: List<Participant>
): EachSideFinisher() {
    override fun notifyFinish() {
        participants.forEach { participant ->
            val bukkitPlayer = participant.bukkitPlayerProvider() ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = Component.text("村人の勝利")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(0x00FF00))
            )

            if (participant.isCitizensSide()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)
        }
    }
}

fun Participant.isCitizensSide(): Boolean =
    this.position is FortunePosition
        || this.position is MediumPosition
        || this.position is CitizenPosition
        || this.position is IdiotPosition