package com.github.tanokun.bakajinrou.plugin.system.game.finished.notifier.each

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys

class CitizenSideFinishNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator
): EachSideFinishNotifier(translator) {
    override fun notify(wonInfo: WonInfo) {
        if (wonInfo !is WonInfo.Citizens) return

        wonInfo.participants.forEach { participant ->
            val bukkitPlayer = playerProvider.getAllowNull(participant) ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = translator.translate(GameKeys.Finish.Citizen.TITLE, bukkitPlayer.locale())
            )

            if (participant.isPosition<SpectatorPosition>()) return@forEach

            if (participant.isPosition<CitizensPosition>()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)

            bukkitPlayer.sendMessage(translator.translate(GameKeys.Finish.Citizen.MESSAGE, bukkitPlayer.locale()))
        }
    }
}