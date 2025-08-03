package com.github.tanokun.bakajinrou.plugin.participant.position.wolf

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.plugin.participant.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.participant.position.HasPrefix
import com.github.tanokun.bakajinrou.plugin.participant.prefix.WolfPrefix

class WolfSecondPosition(private val knownByMadmans: ParticipantScope.NonSpectators): WolfPosition, HasPrefix {
    init {
        if (knownByMadmans.excludePosition<MadmanPosition>().isNotEmpty())
            throw IllegalStateException("唯一知ることのできる参加者は「狂人」でないといけません。")
    }

    override val prefix: WolfPrefix = WolfPrefix(knownByMadmans)

    override val abilityResult: AbilityResult = AbilityResult.Wolf

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {}

    override fun getKnownBy(): ParticipantScope.NonSpectators = knownByMadmans
}