package com.github.tanokun.bakajinrou.plugin

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.bukkit.controller.JinrouGameController
import com.github.tanokun.bakajinrou.bukkit.finishing.JinrouGameFinishDecider
import com.github.tanokun.bakajinrou.plugin.finisher.CitizenSideFinisher
import com.github.tanokun.bakajinrou.plugin.finisher.FoxSideFinisher
import com.github.tanokun.bakajinrou.plugin.finisher.WolfSideFinisher
import com.github.tanokun.bakajinrou.plugin.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.MediumPosition
import com.github.tanokun.bakajinrou.plugin.position.fox.FoxThirdPosition
import com.github.tanokun.bakajinrou.plugin.position.wolf.WolfSecondPosition
import io.mockk.mockk
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.entity.PlayerMock
import kotlin.test.Test
import kotlin.test.assertEquals


class FinishGameByKillingTest {
    private lateinit var server: ServerMock
    
    private lateinit var wolf: PlayerMock
    private lateinit var citizen: PlayerMock
    private lateinit var medium: PlayerMock
    private lateinit var fox: PlayerMock

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()

        wolf = server.addPlayer()
        citizen = server.addPlayer()
        medium = server.addPlayer()
        fox = server.addPlayer()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    @DisplayName("最後の人狼を殺害でのゲーム終了。(妖狐無し)")
    fun finishGameByKillingLastWolfWithoutFoxTest() {
        val gameController = createGame()

        gameController.killed(victim = fox.uniqueId, by = citizen.uniqueId)
        gameController.killed(victim = wolf.uniqueId, by = citizen.uniqueId)

        assertLose(wolf)
        assertVictory(citizen)
        assertVictory(medium)
        assertLose(fox)
    }

    @Test
    @DisplayName("最後の村人を殺害でのゲーム終了。(妖狐無し)")
    fun finishGameByKillingLastCitizenWithoutFoxTest() {
        val gameController = createGame()

        gameController.killed(victim = fox.uniqueId, by = wolf.uniqueId)
        gameController.killed(victim = citizen.uniqueId, by = wolf.uniqueId)
        gameController.killed(victim = medium.uniqueId, by = wolf.uniqueId)

        assertVictory(wolf)
        assertLose(citizen)
        assertLose(medium)
        assertLose(fox)
    }

    @Test
    @DisplayName("最後の人狼を殺害でのゲーム終了。(妖狐有り)")
    fun finishGameByKillingLastCitizenWithFoxTest() {
        val gameController = createGame()

        gameController.killed(victim = wolf.uniqueId, by = fox.uniqueId)

        assertLose(wolf)
        assertLose(citizen)
        assertLose(medium)
        assertVictory(fox)
    }

    fun createGame(): JinrouGameController {
        val wolf = Participant(wolf.uniqueId, WolfSecondPosition) { wolf }
        val citizen = Participant(citizen.uniqueId, CitizenPosition) { citizen }
        val medium = Participant(medium.uniqueId, MediumPosition) { medium }
        val fox = Participant(fox.uniqueId, FoxThirdPosition) { fox }

        val game = JinrouGame(
            participants = listOf(wolf, citizen, medium, fox)
        )

        return JinrouGameController(
            game = game,
            finishDecider = JinrouGameFinishDecider(
                citizenSideFinisher = { CitizenSideFinisher(it) },
                wolfSideFinisher = { WolfSideFinisher(it) },
                foxSideFinisher = { FoxSideFinisher(it) }
            ),
            logger = mockk(relaxed = true),
            scheduler = mockk(relaxed = true),
            bodyHandler = mockk(relaxed = true)
        )
    }

    fun assertVictory(playerMock: PlayerMock) {
        assertEquals(
            expected = Component.text("あなたは勝利した！").color(TextColor.color(0x00FF00)),
            actual = playerMock.nextComponentMessage(),
            message = "勝利のメッセージが違います。"
        )
    }

    fun assertLose(playerMock: PlayerMock) {
        assertEquals(
            expected = Component.text("あなたは敗北してしまった。").color(TextColor.color(0xFF0000)),
            actual = playerMock.nextComponentMessage(),
            message = "敗北のメッセージが違います。"
        )
    }
}