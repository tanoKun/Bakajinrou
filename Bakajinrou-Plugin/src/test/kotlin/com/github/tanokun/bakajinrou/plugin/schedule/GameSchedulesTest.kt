package com.github.tanokun.bakajinrou.plugin.schedule

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.protection.Protection
import com.github.tanokun.bakajinrou.plugin.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.MediumPosition
import com.github.tanokun.bakajinrou.plugin.position.fox.FoxThirdPosition
import com.github.tanokun.bakajinrou.plugin.position.wolf.MadmanSecondPosition
import com.github.tanokun.bakajinrou.plugin.position.wolf.WolfSecondPosition
import com.github.tanokun.bakajinrou.plugin.setting.map.GameSchedules
import io.mockk.mockk
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.potion.PotionEffectType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.entity.PlayerMock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameSchedulesTest {
    private lateinit var server: ServerMock

    private lateinit var wolf: PlayerMock
    private lateinit var madman: PlayerMock
    private lateinit var citizen: PlayerMock
    private lateinit var medium: PlayerMock
    private lateinit var fox: PlayerMock

    private lateinit var players: List<PlayerMock>

    private val serializer = LegacyComponentSerializer.legacySection()

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()

        wolf = server.addPlayer()
        madman = server.addPlayer()
        citizen = server.addPlayer()
        medium = server.addPlayer()
        fox = server.addPlayer()

        players = listOf(wolf, madman, citizen, medium, fox)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    @DisplayName("アクションバーに表示される時間のテスト")
    fun showLeftTimeToActionbarTest() {
        val gameSchedules = GameSchedules(jinrouGame = createJinrouGame())
        gameSchedules.showLeftTime(231)

        players.forEach { player ->
            val expected = Component.text("残り時間: 3分 51秒")
                .color(NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD)

            val lastSendActionbar = player.nextActionBar()

            assertEquals(serializer.serialize(expected), serializer.serialize(lastSendActionbar))
        }

        gameSchedules.showLeftTime(47)

        players.forEach { player ->
            val expected = Component.text("残り時間: 0分 47秒")
                .color(NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD)

            val lastSendActionbar = player.nextActionBar()

            assertEquals(serializer.serialize(expected), serializer.serialize(lastSendActionbar))
        }
    }

    @Test
    @DisplayName("妖狐、人狼は発光せず、村陣営と狂人のみ発光")
    fun doNotGrowWolfsAndFoxButCitizensAndMadman() {
        val gameSchedules = GameSchedules(jinrouGame = createJinrouGame())
        gameSchedules.growCitizens()

        assertFalse(wolf.hasPotionEffect(PotionEffectType.GLOWING), "人狼は発光を持たない")
        assertTrue(madman.hasPotionEffect(PotionEffectType.GLOWING), "狂人は発光を持つ")
        assertTrue(citizen.hasPotionEffect(PotionEffectType.GLOWING), "村人は発光を持つ")
        assertTrue(medium.hasPotionEffect(PotionEffectType.GLOWING), "霊媒師は発光を持つ")
        assertFalse(fox.hasPotionEffect(PotionEffectType.GLOWING), "妖狐は発光を持たない")

    }

    fun createJinrouGame(): JinrouGame {
        val wolf = Participant(wolf.uniqueId, WolfSecondPosition, mockk<Protection>())
        val madman = Participant(madman.uniqueId, MadmanSecondPosition, mockk<Protection>())
        val citizen = Participant(citizen.uniqueId, CitizenPosition, mockk<Protection>())
        val medium = Participant(medium.uniqueId, MediumPosition, mockk<Protection>())
        val fox = Participant(fox.uniqueId, FoxThirdPosition, mockk<Protection>())

        return JinrouGame(
            participants = listOf(wolf, madman, citizen, medium, fox),
            citizenSideFinisher = { mockk() },
            wolfSideFinisher = { mockk() },
            foxSideFinisher = { mockk() }
        )
    }
}