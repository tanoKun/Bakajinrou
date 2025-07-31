package com.github.tanokun.bakajinrou.plugin.gui.ability.fortune

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
 * 偽物の占いを提供します。対象の役職にかかわらず、以下の役職からランダムで表示されます。
 * - 村人
 * - 人狼
 * - 妖狐
 */
class FakeFortuneUsableAbility(private val random: Random = Random): FortuneUsableAbility() {
    private val candidates = listOf(
        CitizenPosition, WolfSecondPosition, FoxThirdPosition
    )

    override fun useOn(target: Participant, user: Player) {
        val targetName = Bukkit.getPlayer(target.uniqueId)?.name ?: PlayerNameCache.get(target.uniqueId) ?: "unknownPlayer"

        val randomPosition = candidates.random(random)
        val color = getPositionColor(randomPosition.publicPosition)
        val positionPrefix = randomPosition.publicPositionName()

        this.showResult(targetName, positionPrefix, color, user)
        this.glowingFox(target)
    }
}