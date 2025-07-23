package com.github.tanokun.bakajinrou.plugin.listener

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.destroystokyo.paper.event.player.PlayerJumpEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.event.EventPriority
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import kotlin.test.Test

class LifecycleListenerTest {
    private lateinit var server: ServerMock

    private lateinit var plugin: Plugin

    class LifecycleListenerExample(plugin: Plugin, callback1: (PlayerJumpEvent) -> Unit, callback2: (PlayerArmorChangeEvent) -> Unit):
        LifecycleListener(plugin = plugin, listenerDsl = {
            register<PlayerJumpEvent>(eventPriority = EventPriority.HIGH, callback = callback1)
            register<PlayerArmorChangeEvent>(callback = callback2)
    })

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.createMockPlugin()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }


    @Test
    @DisplayName("適切なライフサイクルを持つか")
    fun lifecycleTest() {
        val callback1: (PlayerJumpEvent) -> Unit = mockk(relaxed = true)
        val callback2: (PlayerArmorChangeEvent) -> Unit = mockk(relaxed = true)
        val listener = LifecycleListenerExample(plugin, callback1, callback2)

        val event = mockk<PlayerJumpEvent>(relaxed = true)
        every { event.handlers } returns PlayerJumpEvent.getHandlerList()

        val event2 = mockk<PlayerArmorChangeEvent>(relaxed = true)
        every { event2.handlers } returns PlayerArmorChangeEvent.getHandlerList()

        listener.registerAll()

        server.pluginManager.callEvent(event)
        server.pluginManager.callEvent(event2)
        verify(exactly = 1) { callback1(any()) }
        verify(exactly = 1) { callback2(any()) }

        listener.unregisterAll()

        server.pluginManager.callEvent(event)
        server.pluginManager.callEvent(event2)
        verify(exactly = 1) { callback1(any()) }
        verify(exactly = 1) { callback2(any()) }
    }
}