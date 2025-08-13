package com.github.tanokun.bakajinrou.api.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.prefix.IdiotPrefix
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class IdiotPrefixTest : ShouldSpec({

    val realKey = PrefixKeys.Idiot.MEDIUM
    val fakeKey = PrefixKeys.Mystic.MEDIUM
    val source = IdiotPrefix(realKey, fakeKey)

    context("バカ用") {
        should("観察者が死亡している場合、本物のバカのプレフィックスキーを返すべき") {
            val viewer = mockk<Participant> {
                every { isDead() } returns true
            }
            val target = mockk<Participant>()

            val result = source.getVisibleSource(viewer, target)

            result shouldBe realKey
        }

        should("観察者が自分の場合、偽の役職のプレフィックスキーを返すべき") {
            val selfViewer = mockk<Participant> {
                every { isDead() } returns false
            }

            val result = source.getVisibleSource(selfViewer, selfViewer)

            result shouldBe fakeKey
        }

        should("観察者が生存しており、かつ他人を見ている場合、nullを返すべき") {
            val viewer = mockk<Participant> {
                every { isDead() } returns false
            }
            val target = mockk<Participant>() // 異なるインスタンス

            val result = source.getVisibleSource(viewer, target)

            result shouldBe null
        }
    }
})