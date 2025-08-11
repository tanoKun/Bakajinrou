package com.github.tanokun.bakajinrou.api.ability.medium

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlin.random.Random

class FakeCommuneAbilityTest : ShouldSpec({
    val random = mockk<Random>()
    val ability = FakeCommuneAbility(reason = GrantedReason.INITIALIZE, random = random)
    val participant = mockk<Participant>()

    context("偽霊媒") {
        should("乱数が0でない場合、ランダムな役職結果を返すべき") {
            every { random.nextInt(4) } returns 1

            val expectedResult = ResultSource.FOX
            every { random.nextInt(ResultSource.entries.size) } returns ResultSource.entries.indexOf(expectedResult)

            val result = ability.commune(participant)

            result.shouldBeInstanceOf<CommuneResultSource.FoundResult>()
            result.resultKey shouldBe expectedResult
        }

        should("乱数が0の場合、NotDeadErrorを返すべき") {
            every { random.nextInt(3) } returns 0
            every { random.nextInt(4) } returns 0

            val result = ability.commune(participant)

            result shouldBe CommuneResultSource.NotDeadError
        }
    }
})