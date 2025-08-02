package com.github.tanokun.bakajinrou.plugin.participant.position.citizen

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.plugin.gui.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.gui.ability.knight.RealKnightUsableAbility
import com.github.tanokun.bakajinrou.plugin.method.optional.position.KnightGrantItem
import com.github.tanokun.bakajinrou.plugin.participant.position.HasPrefix
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.participant.prefix.DefaultPrefix

object KnightPosition: CitizensPosition, HasPrefix {
    override val prefix: DefaultPrefix = DefaultPrefix(revealedPrefix = "騎士", defaultPrefix = "騎士", Positions.Knight.color)

    override val abilityResult: AbilityResult = AbilityResult.Citizens

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
        val knightGrant = KnightGrantItem(RealKnightUsableAbility, participants.nonSpectators())

        self.grantMethod(knightGrant)
    }
}