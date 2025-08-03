package com.github.tanokun.bakajinrou.plugin.participant.ability.medium

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.participant.position.HasPrefix
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * 占い結果を表示します。
 */
object CorrectMediumUsableAbility: MediumUsableAbility() {
    override fun useOn(target: Participant, user: Player) {
        val targetName = Bukkit.getPlayer(target.uniqueId)?.name ?: PlayerNameCache.get(target.uniqueId) ?: "unknownPlayer"

        if (target.state != ParticipantStates.DEAD) {
            this.showResultNotDead(targetName, user)
            return
        }

        val position = target.position as? HasPrefix ?: return

        this.showResult(targetName, position.abilityResult, user)
    }
}