package com.github.tanokun.bakajinrou.api.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DefaultPrefixTest : ShouldSpec({
    val prefixKey = PrefixKeys.CITIZEN
    val source = DefaultPrefix(prefixKey)

    val viewer = mockk<Participant>()
    val target = mockk<Participant>()

    context("デフォルト") {
        should("観察者が死亡している場合、指定されたプレフィックスキーを返すべき") {
            every { viewer.isDead() } returns true

            val result = source.getVisibleSource(viewer, target)

            result shouldBe prefixKey
        }

        should("観察者と対象が同一人物の場合、指定されたプレフィックスキーを返すべき") {
            every { viewer.isDead() } returns false
            val sameParticipant = mockk<Participant> {
                every { isDead() } returns false
            }

            val result = source.getVisibleSource(sameParticipant, sameParticipant)

            result shouldBe prefixKey
        }

        should("観察者が生存しており、かつ対象と異なる場合、nullを返すべき") {
            every { viewer.isDead() } returns false

            val result = source.getVisibleSource(viewer, target)

            result shouldBe null
        }
    }
})