package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

class JinrouGameTest {
    @Test
    @DisplayName("参加者を更新しても元のゲーム状態は変更されない")
    fun updateParticipantReturnsNewGame() {
        val participant = Participant(UUID.randomUUID().asParticipantId(), mockk(), mockk())
        val dead = participant.dead()
        val game = JinrouGame(listOf(participant).all())

        val updated = game.updateParticipant(participant.participantId) { dead }

        game.getParticipant(participant.participantId) shouldBe participant
        updated.getParticipant(participant.participantId) shouldBe dead
    }
}
