package com.github.tanokun.bakajinrou.game.chat

import com.github.tanokun.bakajinrou.api.participant.Participant
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class ChatIntegrityTest {
    private val integrity = ChatIntegrity

    @Test
    @DisplayName("送信者と受信者が同一の場合は送信される")
    fun sendWhenSenderEqualsReceiver() {
        val participant = mockk<Participant> {
            every { isDead() } returns true // 状態は関係ない
        }

        val result = integrity.isSendingAllowed(participant, participant)

        assertTrue(result)
    }

    @Test
    @DisplayName("送信者が生存状態の場合は送信される")
    fun sendWhenSenderIsAlive() {
        val sender = mockk<Participant> {
            every { isDead() } returns false
        }
        val receiver = mockk<Participant> {
            every { isDead() } returns true // 状態は関係ない
        }

        val result = integrity.isSendingAllowed(sender, receiver)

        assertTrue(result)
    }

    @Test
    @DisplayName("送信者と受信者がともに死亡状態の場合は送信される")
    fun sendWhenBothAreDead() {
        val sender = mockk<Participant> {
            every { isDead() } returns true
        }
        val receiver = mockk<Participant> {
            every { isDead() } returns true
        }

        val result = integrity.isSendingAllowed(sender, receiver)

        assertTrue(result)
    }

    @Test
    @DisplayName("送信者が死亡状態で、受信者が生存状態の場合は送信されない")
    fun notSendWhenSenderDeadAndReceiverAlive() {
        val sender = mockk<Participant> {
            every { isDead() } returns true
        }
        val receiver = mockk<Participant> {
            every { isDead() } returns false
        }

        val result = integrity.isSendingAllowed(sender, receiver)

        assertFalse(result)
    }
}