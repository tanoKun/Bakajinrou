package com.github.tanokun.bakajinrou.bukkit.finishing

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.finishing.GameFinishDecider
import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.bukkit.finishing.finisher.*

class JinrouGameFinishDecider: GameFinishDecider {
    override fun decide(participants: List<Participant>): GameFinisher? {
        val survivors = participants.filter { it.state == ParticipantStates.SURVIVED }
        val citizens = survivors.filter(Participant::isCitizensSide)
        val wolfs = survivors.filter(Participant::isWolf)
        val fox = survivors.filter(Participant::isFox)

        //村人勝利
        if (wolfs.isEmpty() && fox.isEmpty())
            return CitizenSideFinisher(participants)

        //人狼勝利
        if (citizens.isEmpty() && fox.isEmpty())
            return WolfSideFinisher(participants)

        //妖狐勝利
        if (wolfs.isEmpty() || citizens.isEmpty())
            return FoxSideFinisher(participants)

        return null
    }
}