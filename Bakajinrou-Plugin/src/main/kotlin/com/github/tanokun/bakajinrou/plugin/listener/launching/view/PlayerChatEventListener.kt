package com.github.tanokun.bakajinrou.plugin.listener.launching.view

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys
import com.github.tanokun.bakajinrou.game.chat.ChatIntegrity
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.Translator
import org.bukkit.event.EventPriority
import org.bukkit.plugin.Plugin
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.text

class PlayerChatEventListener(
    plugin: Plugin, jinrouGame: JinrouGame, chatIntegrity: ChatIntegrity = ChatIntegrity, translator: Translator
): LifecycleEventListener(plugin, {
    register<AsyncChatEvent>(eventPriority = EventPriority.HIGHEST) { event ->
        val sender = jinrouGame.getParticipant(event.player.uniqueId) ?: return@register

        if (!sender.isDead()) return@register

        event.isCancelled = true

        jinrouGame.getAllParticipants()
            .forEach { receiver ->
                if (!chatIntegrity.isSendingAllowed(sender, receiver)) return@forEach

                BukkitPlayerProvider.get(receiver)?.let {
                    it.sendMessage(component {
                        raw { translator.translate(Component.translatable(TranslationKeys.Prefix.SPECTATOR.key), it.locale()) ?: return@raw Component.text("") }
                        text(" <${event.player.name}> ")
                        raw { event.message() }
                    })
                }
            }
    }
})