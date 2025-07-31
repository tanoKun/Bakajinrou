package com.github.tanokun.bakajinrou.plugin.gui.ability.fortune

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.formatter.getPositionColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * 対象の [com.github.tanokun.bakajinrou.api.participant.position.Position.publicPosition] から、
 * 占い結果を表示します。
 */
object CorrectFortuneUsableAbility: FortuneUsableAbility() {
    override fun useOn(target: Participant, user: Player) {
        val targetName = Bukkit.getPlayer(target.uniqueId)?.name ?: PlayerNameCache.get(target.uniqueId) ?: "unknownPlayer"

        val color = getPositionColor(target.position.publicPosition)
        val positionPrefix = target.position.publicPositionName()

        this.showResult(targetName, positionPrefix, color, user)
        this.glowingFox(target)
    }
}