package com.github.tanokun.bakajinrou.plugin.participant.dead.body.name

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.participant.dead.body.BukkitBodyHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class BodyNameVisibilityListener(
    plugin: Plugin,
    game: GameStore,
    bodyHandler: BukkitBodyHandler
) : LifecycleEventListener(plugin, {
    fun refresh(event: PlayerMoveEvent) {
        val participantId = event.player.uniqueId.asParticipantId()
        if (game.getParticipant(participantId) == null) return

        val eyeLocation = event.to.clone().add(0.0, event.player.eyeHeight, 0.0)
        bodyHandler.refreshNameVisibility(event.player, eyeLocation)
    }

    register<PlayerMoveEvent>(eventPriority = EventPriority.MONITOR, ignoreCancelled = true) { event ->
        refresh(event)
    }

    register<PlayerTeleportEvent>(eventPriority = EventPriority.MONITOR, ignoreCancelled = true) { event ->
        refresh(event)
    }
})
