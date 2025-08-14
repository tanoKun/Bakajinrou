package com.github.tanokun.bakajinrou.plugin.interaction.game.finished.notifier.each

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys

class FoxSideFinishNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator
): EachSideFinishNotifier(translator) {
    override fun notify(wonInfo: WonInfo) {
        if (wonInfo !is WonInfo.Fox) return

        wonInfo.participants.forEach { participant ->
            val bukkitPlayer = playerProvider.getAllowNull(participant) ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = translator.translate(GameKeys.Finish.Fox.TITLE, bukkitPlayer.locale())
            )

            if (participant.isPosition<SpectatorPosition>()) return@forEach

            if (participant.isPosition<FoxPosition>()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)

            bukkitPlayer.sendMessage(translator.translate(GameKeys.Finish.Fox.MESSAGE, bukkitPlayer.locale()))
        }
    }
}