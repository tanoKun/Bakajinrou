package com.github.tanokun.bakajinrou.api.ability.fortune

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.random.Random

class FakeDivineAbilityTest : ShouldSpec({
    context("偽占い") {
        should("役職に関わらず、指定されたランダムな結果を返すべき") {
            val mockRandom = mockk<Random>()
            val ability = FakeDivineAbility(reason = GrantedReason.INITIALIZE, random = mockRandom)
            val participant = mockk<Participant>()

            val expectedResult = ResultSource.WOLF
            every { mockRandom.nextInt(ResultSource.entries.size) } returns ResultSource.entries.indexOf(expectedResult)

            val result = ability.divine(participant)

            result shouldBe expectedResult
        }
    }
})