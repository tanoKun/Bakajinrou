package com.github.tanokun.bakajinrou.plugin.setting

import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

class GamePlannerTest {
    @Test
    @DisplayName("選択役職に対して、不足した人数ではゲームは開始できない")
    fun cannotPlayWhenShortageOfParticipants() {
        val gamePlanner = GamePlanner(
            random = Random(0),
            jinrouGameProvider = { mockk() },
            loggerProvider = { mockk() },
            gameSchedulerProvider = { _, _, _ -> mockk() },
            bodyHandlerProvider = { mockk() },
            gameSchedulePlanner = { mockk() },
        )

        gamePlanner.selectedMap = mockk()

        assertThrows<IllegalStateException>("現在の参加人数では、選択されている役職が多すぎます。") {
            gamePlanner.createGame(mockk(), mockk())
        }
    }

    @Test
    @DisplayName("選択役職に対して、不足した人数ではゲームは開始できない")
    fun cannotPlayWhenNoSelectedMap() {
        val gamePlanner = GamePlanner(
            random = Random(0),
            jinrouGameProvider = { mockk() },
            loggerProvider = { mockk() },
            gameSchedulerProvider = { _, _, _ -> mockk() },
            bodyHandlerProvider = { mockk() },
            gameSchedulePlanner = { mockk() },
        )

        assertThrows<IllegalStateException>("マップが選択されていません。") {
            gamePlanner.createGame(mockk(), mockk())
        }
    }
}