package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.distinctUntilChangedByParticipantOf
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body.restriction.DisableHittingBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class ViewBodyOnDeath(
    private val game: JinrouGame,
    private val bodyHandler: BukkitBodyHandler,
    private val playerProvider: BukkitPlayerProvider,
    private val mainScope: CoroutineScope,
    private val disableHittingBody: DisableHittingBody,
): Observer {
    init {
        mainScope.launch {
            game.observeParticipants(mainScope)
                .distinctUntilChangedByParticipantOf(Participant::isDead)
                .map { it.after }
                .collect(::onDeath)
        }
    }

    private fun onDeath(dead: Participant) {
        val player = playerProvider.getAllowNull(dead) ?: return

        disableHittingBody.ghost(player as CraftPlayer)
        bodyHandler.createBody(dead.participantId)
    }
}