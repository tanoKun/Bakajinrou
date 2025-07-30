package com.github.tanokun.bakajinrou.plugin.scheduler.schedule

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import org.bukkit.entity.Player

class GameLifecycle(
    private val jinrouGame: JinrouGame,
    private val getBukkitPlayer: (Participant) -> Player?
) {

}