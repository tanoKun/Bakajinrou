package com.github.tanokun.bakajinrou.plugin.interaction.participant.chat.listen

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.translate.PrefixKeys
import com.github.tanokun.bakajinrou.game.chat.ChatIntegrity
import com.github.tanokun.bakajinrou.plugin.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventPriority
import org.bukkit.plugin.Plugin
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.text

class ParticipantChatEventListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
    chatIntegrity: ChatIntegrity,
    translator: JinrouTranslator,
    playerProvider: BukkitPlayerProvider
): LifecycleEventListener(plugin, {
    register<AsyncChatEvent>(eventPriority = EventPriority.HIGHEST) { event ->
        val sender = jinrouGame.getParticipant(event.player.uniqueId.asParticipantId()) ?: return@register

        if (!sender.isDead()) return@register

        event.isCancelled = true

        jinrouGame.getCurrentParticipants()
            .forEach { receiver ->
                if (!chatIntegrity.isSendingAllowed(sender, receiver)) return@forEach

                playerProvider.getAllowNull(receiver)?.let {
                    it.sendMessage(component {
                        raw { translator.translate(PrefixKeys.Companion.SPECTATOR, it.locale()) }
                        text(" <${event.player.name}> ")
                        raw { event.message() }
                    })
                }
            }
    }
})