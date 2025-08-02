package com.github.tanokun.bakajinrou.plugin.participant.position.wolf

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.plugin.gui.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.participant.position.HasPrefix
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.participant.prefix.DefaultPrefix

object MadmanSecondPosition: MadmanPosition, HasPrefix {
    override val prefix: DefaultPrefix = DefaultPrefix(revealedPrefix = "狂人", defaultPrefix = "狂人", Positions.Madman.color)

    override val abilityResult: AbilityResult = AbilityResult.Citizens

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {}
}