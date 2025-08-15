package com.github.tanokun.bakajinrou.plugin.interaction.game.finished.finisher.each

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys

class WolfSideFinishNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator
): EachSideFinishNotifier(translator) {
    override fun notify(wonInfo: WonInfo) {
        if (wonInfo !is WonInfo.Wolfs) return

        wonInfo.participants.forEach { participant ->
            val bukkitPlayer = playerProvider.getAllowNull(participant) ?: return@forEach

            showVictorySideTitle(
                player = bukkitPlayer,
                text = translator.translate(GameKeys.Finish.Wolf.TITLE, bukkitPlayer.locale())
            )

            if (participant.isPosition<SpectatorPosition>()) return@forEach

            if (participant.isPosition<WolfPosition>() || participant.isPosition<MadmanPosition>()) sendVictoryMessage(bukkitPlayer)
            else sendLoseMessage(bukkitPlayer)

            bukkitPlayer.sendMessage(translator.translate(GameKeys.Finish.Wolf.MESSAGE, bukkitPlayer.locale()))
        }
    }
}