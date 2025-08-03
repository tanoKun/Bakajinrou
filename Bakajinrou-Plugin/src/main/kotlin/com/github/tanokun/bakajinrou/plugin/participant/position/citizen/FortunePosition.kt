package com.github.tanokun.bakajinrou.plugin.participant.position.citizen

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.plugin.method.optional.position.FortuneBookItem
import com.github.tanokun.bakajinrou.plugin.participant.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.participant.ability.fortune.CorrectFortuneUsableAbility
import com.github.tanokun.bakajinrou.plugin.participant.position.HasPrefix
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.participant.prefix.DefaultPrefix

object FortunePosition: CitizensPosition, HasPrefix {
    override val prefix: DefaultPrefix = DefaultPrefix(revealedPrefix = "占い師", defaultPrefix = "占い師", Positions.Fortune.color)

    override val abilityResult: AbilityResult = AbilityResult.Citizens

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
       val fortuneBook = FortuneBookItem(CorrectFortuneUsableAbility, participants.nonSpectators())

        self.grantMethod(fortuneBook)
    }
}