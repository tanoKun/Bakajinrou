package com.github.tanokun.bakajinrou.plugin.scheduler.schedule

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.formatter.ParticipantsFormatter
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class HiddenPositionAnnouncer(
    private val jinrouGame: JinrouGame,
    private val getBukkitPlayer: (Participant) -> Player?,
    private val playerNameCache: PlayerNameCache
) {
    /**
     * 以下の役職の付与者一覧を、全ての参加者に表示します。
     * - 人狼
     * - 妖狐
     *
     * @see com.github.tanokun.bakajinrou.plugin.formatter.ParticipantsFormatter
     */
    fun notifyWolfsAndFox() {
        val formatter = ParticipantsFormatter(jinrouGame.participants, playerNameCache) { Bukkit.getPlayer(it.uniqueId) }

        jinrouGame.participants.forEach {
            val bukkitPlayer = getBukkitPlayer(it) ?: return@forEach

            bukkitPlayer.sendMessage(formatter.formatWolf())
            bukkitPlayer.sendMessage(formatter.formatFox())
        }
    }
}