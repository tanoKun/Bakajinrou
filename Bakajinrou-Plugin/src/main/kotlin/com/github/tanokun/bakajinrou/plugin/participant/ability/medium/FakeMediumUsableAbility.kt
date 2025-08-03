package com.github.tanokun.bakajinrou.plugin.participant.ability.medium

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.participant.ability.AbilityResult
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.random.Random

/**
 * 偽物の霊媒を提供します。対象の役職にかかわらず、以下からランダムで表示されます。
 * - まだ死亡していない。
 * - 市民
 * - 人狼
 * - 妖狐
 */
class FakeMediumUsableAbility(private val random: Random = Random): MediumUsableAbility() {
    private val candidates = listOf(
        AbilityResult.Citizens, AbilityResult.Wolf, AbilityResult.Fox
    )

    override fun useOn(target: Participant, user: Player) {
        val targetName = Bukkit.getPlayer(target.uniqueId)?.name ?: PlayerNameCache.get(target.uniqueId) ?: "unknownPlayer"

        val randomResult = candidates.random(random)

        //25%の確率
        if (random.nextInt(4) == 0) {
            this.showResultNotDead(targetName, user)
            return
        }

        this.showResult(targetName, randomResult, user)
    }
}