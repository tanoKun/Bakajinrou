package com.github.tanokun.bakajinrou.game.method.transferring

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.game.state.GameStore
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class TransferMethodTest : ShouldSpec({
    fun participant(id: ParticipantId, method: GrantedMethod? = null): Participant = Participant(
        id,
        mockk(),
        GrantedStrategy(method?.let { mapOf(it.methodId to it) }.orEmpty()),
        ParticipantStates.ALIVE,
    )

    fun storeOf(vararg participants: Participant) = GameStore(JinrouGame(participants.toList().all()))

    context("参加者間の譲渡") {
        should("正常に譲渡できる場合、両者の状態を同じトランザクションで更新する") {
            val methodId = MethodId(UUID.randomUUID())
            val transferred = mockk<GrantedMethod> {
                every { this@mockk.methodId } returns methodId
            }
            val method = mockk<GrantedMethod> {
                every { this@mockk.methodId } returns methodId
                every { asTransferred() } returns transferred
            }
            val fromId = ParticipantId(UUID.randomUUID())
            val toId = ParticipantId(UUID.randomUUID())
            val store = storeOf(participant(fromId, method), participant(toId))

            TransferMethod(store).transport(methodId, fromId, toId) shouldBe true

            store.getParticipant(fromId)?.hasGrantedMethod(methodId) shouldBe false
            store.getParticipant(toId)?.getGrantedMethod(methodId) shouldBe transferred
        }

        should("譲渡元の参加者が存在しない場合は状態を変更しない") {
            val methodId = MethodId(UUID.randomUUID())
            val fromId = ParticipantId(UUID.randomUUID())
            val toId = ParticipantId(UUID.randomUUID())
            val store = storeOf(participant(toId))

            TransferMethod(store).transport(methodId, fromId, toId) shouldBe false
            store.getParticipant(toId)?.getGrantedMethods()?.isEmpty() shouldBe true
        }

        should("譲渡先の参加者が存在しない場合は状態を変更しない") {
            val methodId = MethodId(UUID.randomUUID())
            val method = mockk<GrantedMethod> {
                every { this@mockk.methodId } returns methodId
            }
            val fromId = ParticipantId(UUID.randomUUID())
            val toId = ParticipantId(UUID.randomUUID())
            val store = storeOf(participant(fromId, method))

            TransferMethod(store).transport(methodId, fromId, toId) shouldBe false
            store.getParticipant(fromId)?.getGrantedMethod(methodId) shouldBe method
        }

        should("譲渡元が指定された手段を持っていない場合は状態を変更しない") {
            val methodId = MethodId(UUID.randomUUID())
            val fromId = ParticipantId(UUID.randomUUID())
            val toId = ParticipantId(UUID.randomUUID())
            val store = storeOf(participant(fromId), participant(toId))

            TransferMethod(store).transport(methodId, fromId, toId) shouldBe false
            store.getParticipant(fromId)?.getGrantedMethods()?.isEmpty() shouldBe true
            store.getParticipant(toId)?.getGrantedMethods()?.isEmpty() shouldBe true
        }
    }
})
