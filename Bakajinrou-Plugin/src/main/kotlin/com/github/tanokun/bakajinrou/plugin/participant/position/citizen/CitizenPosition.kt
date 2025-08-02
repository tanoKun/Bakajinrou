package com.github.tanokun.bakajinrou.plugin.participant.position.citizen

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.plugin.gui.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.participant.position.HasPrefix
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.participant.prefix.DefaultPrefix

object CitizenPosition: CitizensPosition, HasPrefix {
    override val prefix: DefaultPrefix = DefaultPrefix(revealedPrefix = "市民", defaultPrefix = "市民", Positions.Citizen.color)

    override val abilityResult: AbilityResult = AbilityResult.Citizens

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {}
}