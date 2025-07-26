package com.github.tanokun.bakajinrou.plugin.controller

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.protection.Protection
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
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

    private lateinit var wolfPlayerMock: PlayerMock
    private lateinit var citizenPlayerMock: PlayerMock
    private lateinit var mediumPlayerMock: PlayerMock
    private lateinit var foxPlayerMock: PlayerMock

    private val wolf by lazy { Participant(wolfPlayerMock.uniqueId, WolfSecondPosition, mockk<Protection>()) }
    private val citizen by lazy { Participant(citizenPlayerMock.uniqueId, CitizenPosition, mockk<Protection>()) }
    private val medium by lazy { Participant(mediumPlayerMock.uniqueId, MediumPosition, mockk<Protection>()) }
    private val fox by lazy { Participant(foxPlayerMock.uniqueId, FoxThirdPosition, mockk<Protection>()) }

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()

        wolfPlayerMock = server.addPlayer()
        citizenPlayerMock = server.addPlayer()
        mediumPlayerMock = server.addPlayer()
        foxPlayerMock = server.addPlayer()
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

        assertLose(wolfPlayerMock)
        assertVictory(citizenPlayerMock)
        assertVictory(mediumPlayerMock)
        assertLose(foxPlayerMock)
    }

    @Test
    @DisplayName("最後の市民を殺害でのゲーム終了。(妖狐無し)")
    fun finishGameByKillingLastCitizenWithoutFoxTest() {
        val gameController = createGame()

        gameController.killed(victim = fox.uniqueId, by = wolf.uniqueId)
        gameController.killed(victim = citizen.uniqueId, by = wolf.uniqueId)
        gameController.killed(victim = medium.uniqueId, by = wolf.uniqueId)

        assertVictory(wolfPlayerMock)
        assertLose(citizenPlayerMock)
        assertLose(mediumPlayerMock)
        assertLose(foxPlayerMock)
    }

    @Test
    @DisplayName("最後の人狼を殺害でのゲーム終了。(妖狐有り)")
    fun finishGameByKillingLastCitizenWithFoxTest() {
        val gameController = createGame()

        gameController.killed(victim = wolf.uniqueId, by = fox.uniqueId)

        assertLose(wolfPlayerMock)
        assertLose(citizenPlayerMock)
        assertLose(mediumPlayerMock)
        assertVictory(foxPlayerMock)
    }

    fun createGame(): JinrouGameController {
        val game = JinrouGame(
            participants = listOf(wolf, citizen, medium, fox),
            citizenSideFinisher = { CitizenSideFinisher(it) },
            wolfSideFinisher = { WolfSideFinisher(it) },
            foxSideFinisher = { FoxSideFinisher(it) }
        )

        return JinrouGameController(
            game = game,
            logger = mockk(relaxed = true),
            scheduler = mockk(relaxed = true),
            bodyHandler = mockk(relaxed = true),
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