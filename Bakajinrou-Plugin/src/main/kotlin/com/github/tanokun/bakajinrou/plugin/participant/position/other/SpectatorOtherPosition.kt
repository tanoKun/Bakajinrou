package com.github.tanokun.bakajinrou.plugin.participant.position.other

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.plugin.gui.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.participant.position.HasPrefix
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.participant.prefix.LiteralPrefix
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.text

object SpectatorOtherPosition: SpectatorPosition, HasPrefix {
    override val prefix: LiteralPrefix = LiteralPrefix(component { text("観戦") color Positions.Spectator.color.asHexString() })

    override val abilityResult: AbilityResult = AbilityResult.Citizens

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {}
}