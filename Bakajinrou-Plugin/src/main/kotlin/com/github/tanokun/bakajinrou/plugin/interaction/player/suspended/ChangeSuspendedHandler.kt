package com.github.tanokun.bakajinrou.plugin.interaction.player.suspended

import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.participant.state.suspended.ChangeSuspended
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class ChangeSuspendedHandler(
    changeSuspended: ChangeSuspended, plugin: Plugin, scope: CoroutineScope
): LifecycleEventListener(plugin, {
    register<PlayerJoinEvent> { event ->
        event.joinMessage(null)

        val participantId = event.player.uniqueId.asParticipantId()
        scope.launch {
            changeSuspended.changeToAlive(participantId)
        }
    }

    register<PlayerQuitEvent> { event ->
        event.quitMessage(null)

        val participantId = event.player.uniqueId.asParticipantId()
        scope.launch {
            changeSuspended.changeToSuspended(participantId)
        }
    }
})