package com.github.tanokun.bakajinrou.plugin.participant.ability.fortune

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.participant.ability.AbilityResult
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.random.Random

/**
 * 偽物の占いを提供します。対象の役職にかかわらず、以下の役職からランダムで表示されます。
 * - 市民
 * - 人狼
 * - 妖狐
 */
class FakeFortuneUsableAbility(private val random: Random = Random): FortuneUsableAbility() {
    private val candidates = listOf(
        AbilityResult.Citizens, AbilityResult.Wolf, AbilityResult.Fox
    )

    override fun useOn(target: Participant, user: Player) {
        val targetName = Bukkit.getPlayer(target.uniqueId)?.name ?: PlayerNameCache.get(target.uniqueId) ?: "unknownPlayer"

        val randomResult = candidates.random(random)

        this.showResult(targetName, randomResult, user)
        this.glowingFox(target)
    }
}