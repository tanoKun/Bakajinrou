package com.github.tanokun.bakajinrou.plugin.scheduler.schedule

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.seconds

class TimeAnnouncer(
    private val getBukkitPlayer: (Participant) -> Player?
) {

    /**
     * 残り時間をアクションバーに表示します。
     *
     * 表示フォーマットは `"残り時間: ${minutes}分 ${seconds}秒"` です。
     *
     * @param participants ゲームの全ての参加者
     * @param leftSeconds 残り時間（秒単位）。0以上である必要があります。
     *
     * @throws IllegalArgumentException 残り時間が0未満の場合
     */
     fun showRemainingTimeActionBar(participants: ParticipantScope.All, leftSeconds: Long) {
        if (leftSeconds < 0) throw IllegalArgumentException("残り時間は0以上である必要があります。")

        val formattedTime = leftSeconds.seconds.toComponents { _, minutes, seconds, _ ->
            "残り時間: ${minutes}分 ${seconds}秒"
        }

        participants.forEach {
            val bukkitPlayer = getBukkitPlayer(it) ?: return@forEach

            bukkitPlayer.sendActionBar(
                Component.text(formattedTime)
                    .color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.BOLD)
            )
        }
    }
}