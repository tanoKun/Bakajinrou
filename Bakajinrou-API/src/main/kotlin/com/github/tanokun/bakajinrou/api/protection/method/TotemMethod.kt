package com.github.tanokun.bakajinrou.api.protection.method

import com.github.tanokun.bakajinrou.api.attacking.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.protection.ActivationPriority
import com.github.tanokun.bakajinrou.api.protection.ProtectResult
import com.github.tanokun.bakajinrou.api.protection.ProtectVerificator
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import java.util.*

/**
 * トーテムを表します。
 * 全ての攻撃を防御出来ます。
 */
data class TotemMethod(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason,
    private val verificator: ProtectVerificator
): ProtectiveMethod() {
    override val priority: ActivationPriority = ActivationPriority.LOW

    override val assetKey: MethodAssetKeys.Protective = MethodAssetKeys.Protective.TOTEM

    override val isValid: Boolean
        get() = verificator.isValid()

    override fun verifyProtect(method: AttackMethod): ProtectResult = ProtectResult.PROTECTED

    override fun asTransferred(participantId: ParticipantId): ProtectiveMethod =
        copy(reason = GrantedReason.TRANSFERRED, verificator = verificator.copy(participantId = participantId))
}