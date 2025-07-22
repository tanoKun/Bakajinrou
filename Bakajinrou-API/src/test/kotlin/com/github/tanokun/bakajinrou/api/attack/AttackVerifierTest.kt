package com.github.tanokun.bakajinrou.api.attack

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.protection.ProtectionResult
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertEquals

class AttackVerifierTest {
    @ParameterizedTest
    @EnumSource(ProtectionResult::class)
    @DisplayName("剣での攻撃")
    fun testVerifyAttackingBySword(protection: ProtectionResult) {
        val participant = mockk<Participant>()
        every { participant.hasProtection() } returns protection

        val result = AttackVerifier.Sword.verify(to = participant)

        val expected = when (protection) {
            ProtectionResult.TOTEM -> AttackResult.PROTECTED_BY_TOTEM
            ProtectionResult.POTION_RESISTANCE -> AttackResult.PROTECTED_BY_POTION_RESISTANCE
            ProtectionResult.SHIELD -> AttackResult.SUCCESS_ATTACK
            ProtectionResult.NONE -> AttackResult.SUCCESS_ATTACK
        }

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @EnumSource(ProtectionResult::class)
    @DisplayName("弓での攻撃")
    fun testVerifyAttackingByBow(protection: ProtectionResult) {
        val participant = mockk<Participant>()
        every { participant.hasProtection() } returns protection

        val result = AttackVerifier.Bow.verify(to = participant)

        val expected = when (protection) {
            ProtectionResult.TOTEM -> AttackResult.PROTECTED_BY_TOTEM
            ProtectionResult.POTION_RESISTANCE -> AttackResult.PROTECTED_BY_POTION_RESISTANCE
            ProtectionResult.SHIELD -> AttackResult.PROTECTED_BY_SHIELD
            ProtectionResult.NONE -> AttackResult.SUCCESS_ATTACK
        }

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @EnumSource(ProtectionResult::class)
    @DisplayName("ポーションでの攻撃")
    fun testVerifyAttackingByPotion(protection: ProtectionResult) {
        val participant = mockk<Participant>()
        every { participant.hasProtection() } returns protection

        val result = AttackVerifier.Potion.verify(to = participant)

        val expected = when (protection) {
            ProtectionResult.TOTEM -> AttackResult.PROTECTED_BY_TOTEM
            ProtectionResult.POTION_RESISTANCE -> AttackResult.PROTECTED_BY_POTION_RESISTANCE
            ProtectionResult.SHIELD -> AttackResult.SUCCESS_ATTACK
            ProtectionResult.NONE -> AttackResult.SUCCESS_ATTACK
        }

        assertEquals(expected, result)
    }
}