package com.github.tanokun.bakajinrou.api.ability.medium

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk

class CorrectCommuneAbilityTest : ShouldSpec({
    val ability = CorrectCommuneAbility(reason = GrantedReason.INITIALIZED)

    context("霊媒") {
        should("対象が死亡している場合、役職にあった正しい結果を返すべき") {
            val participant = mockk<Participant> {
                every { isDead() } returns true
                every { position } returns mockk {
                    every { abilityResult } returns ResultSource.CITIZENS
                }
            }

            val result = ability.commune(participant)

            result.shouldBeInstanceOf<CommuneResultSource.FoundResult>()
            result.resultKey shouldBe ResultSource.CITIZENS
        }

        should("対象が生存している場合、NotDeadErrorを返すべき") {
            val participant = mockk<Participant> {
                every { isDead() } returns false
            }

            val result = ability.commune(participant)

            result shouldBe CommuneResultSource.NotDeadError
        }
    }
})