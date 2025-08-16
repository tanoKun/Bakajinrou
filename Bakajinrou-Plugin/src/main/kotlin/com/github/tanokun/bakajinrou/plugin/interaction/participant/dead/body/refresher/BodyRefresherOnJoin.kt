package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body.refresher

import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body.BukkitBodyHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class BodyRefresherOnJoin(
    plugin: Plugin, bodyHandler: BukkitBodyHandler
): LifecycleEventListener(plugin, {
    register<PlayerJoinEvent> { event ->
        bodyHandler.showBodies(event.player.uniqueId.asParticipantId())
    }
})