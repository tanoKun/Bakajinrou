package com.github.tanokun.bakajinrou.plugin.participant.method.attack.adapting

import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.util.UUID

class ScatterShotTrackerTest : StringSpec({
    "三本すべてが着弾するまで手段を完了しない" {
        val tracker = ScatterShotTracker()
        val shooterId = ParticipantId(UUID.randomUUID())
        val methodId = MethodId(UUID.randomUUID())
        val projectiles = List(3) { UUID.randomUUID() }

        projectiles.forEach { tracker.register(shooterId, methodId, it) }

        tracker.land(projectiles[0]).shouldBeNull()
        tracker.land(projectiles[1]).shouldBeNull()
        tracker.land(projectiles[2]) shouldBe CompletedScatterShot(shooterId, methodId)
    }

    "有効な命中で完了した後は残りの飛翔体を無視する" {
        val tracker = ScatterShotTracker()
        val shooterId = ParticipantId(UUID.randomUUID())
        val methodId = MethodId(UUID.randomUUID())
        val projectiles = List(3) { UUID.randomUUID() }

        projectiles.forEach { tracker.register(shooterId, methodId, it) }

        tracker.complete(methodId) shouldBe CompletedScatterShot(shooterId, methodId)
        projectiles.forEach { tracker.land(it).shouldBeNull() }
    }

    "最初の飛翔体だけがタイムアウト監視を開始する" {
        val tracker = ScatterShotTracker()
        val shooterId = ParticipantId(UUID.randomUUID())
        val methodId = MethodId(UUID.randomUUID())

        tracker.register(shooterId, methodId, UUID.randomUUID()) shouldBe true
        tracker.register(shooterId, methodId, UUID.randomUUID()) shouldBe false
    }
})
