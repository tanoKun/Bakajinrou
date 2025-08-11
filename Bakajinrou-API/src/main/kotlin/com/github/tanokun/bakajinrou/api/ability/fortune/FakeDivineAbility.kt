package com.github.tanokun.bakajinrou.api.ability.fortune

import com.github.tanokun.bakajinrou.api.ability.DivineAbility
import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import java.util.*
import kotlin.random.Random

/**
 * 占い能力の偽物。
 *
 * 実際の役職や陣営に関係なく、ランダムな結果を返します。
 * テストや特殊ルールでの利用を想定しています。
 */
data class FakeDivineAbility(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason,
    val random: Random = Random,
): DivineAbility() {
    override fun divine(target: Participant): ResultSource {
        val randomResult = ResultSource.entries.random(random)

        return randomResult
    }
}