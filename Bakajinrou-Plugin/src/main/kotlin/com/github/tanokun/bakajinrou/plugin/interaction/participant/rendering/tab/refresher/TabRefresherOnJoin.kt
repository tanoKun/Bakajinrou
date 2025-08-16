package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.refresher

import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.modifier.TabListModifier
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class TabRefresherOnJoin(
    plugin: Plugin, tabListModifier: TabListModifier
): LifecycleEventListener(plugin, {
    register<PlayerJoinEvent> { event ->
        val participantId = event.player.uniqueId.asParticipantId()
        tabListModifier.updateDisplayNameOfAll(viewerId = participantId)
        tabListModifier.updateGameModeOfAll(viewerId = participantId)

        tabListModifier.updateDisplayNameToAll(targetId = participantId)
    }
})