package com.github.tanokun.bakajinrou.game.method.transferring

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

class TransferMethodTest : ShouldSpec({
    val methodId = mockk<MethodId>()
    val method = mockk<GrantedMethod>()

    val fromId = mockk<ParticipantId>()
    val toId = mockk<ParticipantId>()

    val fromParticipant = mockk<Participant> {
        every { getGrantedMethod(methodId) } returns method
    }
    val toParticipant = mockk<Participant>()

    val game = mockk<JinrouGame> {
        coEvery { getParticipant(fromId) } returns fromParticipant
        coEvery { existParticipant(toId) } returns true
        coEvery { updateParticipant(any(), any()) } just Runs
    }

    val transfer = TransferMethod(game)

    context("参加者間の譲渡") {
        should("正常に譲渡できる場合、trueを返し、両者の状態を更新すべき") {
            val fromAfterRemove = mockk<Participant>()
            val toAfterGrant = mockk<Participant>()

            every { fromParticipant.removeMethod(method) } returns fromAfterRemove
            every { toParticipant.grantMethod(method) } returns toAfterGrant

            val result = transfer.transport(methodId, fromId, toId)

            result shouldBe true

            coVerify(exactly = 1) { game.updateParticipant(fromId, any()) }
            coVerify(exactly = 1) { game.updateParticipant(toId, any()) }
        }

        should("譲渡元の参加者が存在しない場合、falseを返し、更新処理を呼ばないべき") {
            coEvery { game.getParticipant(fromId) } returns null

            val result = transfer.transport(methodId, fromId, toId)

            result shouldBe false
            coVerify(exactly = 0) { game.updateParticipant(any(), any()) }
        }

        should("譲渡先の参加者が存在しない場合、falseを返し、更新処理を呼ばないべき") {
            coEvery { game.existParticipant(toId) } returns false

            val result = transfer.transport(methodId, fromId, toId)

            result shouldBe false
            coVerify(exactly = 0) { game.updateParticipant(any(), any()) }
        }

        should("譲渡元が指定された手段を持っていない場合、falseを返し、更新処理を呼ばないべき") {
            every { fromParticipant.getGrantedMethod(methodId) } returns null

            val result = transfer.transport(methodId, fromId, toId)

            result shouldBe false
            coVerify(exactly = 0) { game.updateParticipant(any(), any()) }
        }
    }
})