package com.github.tanokun.bakajinrou.api.ability.fortune

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class CorrectDivineAbilityTest : ShouldSpec({
    val ability = CorrectDivineAbility(reason = GrantedReason.INITIALIZED)

    context("占い") {
        should("役職にあった正しい結果を返すべき") {
            val participant = mockk<Participant>()
            every { participant.position } returns mockk<Position> {
                every { abilityResult } returns ResultSource.FOX
            }

            val result = ability.divine(participant)

            result shouldBe ResultSource.FOX
        }
    }
})