package com.github.tanokun.bakajinrou.plugin.command

import com.github.tanokun.bakajinrou.plugin.BakaJinrou
import dev.jorel.commandapi.CommandAPITestUtilities.assertCommandSucceeds
import dev.jorel.commandapi.MockCommandAPIPlugin
import org.bukkit.plugin.PluginDescriptionFile
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock


class CommandTest {
    private lateinit var server: ServerMock

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()

        MockBukkit.load<MockCommandAPIPlugin>(MockCommandAPIPlugin::class.java)

        MockBukkit.loadWith(BakaJinrou::class.java, PluginDescriptionFile(
            "Bakajinrou",
            "Test",
            "com.github.tanokun.bakajinrou.plugin.BakaJinrou"
        ))
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun runCommand() {
        val player = server.addPlayer()

        assertCommandSucceeds(player, "ping")

        println(player.nextMessage())
    }
}