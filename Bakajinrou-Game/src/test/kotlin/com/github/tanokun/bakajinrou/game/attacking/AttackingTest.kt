package com.github.tanokun.bakajinrou.game.attacking

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.attacking.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.state.GameStore
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.util.UUID

class AttackingTest : StringSpec({
    fun participant(id: ParticipantId, method: GrantedMethod? = null): Participant = Participant(
        id,
        mockk(),
        GrantedStrategy(method?.let { mapOf(it.methodId to it) }.orEmpty()),
        ParticipantStates.ALIVE,
    )

    val attackerId = ParticipantId(UUID.randomUUID())
    val victimId = ParticipantId(UUID.randomUUID())
    val methodId = MethodId(UUID.randomUUID())

    "攻撃IDを持つ手段の型が指定と違う場合は状態を変更しない" {
        val wrongMethod = OtherAttackMethod(methodId, mockk(), GrantedReason.SYSTEM)
        val store = GameStore(JinrouGame(listOf(
            participant(attackerId, wrongMethod),
            participant(victimId),
        ).all()))

        runBlocking {
            Attacking(store).attack(attackerId, listOf(victimId), methodId, DummyAttackMethod::class)
        }

        store.getParticipant(attackerId)?.hasGrantedMethod(methodId) shouldBe true
        store.getParticipant(victimId)?.isAlive() shouldBe true
    }

    "攻撃IDを持つ手段の型が指定と同じなら攻撃結果を一括反映する" {
        val method = DummyAttackMethod(methodId, mockk(), GrantedReason.SYSTEM)
        val store = GameStore(JinrouGame(listOf(
            participant(attackerId, method),
            participant(victimId),
        ).all()))

        runBlocking {
            Attacking(store).attack(attackerId, listOf(victimId), methodId, DummyAttackMethod::class)
        }

        store.getParticipant(attackerId)?.hasGrantedMethod(methodId) shouldBe false
        store.getParticipant(victimId)?.isDead() shouldBe true
    }

    "攻撃手段でない場合は状態を変更しない" {
        val notAttackMethod = mockk<GrantedMethod> {
            every { this@mockk.methodId } returns methodId
        }
        val store = GameStore(JinrouGame(listOf(
            participant(attackerId, notAttackMethod),
            participant(victimId),
        ).all()))

        runBlocking {
            Attacking(store).attack(attackerId, listOf(victimId), methodId, DummyAttackMethod::class)
        }

        store.getParticipant(attackerId)?.hasGrantedMethod(methodId) shouldBe true
        store.getParticipant(victimId)?.isAlive() shouldBe true
    }
}) {
    class DummyAttackMethod(
        override val methodId: MethodId,
        override val assetKey: MethodAssetKeys.Attack,
        override val reason: GrantedReason,
    ) : AttackMethod() {
        override fun asTransferred(): GrantedMethod = error("このテストでは譲渡しない")
    }

    class OtherAttackMethod(
        override val methodId: MethodId,
        override val assetKey: MethodAssetKeys.Attack,
        override val reason: GrantedReason,
    ) : AttackMethod() {
        override fun asTransferred(): GrantedMethod = error("このテストでは譲渡しない")
    }
}
