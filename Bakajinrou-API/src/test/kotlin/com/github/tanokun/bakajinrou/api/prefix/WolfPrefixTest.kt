package com.github.tanokun.bakajinrou.api.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.api.participant.prefix.WolfPrefix
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class WolfPrefixTest : ShouldSpec({

    val prefixKey = PrefixKeys.Companion.WOLF
    val knownByMadmans = mockk<ParticipantScope.NonSpectators>()
    val source = WolfPrefix(knownByMadmans)

    val viewer = mockk<Participant>()
    val target = mockk<Participant>()

    context("人狼用") {
        beforeEach {
            every { viewer.isDead() } returns false
            every { viewer.position } returns mockk<CitizenPosition>()
            every { knownByMadmans.contains(viewer) } returns false
        }

        should("観察者が死亡している場合、人狼プレフィックスを返すべき") {
            every { viewer.isDead() } returns true
            source.getVisibleSource(viewer, target) shouldBe prefixKey
        }

        should("観察者が人狼である場合、人狼プレフィックスを返すべき") {
            every { viewer.position } returns mockk<WolfPosition>()
            source.getVisibleSource(viewer, target) shouldBe prefixKey
        }

        should("観察者が狂人に認知されている場合、人狼プレフィックスを返すべき") {
            every { knownByMadmans.contains(viewer) } returns true
            source.getVisibleSource(viewer, target) shouldBe prefixKey
        }

        should("観察者が自分の場合、人狼プレフィックスを返すべき") {
            source.getVisibleSource(viewer, viewer) shouldBe prefixKey
        }

        should("上記のいずれの条件も満たさない場合、nullを返すべき") {
            source.getVisibleSource(viewer, target) shouldBe null
        }
    }
})