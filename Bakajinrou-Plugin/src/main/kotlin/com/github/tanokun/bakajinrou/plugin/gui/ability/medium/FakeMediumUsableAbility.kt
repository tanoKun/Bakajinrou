package com.github.tanokun.bakajinrou.plugin.gui.ability.medium

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.formatter.getPositionColor
import com.github.tanokun.bakajinrou.plugin.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.plugin.position.fox.FoxThirdPosition
import com.github.tanokun.bakajinrou.plugin.position.wolf.WolfSecondPosition
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.random.Random

/**
 * 偽物の霊媒を提供します。対象の役職にかかわらず、以下からランダムで表示されます。
 * - まだ死亡していない。
 * - 村人
 * - 人狼
 * - 妖狐
 */
class FakeMediumUsableAbility(private val random: Random = Random): MediumUsableAbility() {
    private val candidates = listOf(
        CitizenPosition, WolfSecondPosition, FoxThirdPosition
    )

    override fun useOn(target: Participant, user: Player) {
        val targetName = Bukkit.getPlayer(target.uniqueId)?.name ?: PlayerNameCache.get(target.uniqueId) ?: "unknownPlayer"

        val randomPosition = candidates.random(random)
        val color = getPositionColor(randomPosition.publicPosition)
        val positionPrefix = randomPosition.publicPositionName()

        //25%の確率
        if (random.nextInt(4) == 0) {
            this.showResultNotDead(targetName, user)
            return
        }

        this.showResult(targetName, positionPrefix, color, user)
    }
}