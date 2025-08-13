package com.github.tanokun.bakajinrou.plugin.interaction.method.resistance.activate

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.protection.method.ResistanceMethod
import com.github.tanokun.bakajinrou.game.method.resistance.activator.ResistanceActivator
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class UseResistancePotion(
    plugin: Plugin, game: JinrouGame, mainScope: CoroutineScope, resistanceActivator: ResistanceActivator
): LifecycleEventListener(plugin, {
    register<PlayerItemConsumeEvent> { event ->
        val user = game.getParticipant(event.player.uniqueId.asParticipantId()) ?: return@register
        val methodId = event.item.getMethodId() ?: return@register
        val resistanceMethod = user.getGrantedMethod(methodId) as? ResistanceMethod ?: return@register

        mainScope.launch {
            resistanceActivator.activate(resistanceMethod, user.participantId)
        }
    }
})