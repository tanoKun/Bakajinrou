package com.github.tanokun.bakajinrou.plugin.listener

import com.github.tanokun.bakajinrou.api.participant.protection.ProtectionResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.damage.DamageType
import org.bukkit.entity.Arrow
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.projectiles.ProjectileSource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import kotlin.test.assertTrue

class AssumptionOnAttackByBowListenerTest: AssumptionOnAttackTest() {
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
    @DisplayName("弓 (矢) -> 無し: 成功")
    fun successAttackByBow() {
        val shooter = server.addPlayer()
        val arrow = createArrowMock(shooter)
        val victim = server.addPlayer()

        val controller = createController(shooter, victim, ProtectionResult.NONE)
        OnAttackEventListener(plugin, controller).apply { registerAll() }

        createDamageEvent(arrow, victim, DamageType.ARROW).callEvent()

        verify(exactly = 1) { wolfSideFinisherMock.notifyFinish() }
    }

    @Test
    @DisplayName("弓 (矢) -> トーテム: 防御")
    fun protectedByTotemAttackByBow() {
        val shooter = server.addPlayer()
        val arrow = createArrowMock(shooter)
        val victim = server.addPlayer().apply {
            inventory.setItemInMainHand(ItemStack.of(Material.TOTEM_OF_UNDYING))
            playerWorldMock()
        }

        val controller = createController(shooter, victim, ProtectionResult.TOTEM)
        OnAttackEventListener(plugin, controller).apply { registerAll() }

        createDamageEvent(arrow, victim, DamageType.ARROW).callEvent()

        verify(exactly = 0) { wolfSideFinisherMock.notifyFinish() }
        assertTrue("トーテムがなくなっているはず") { victim.inventory.itemInMainHand == ItemStack.of(Material.AIR) }
    }

    @Test
    @DisplayName("弓 (矢) -> 盾: 防御")
    fun protectedByShieldAttackByBow() {
        val shooter = server.addPlayer() 
        val arrow = createArrowMock(shooter)
        val victim = server.addPlayer().apply {
            inventory.setItemInOffHand(ItemStack.of(Material.SHIELD))
            playerWorldMock()
        }

        val controller = createController(shooter, victim, ProtectionResult.SHIELD)
        OnAttackEventListener(plugin, controller).apply { registerAll() }

        createDamageEvent(arrow, victim, DamageType.ARROW).callEvent()

        verify(exactly = 0) { wolfSideFinisherMock.notifyFinish() }
        assertTrue("盾がなくなっているはず") { shooter.inventory.itemInOffHand == ItemStack.of(Material.AIR) }
    }

    @Test
    @DisplayName("弓 (矢) -> 耐性効果(Effect): 防御")
    fun protectedByResistanceAttackByBow() {
        val shooter = server.addPlayer()
        val arrow = createArrowMock(shooter)
        val victim = server.addPlayer().apply {
            addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 100, 100))
            playerWorldMock()
        }

        val controller = createController(shooter, victim, ProtectionResult.POTION_RESISTANCE)
        OnAttackEventListener(plugin, controller).apply { registerAll() }

        createDamageEvent(arrow, victim, DamageType.ARROW).callEvent()

        verify(exactly = 0) { wolfSideFinisherMock.notifyFinish() }
        assertTrue("耐性がなくなっているはず") { !victim.hasPotionEffect(PotionEffectType.RESISTANCE) }
    }

    private fun createArrowMock(shooter: ProjectileSource) = mockk<Arrow>().apply {
        every { this@apply.shooter } returns shooter
    }
}