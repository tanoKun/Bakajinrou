package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.Participant
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class JinrouGameTest {
    @Test
    @DisplayName("重複した参加者がいるゲームは不可")
    fun cannotDuplicateParticipant() {
        val duplication = UUID.randomUUID()

        assertThrows<IllegalArgumentException>("重複したプレイヤーが存在します。") {
            JinrouGame(
                participants = listOf(
                    Participant(duplication, mockk(), mockk()),
                    Participant(duplication, mockk(), mockk()),
                    Participant(UUID.randomUUID(), mockk(), mockk())
                ),
                citizenSideFinisher = mockk(),
                wolfSideFinisher = mockk(),
                foxSideFinisher = mockk(),
            )
        }
    }

    @Test
    @DisplayName("全て重複なしの参加者の場合、ゲームは可能")
    fun canDoWithoutDuplicateParticipant() {
        assertDoesNotThrow {
            JinrouGame(
                participants = listOf(
                    Participant(UUID.randomUUID(), mockk(), mockk()),
                    Participant(UUID.randomUUID(), mockk(), mockk()),
                    Participant(UUID.randomUUID(), mockk(), mockk())
                ),
                citizenSideFinisher = mockk(),
                wolfSideFinisher = mockk(),
                foxSideFinisher = mockk(),
            )
        }
    }
}