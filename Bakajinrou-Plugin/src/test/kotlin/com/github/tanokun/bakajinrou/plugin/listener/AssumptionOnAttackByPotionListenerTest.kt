package com.github.tanokun.bakajinrou.plugin.listener

import com.github.tanokun.bakajinrou.api.participant.protection.ProtectionResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock

class AssumptionOnAttackByPotionListenerTest: AssumptionOnAttackTest() {
    private lateinit var server: ServerMock

    private lateinit var plugin: Plugin

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
    @DisplayName("即時ダメージポーション -> 無し: 成功")
    fun successAttackByPotion() {
        val shooter = server.addPlayer().apply {
            inventory.setItemInMainHand(ItemStack.of(Material.DIAMOND_SWORD))
        }
        val victim = server.addPlayer()

        val controller = createController(shooter, victim, ProtectionResult.NONE)
        OnAttackListener(plugin, controller).apply { registerAll() }

        createPotionSplashEvent(shooter, victim).callEvent()

        verify(exactly = 1) { wolfSideFinisherMock.notifyFinish() }
    }

    @Test
    @DisplayName("即時ダメージポーション -> トーテム: 防御")
    fun protectedByTotemAttackByPotion() {
        val shooter = server.addPlayer()
        val victim = server.addPlayer().apply { playerWorldMock() }

        val controller = createController(shooter, victim, ProtectionResult.TOTEM)
        OnAttackListener(plugin, controller).apply { registerAll() }

        createPotionSplashEvent(shooter, victim).callEvent()

        verify(exactly = 0) { wolfSideFinisherMock.notifyFinish() }
    }

    @Test
    @DisplayName("即時ダメージポーション -> 盾: 成功")
    fun protectedByShieldAttackByPotion() {
        val shooter = server.addPlayer()
        val victim = server.addPlayer()

        val controller = createController(shooter, victim, ProtectionResult.SHIELD)
        OnAttackListener(plugin, controller).apply { registerAll() }

        createPotionSplashEvent(shooter, victim).callEvent()

        verify(exactly = 1) { wolfSideFinisherMock.notifyFinish() }
    }

    @Test
    @DisplayName("即時ダメージポーション -> 耐性効果(Effect): 防御")
    fun protectedByResistanceAttackByPotion() {
        val shooter = server.addPlayer()
        val victim = server.addPlayer()

        val controller = createController(shooter, victim, ProtectionResult.POTION_RESISTANCE)
        OnAttackListener(plugin, controller).apply { registerAll() }

        createPotionSplashEvent(shooter, victim).callEvent()

        verify(exactly = 0) { wolfSideFinisherMock.notifyFinish() }
    }

    private fun createPotionSplashEvent(shooter: Player, victim: Player): PotionSplashEvent {
        val thrownPotion = mockk<ThrownPotion>().apply {
            val potionMeta = mockk<PotionMeta>().apply {
                every { basePotionType } returns PotionType.HARMING
                every { clone() } returns this
            }

            every { this@apply.potionMeta } returns potionMeta
            every { this@apply.shooter } returns shooter
        }

        return PotionSplashEvent(thrownPotion, null, null, null, hashMapOf(victim to 1.0))
    }
}