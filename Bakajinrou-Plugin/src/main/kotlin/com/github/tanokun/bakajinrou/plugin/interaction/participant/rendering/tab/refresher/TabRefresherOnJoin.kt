package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.refresher

import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.modifier.TabListModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * Tabの再描画を行います。
 *
 * イベントの関係上、完全な初期化後ではないためパケット関係のズレが起こります。
 * その修正のために、1tick delay しています。
 */
@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class TabRefresherOnJoin(
    plugin: Plugin, tabListModifier: TabListModifier, mainScope: CoroutineScope
): LifecycleEventListener(plugin, {
    register<PlayerJoinEvent> { event ->
        mainScope.launch {
            delay(50)
            val participantId = event.player.uniqueId.asParticipantId()
            tabListModifier.updateDisplayNameOfAll(viewerId = participantId)
            tabListModifier.updateGameModeOfAll(viewerId = participantId)

            tabListModifier.updateDisplayNameToAll(targetId = participantId)
        }
    }
})