package com.github.tanokun.bakajinrou.plugin.listener

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.method.effect.DamagePotionEffect
import com.github.tanokun.bakajinrou.api.attack.method.item.SwordItem
import com.github.tanokun.bakajinrou.api.attack.method.other.ArrowMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.listener.launching.attack.OnAttackByBowEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.attack.OnAttackByPotionEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.attack.OnAttackBySwordEventListener
import com.github.tanokun.bakajinrou.plugin.method.getGrantedMethodByItemStack
import com.google.common.base.Function
import io.mockk.*
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionType
import org.bukkit.projectiles.ProjectileSource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import java.util.*
import kotlin.test.Test

class OnAttackEventListenerTest {
    private lateinit var server: ServerMock

    private lateinit var plugin: Plugin


    private val attackerMock: Participant = mockk {
        every { removeMethod(any()) } just runs
    }
    private val victimMock: Participant = mockk {
        every { removeMethod(any()) } just runs
    }

    private val attackerPlayerMock: Player = mockk {
        every { uniqueId } returns UUID.randomUUID()
    }
    private val victimPlayerMock: Player = mockk {
        every { uniqueId } returns UUID.randomUUID()
    }

    private val jinrouGameMock: JinrouGame = mockk {
        every { getParticipant(any()) } answers {
            when (it.invocation.args[0]) {
                attackerPlayerMock.uniqueId -> attackerMock
                victimPlayerMock.uniqueId -> victimMock
                else -> null
            }
        }
    }

    private val itemStackMock: ItemStack = mockk()

    private val swordItemMock: SwordItem = mockk {
        every { attack(by = attackerMock, victim = victimMock) } just runs
        every { onConsume(any()) } just runs
    }

    private val arrowMethodMock: ArrowMethod = mockk {
        every { attack(by = attackerMock, victim = victimMock) } just runs
        every { onConsume(any()) } just runs
    }

    private val damagePotionEffectMock: DamagePotionEffect = mockk {
        every { attack(by = attackerMock, victim = victimMock) } just runs
        every { onConsume(any()) } just runs
    }

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.createMockPlugin()

        mockkStatic(Participant::getGrantedMethodByItemStack)

        OnAttackByBowEventListener(plugin, jinrouGameMock).registerAll()
        OnAttackByPotionEventListener(plugin, jinrouGameMock).registerAll()
        OnAttackBySwordEventListener(plugin, jinrouGameMock).registerAll()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    @DisplayName("近接攻撃 (剣): 成功")
    fun successAttackBySwordTest() {
        every { attackerPlayerMock.inventory.itemInMainHand } returns itemStackMock
        every { attackerMock.getGrantedMethodByItemStack(itemStackMock) } returns swordItemMock

        createDamageEvent(attackerPlayerMock, victimPlayerMock, damageType = DamageType.PLAYER_ATTACK).callEvent()

        verify(exactly = 1) { swordItemMock.attack(by = attackerMock, victim = victimMock) }
        verify(exactly = 0) { arrowMethodMock.attack(by = attackerMock, victim = victimMock) }
        verify(exactly = 0) { damagePotionEffectMock.attack(by = attackerMock, victim = victimMock) }
    }

    @Test
    @DisplayName("近接攻撃 (剣以外): 失敗")
    fun failureAttackBySwordTest() {
        every { attackerPlayerMock.inventory.itemInMainHand } returns itemStackMock

        listOf(arrowMethodMock, damagePotionEffectMock).forEach { method ->
            every { attackerMock.getGrantedMethodByItemStack(itemStackMock) } returns method

            createDamageEvent(attackerPlayerMock, victimPlayerMock, damageType = DamageType.PLAYER_ATTACK).callEvent()

            verify(exactly = 0) { swordItemMock.attack(by = attackerMock, victim = victimMock) }
            verify(exactly = 0) { arrowMethodMock.attack(by = attackerMock, victim = victimMock) }
            verify(exactly = 0) { damagePotionEffectMock.attack(by = attackerMock, victim = victimMock) }
        }
    }

    @Test
    @DisplayName("弓矢攻撃 (プレイヤー): 成功")
    fun successAttackByArrowTest() {
        val arrow: Arrow = createArrow(attackerPlayerMock)

        every { attackerMock.getGrantedMethodByItemStack(itemStackMock) } returns arrowMethodMock

        createDamageEvent(arrow, victimPlayerMock, damageType = DamageType.ARROW).callEvent()

        verify(exactly = 0) { swordItemMock.attack(by = attackerMock, victim = victimMock) }
        verify(exactly = 1) { arrowMethodMock.attack(by = attackerMock, victim = victimMock) }
        verify(exactly = 0) { damagePotionEffectMock.attack(by = attackerMock, victim = victimMock) }
    }

    @Test
    @DisplayName("弓矢攻撃 (その他): 失敗")
    fun failureAttackByArrowTest() {
        val arrow: Arrow = createArrow(mockk())

        every { attackerMock.getGrantedMethodByItemStack(itemStackMock) } returns arrowMethodMock

        createDamageEvent(arrow, victimPlayerMock, damageType = DamageType.ARROW).callEvent()

        verify(exactly = 0) { swordItemMock.attack(by = attackerMock, victim = victimMock) }
        verify(exactly = 0) { arrowMethodMock.attack(by = attackerMock, victim = victimMock) }
        verify(exactly = 0) { damagePotionEffectMock.attack(by = attackerMock, victim = victimMock) }
    }

    @Test
    @DisplayName("ダメージポーション攻撃 (プレイヤー): 成功")
    fun successAttackByDamagePotionTest() {
        every { attackerMock.getGrantedMethodByItemStack(itemStackMock) } returns damagePotionEffectMock

        createPotionSplashEvent(attackerPlayerMock, victimPlayerMock).callEvent()

        verify(exactly = 0) { swordItemMock.attack(by = attackerMock, victim = victimMock) }
        verify(exactly = 0) { arrowMethodMock.attack(by = attackerMock, victim = victimMock) }
        verify(exactly = 1) { damagePotionEffectMock.attack(by = attackerMock, victim = victimMock) }
    }

    @Test
    @DisplayName("ダメージポーション攻撃 (その他): 失敗")
    fun failureAttackByDamagePotionTest() {
        every { attackerMock.getGrantedMethodByItemStack(itemStackMock) } returns damagePotionEffectMock

        createPotionSplashEvent(mockk(), victimPlayerMock).callEvent()

        verify(exactly = 0) { swordItemMock.attack(by = attackerMock, victim = victimMock) }
        verify(exactly = 0) { arrowMethodMock.attack(by = attackerMock, victim = victimMock) }
        verify(exactly = 0) { damagePotionEffectMock.attack(by = attackerMock, victim = victimMock) }
    }

    private fun createDamageEvent(attacker: Entity, victim: Entity, damageType: DamageType): EntityDamageByEntityEvent {
        val modifiers = EnumMap<DamageModifier, Double>(DamageModifier::class.java).apply {
            this[DamageModifier.BASE] = 3.0
        }

        val modifierFunctions = EnumMap<DamageModifier, Function<Double, Double>>(DamageModifier::class.java).apply {
            this[DamageModifier.BASE] = Function { 1.0 }
        }

        val damageSource = DamageSource.builder(damageType)
            .withDirectEntity(attacker)
            .build()

        return EntityDamageByEntityEvent(
            attacker,
            victim,
            DamageCause.ENTITY_ATTACK,
            damageSource,
            modifiers,
            modifierFunctions,
            false
        )
    }

    private fun createPotionSplashEvent(shooter: ProjectileSource, victim: Player): PotionSplashEvent {
        val thrownPotion = mockk<ThrownPotion> {
            val potionMeta = mockk<PotionMeta> {
                every { basePotionType } returns PotionType.HARMING
                every { clone() } returns this
            }

            every { this@mockk.potionMeta } returns potionMeta
            every { this@mockk.shooter } returns shooter
            every { this@mockk.item } returns itemStackMock
        }

        return PotionSplashEvent(thrownPotion, null, null, null, hashMapOf(victim to 1.0))
    }

    private fun createArrow(shooter: ProjectileSource) = mockk<Arrow> {
        every { itemStack } returns itemStackMock
        every { this@mockk.shooter } returns shooter
        every { uniqueId } returns UUID.randomUUID()
    }
}