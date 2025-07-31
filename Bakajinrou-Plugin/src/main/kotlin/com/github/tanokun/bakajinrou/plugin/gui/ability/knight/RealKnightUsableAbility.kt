package com.github.tanokun.bakajinrou.plugin.gui.ability.knight

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.method.protective.TotemProtectiveItem
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object RealKnightUsableAbility: KnightUsableAbility() {
    override fun useOn(target: Participant, user: Player) {
        val targetName = Bukkit.getPlayer(target.uniqueId)?.name ?: PlayerNameCache.get(target.uniqueId) ?: "unknownPlayer"

       this.showResult(targetName, user)
       this.grantMethod(target, TotemProtectiveItem())
    }
}