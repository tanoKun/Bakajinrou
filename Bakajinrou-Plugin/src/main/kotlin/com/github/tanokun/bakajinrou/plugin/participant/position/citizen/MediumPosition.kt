package com.github.tanokun.bakajinrou.plugin.participant.position.citizen

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.plugin.method.optional.position.MediumHeartItem
import com.github.tanokun.bakajinrou.plugin.participant.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.participant.ability.medium.CorrectMediumUsableAbility
import com.github.tanokun.bakajinrou.plugin.participant.position.HasPrefix
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.participant.prefix.DefaultPrefix

object MediumPosition: CitizensPosition, HasPrefix {
    override val prefix: DefaultPrefix = DefaultPrefix(revealedPrefix = "霊媒師", defaultPrefix = "霊媒師", Positions.Medium.color)

    override val abilityResult: AbilityResult = AbilityResult.Citizens

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
        val mediumHeart = MediumHeartItem(CorrectMediumUsableAbility, participants.nonSpectators())

        self.grantMethod(mediumHeart)
    }
}