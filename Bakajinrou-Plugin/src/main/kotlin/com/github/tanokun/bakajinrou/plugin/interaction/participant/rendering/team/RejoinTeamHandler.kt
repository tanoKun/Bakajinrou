package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.team

import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.team.modifier.ViewTeamModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * チームの再描画を行います。
 *
 * イベントの関係上、完全な初期化後ではないためパケット関係のズレが起こります。
 * その修正のために、1tick delay しています。
 */
@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class RejoinTeamHandler(
    plugin: Plugin, viewTeamModifier: ViewTeamModifier, mainScope: CoroutineScope
): LifecycleEventListener(plugin, {
    register<PlayerJoinEvent> { event ->
        mainScope.launch {
            delay(50)
            viewTeamModifier.applyModification(event.player.uniqueId.asParticipantId())
        }
    }
})