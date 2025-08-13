package com.github.tanokun.bakajinrou.api.ability.fortune

import com.github.tanokun.bakajinrou.api.ability.DivineAbility
import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import java.util.*

/**
 * 占い能力の正規版。
 *
 * 対象の実際の役職に基づいて正しい占い結果を返します。
 */
data class CorrectDivineAbility(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason
): DivineAbility() {
    override fun divine(target: Participant): ResultSource = target.position.abilityResult

    override fun asCrafted(): GrantedMethod = copy(reason = GrantedReason.CRAFTED)
}