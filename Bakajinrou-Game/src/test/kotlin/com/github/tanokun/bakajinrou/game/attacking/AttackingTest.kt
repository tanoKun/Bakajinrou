package com.github.tanokun.bakajinrou.game.attacking

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attacking.AttackVerificator
import com.github.tanokun.bakajinrou.api.attacking.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import io.kotest.core.spec.style.StringSpec
import io.mockk.*
import kotlinx.coroutines.runBlocking
import java.util.*

class AttackingTest : StringSpec({

    val game = mockk<JinrouGame>(relaxed = true)
    val debug = mockk<DebugLogger>(relaxed = true)
    val attacking = Attacking(game)

    val attackerId = ParticipantId(UUID.randomUUID())
    val victimId = ParticipantId(UUID.randomUUID())
    val methodId = MethodId(UUID.randomUUID())

    val attacker = mockk<Participant>(relaxed = true)
    val victim = mockk<Participant>(relaxed = true)

    beforeTest {
        clearMocks(game, debug, attacker, victim)
    }

    "攻撃ID を持つ 手段 が <T> と型が違う場合は処理しない" {
        val wrongMethod = mockk<AttackMethod>(relaxed = true)
        every { game.getParticipant(attackerId) } returns attacker
        every { game.getParticipant(victimId) } returns victim
        every { attacker.getGrantedMethod(methodId) } returns wrongMethod

        runBlocking {
            attacking.attack(attackerId, listOf(victimId), methodId, DummyAttackMethod::class)
        }

        coVerify(exactly = 0) { AttackVerificator.attack(wrongMethod, victim) }
        coVerify(exactly = 0) { game.updateParticipant(attackerId, any()) }
        coVerify(exactly = 0) { game.updateParticipant(victimId, any()) }
    }

    "攻撃ID を持つ 手段 が <T> と同じ型なら処理を続ける" {
        val correctMethod = mockk<DummyAttackMethod>(relaxed = true)
        val removedAttacker = mockk<Participant>(relaxed = true)

        every { game.getParticipant(attackerId) } returns attacker
        every { game.getParticipant(victimId) } returns victim
        every { attacker.getGrantedMethod(methodId) } returns correctMethod
        every { attacker.removeMethod(correctMethod) } returns removedAttacker
        mockkObject(AttackVerificator)

        runBlocking {
            attacking.attack(attackerId, listOf(victimId), methodId, DummyAttackMethod::class)
        }

        coVerify(exactly = 1) { AttackVerificator.attack(correctMethod, victim) }
        coVerify(exactly = 1) { game.updateParticipant(attackerId, any()) }
        coVerify(exactly = 1) { game.updateParticipant(victimId, any()) }
    }

    "攻撃手段でない場合は処理をしない" {
        val notAttackMethod = mockk<GrantedMethod>()
        every { game.getParticipant(attackerId) } returns attacker
        every { attacker.getGrantedMethod(methodId) } returns notAttackMethod

        runBlocking {
            attacking.attack(attackerId, listOf(victimId), methodId, DummyAttackMethod::class)
        }

        coVerify(exactly = 0) { game.updateParticipant(attackerId, any()) }
        coVerify(exactly = 0) { game.updateParticipant(victimId, any()) }
    }
}) {

    class DummyAttackMethod(
        override val methodId: MethodId,
        override val assetKey: MethodAssetKeys.Attack,
        override val reason: GrantedReason
    ) : AttackMethod() {
        override fun asTransferred(): GrantedMethod = throw Error()
    }
}