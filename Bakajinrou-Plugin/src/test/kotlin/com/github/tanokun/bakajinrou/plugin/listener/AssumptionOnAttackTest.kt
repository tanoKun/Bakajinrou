package com.github.tanokun.bakajinrou.plugin.listener

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.api.participant.protection.Protection
import com.github.tanokun.bakajinrou.api.participant.protection.ProtectionResult
import com.github.tanokun.bakajinrou.bukkit.controller.JinrouGameController
import com.github.tanokun.bakajinrou.plugin.finisher.CitizenSideFinisher
import com.github.tanokun.bakajinrou.plugin.finisher.FoxSideFinisher
import com.github.tanokun.bakajinrou.plugin.finisher.WolfSideFinisher
import com.google.common.base.Function
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Location
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier
import org.mockbukkit.mockbukkit.entity.PlayerMock
import org.mockbukkit.mockbukkit.world.WorldMock
import java.util.*

abstract class AssumptionOnAttackTest {

    protected val citizenSideFinisherMock: CitizenSideFinisher = mockk()
    protected val wolfSideFinisherMock: WolfSideFinisher = mockk()
    protected val foxSideFinisherMock: FoxSideFinisher = mockk()

    init {
        every { citizenSideFinisherMock.notifyFinish() } returns Unit
        every { wolfSideFinisherMock.notifyFinish() } returns Unit
        every { foxSideFinisherMock.notifyFinish() } returns Unit
    }

    protected fun createController(attacker: PlayerMock, victim: PlayerMock, result: ProtectionResult): JinrouGameController {
        val participants = listOf(
            Participant(attacker.uniqueId, mockk<WolfPosition>(), protectionMock(result)),
            Participant(victim.uniqueId, mockk<CitizensPosition>(), protectionMock(result)),
        )

        return JinrouGameController(
            game = JinrouGame(participants, { citizenSideFinisherMock }, { wolfSideFinisherMock }, { foxSideFinisherMock }),
            logger = mockk(relaxed = true),
            scheduler = mockk(relaxed = true),
            bodyHandler  = mockk(relaxed = true),
        )
    }


    protected fun protectionMock(result: ProtectionResult): Protection {
        val protection = mockk<Protection>()
        every { protection.hasProtection() } returns result

        return protection
    }

    fun createDamageEvent(attacker: Entity, victim: Entity, damageType: DamageType): EntityDamageByEntityEvent {
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

    protected fun PlayerMock.playerWorldMock() {
        val worldMock = mockk<WorldMock>(relaxed = true)

        this.location = Location(worldMock, 0.0, 0.0, 0.0)
    }
}