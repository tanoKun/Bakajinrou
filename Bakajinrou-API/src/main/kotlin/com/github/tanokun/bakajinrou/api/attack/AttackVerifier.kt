package com.github.tanokun.bakajinrou.api.attack

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.protection.ProtectionResult

sealed interface AttackVerifier {
    fun verify(to: Participant): AttackResult

    object Sword: AttackVerifier {
        override fun verify(to: Participant): AttackResult {
            val protection: ProtectionResult = to.hasProtection()

            return when (protection) {
                ProtectionResult.TOTEM -> AttackResult.PROTECTED_BY_TOTEM
                ProtectionResult.POTION_RESISTANCE -> AttackResult.PROTECTED_BY_POTION_RESISTANCE
                ProtectionResult.SHIELD -> AttackResult.SUCCESS_ATTACK
                ProtectionResult.NONE -> AttackResult.SUCCESS_ATTACK
            }
        }
    }

    object Bow: AttackVerifier {
        override fun verify(to: Participant): AttackResult {
            val protection: ProtectionResult = to.hasProtection()

            return when (protection) {
                ProtectionResult.TOTEM -> AttackResult.PROTECTED_BY_TOTEM
                ProtectionResult.POTION_RESISTANCE -> AttackResult.PROTECTED_BY_POTION_RESISTANCE
                ProtectionResult.SHIELD -> AttackResult.PROTECTED_BY_SHIELD
                ProtectionResult.NONE -> AttackResult.SUCCESS_ATTACK
            }
        }
    }

    object Potion: AttackVerifier {
        override fun verify(to: Participant): AttackResult {
            val protection: ProtectionResult = to.hasProtection()

            return when (protection) {
                ProtectionResult.TOTEM -> AttackResult.PROTECTED_BY_TOTEM
                ProtectionResult.POTION_RESISTANCE -> AttackResult.PROTECTED_BY_POTION_RESISTANCE
                ProtectionResult.SHIELD -> AttackResult.SUCCESS_ATTACK
                ProtectionResult.NONE -> AttackResult.SUCCESS_ATTACK
            }
        }
    }
}