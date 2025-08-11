package com.github.tanokun.bakajinrou.api.ability.medium

import com.github.tanokun.bakajinrou.api.ability.CommuneAbility
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import java.util.*

/**
 * 霊媒能力の正規版。
 *
 * 死亡している対象に対して、実際の役職に基づく結果を返します。
 * 対象が生存している場合は [CommuneResultSource.NotDeadError] を返します。
 */
data class CorrectCommuneAbility(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason,
): CommuneAbility() {
    override fun commune(target: Participant): CommuneResultSource {
        if (!target.isDead()) return CommuneResultSource.NotDeadError

        return CommuneResultSource.FoundResult(target.position.abilityResult)
    }
}