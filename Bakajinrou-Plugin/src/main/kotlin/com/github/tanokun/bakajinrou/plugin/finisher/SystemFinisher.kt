package com.github.tanokun.bakajinrou.plugin.finisher

import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import org.bukkit.Bukkit
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.darkRed
import plutoproject.adventurekt.text.text

class SystemFinisher(
    private val participants: ParticipantScope.All
): GameFinisher {
    override fun notifyFinish() {
        participants.forEach { participant ->
            val bukkitPlayer = getBukkitPlayer(participant) ?: return@forEach

            bukkitPlayer.sendMessage(component { text("強制終了") color darkRed deco bold })
        }
    }

    private fun getBukkitPlayer(participant: Participant) = Bukkit.getPlayer(participant.uniqueId)
}