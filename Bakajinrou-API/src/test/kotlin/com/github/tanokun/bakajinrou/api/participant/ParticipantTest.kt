package com.github.tanokun.bakajinrou.api.participant

import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ParticipantTest {

    @Test
    @DisplayName("生存状態から死亡状態へ遷移できる")
    fun canChangeSurvivedToDead() {
        val player = Participant(mockk(), mockk(), mockk())
        val result = player.dead()

        Assertions.assertTrue(result, "生存状態から死亡状態へ遷移できるべき")
    }

    @Test
    @DisplayName("中断状態から生存状態へ遷移できる")
    fun changeSuspendedToSurvived() {
        val player = createParticipant().apply { suspended() }

        Assertions.assertTrue(player.survived(), "中断状態から生存状態に戻れるべき")
    }

    @Test
    @DisplayName("死亡状態から他の状態へ遷移できない")
    fun cannotChangeDeadToAnotherState() {
        val player = createParticipant().apply { dead() }

        Assertions.assertFalse(player.survived(), "死亡状態から生存には遷移できない")
        Assertions.assertFalse(player.suspended(), "死亡状態から中断には遷移できない")
    }

    @Test
    @DisplayName("同じ状態への遷移は無効")
    fun cannotChangeSameState() {
        val survived = createParticipant()
        Assertions.assertFalse(survived.survived())

        val dead = createParticipant().apply { dead() }
        Assertions.assertFalse(dead.dead())

        val suspended = createParticipant().apply { suspended() }
        Assertions.assertFalse(suspended.suspended())
    }
    
    private fun createParticipant() = Participant(mockk(), mockk(), mockk())
}