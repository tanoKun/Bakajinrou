package com.github.tanokun.bakajinrou.api.position

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.Position
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class ParticipantTest {
    object TempPosition: Position {

        override fun doAtStarting() = Unit
    }
    
    val nonPlayerProvider = { throw IllegalStateException("") }

    @Test
    @DisplayName("生存状態から死亡状態へ遷移できる")
    fun canChangeSurvivedToDead() {
        val player = Participant(UUID.randomUUID(), TempPosition, nonPlayerProvider)
        val result = player.dead()

        assertTrue(result, "生存状態から死亡状態へ遷移できるべき")
    }

    @Test
    @DisplayName("中断状態から生存状態へ遷移できる")
    fun changeSuspendedToSurvived() {
        val player = Participant(UUID.randomUUID(), TempPosition, nonPlayerProvider).apply { suspended() }

        assertTrue(player.survived(), "中断状態から生存状態に戻れるべき")
    }

    @Test
    @DisplayName("死亡状態から他の状態へ遷移できない")
    fun cannotChangeDeadToAnotherState() {
        val player = Participant(UUID.randomUUID(), TempPosition, nonPlayerProvider).apply { dead() }

        assertFalse(player.survived(), "死亡状態から生存には遷移できない")
        assertFalse(player.suspended(), "死亡状態から中断には遷移できない")
    }

    @Test
    @DisplayName("同じ状態への遷移は無効")
    fun cannotChangeSameState() {
        val survived = Participant(UUID.randomUUID(), TempPosition, nonPlayerProvider)
        assertFalse(survived.survived())

        val dead = Participant(UUID.randomUUID(), TempPosition, nonPlayerProvider).apply { dead() }
        assertFalse(dead.dead())

        val suspended = Participant(UUID.randomUUID(), TempPosition, nonPlayerProvider).apply { suspended() }
        assertFalse(suspended.suspended())
    }
}