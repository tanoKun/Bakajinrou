package com.github.tanokun.bakajinrou.plugin.observer

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class ParticipantStateObserverTest {
    private val testDefaultDispatcher = StandardTestDispatcher()
    private val testUIDispatcher = StandardTestDispatcher()
    private val testScope = CoroutineScope(SupervisorJob() + testDefaultDispatcher)

    @Test
    @DisplayName("死亡の際、ゲームモードをスペクテイターにする")
    fun changeGameModeToSpectatorTest() = runTest {
        val uniqueId = UUID.randomUUID()

        val playerMock = mockk<Player> {
            every { gameMode = GameMode.SPECTATOR } just runs
        }

        val participantMock = Participant(uniqueId, mockk(), mockk(), mockk())

        val jinrouGameMock = mockk<JinrouGame> {
            every { participants } returns listOf(participantMock)
        }

        val controllerMock = mockk<JinrouGameController> {
            every { scope } returns testScope
        }

        mockkStatic(Bukkit::class)
        every { Bukkit.getPlayer(uniqueId) } returns playerMock
        every { Bukkit.getOnlinePlayers() } returns listOf()

        ParticipantStateObserver(
            jinrouGame = jinrouGameMock,
            controller = controllerMock,
            asyncContext = testDefaultDispatcher,
            uiContext = testUIDispatcher
        )

        participantMock.dead()

        testDefaultDispatcher.scheduler.advanceUntilIdle()
        testUIDispatcher.scheduler.advanceUntilIdle()

        verify(exactly = 1) { playerMock.gameMode = GameMode.SPECTATOR }
    }
}