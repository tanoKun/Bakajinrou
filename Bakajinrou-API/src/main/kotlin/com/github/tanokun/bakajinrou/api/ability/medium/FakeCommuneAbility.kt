package com.github.tanokun.bakajinrou.api.ability.medium

import com.github.tanokun.bakajinrou.api.ability.CommuneAbility
import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import java.util.*
import kotlin.random.Random

/**
 * 霊媒能力の偽物。
 *
 * 実際の役職に関係なくランダムな結果を返す場合があります。
 */
data class FakeCommuneAbility(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason,
    val random: Random = Random,
): CommuneAbility() {
    override fun commune(target: Participant): CommuneResultSource {
        val randomResult = ResultSource.entries.random(random)

        if (random.nextInt(4) == 0) return CommuneResultSource.NotDeadError

        return CommuneResultSource.FoundResult(randomResult)
    }

    override fun asCrafted(): GrantedMethod = copy(reason = GrantedReason.CRAFTED)
}