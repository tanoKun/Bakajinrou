package com.github.tanokun.bakajinrou.plugin.scheduler.schedule

import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import kotlin.time.Duration.Companion.seconds

class TimeAnnouncer(
    private val playerProvider: BukkitPlayerProvider
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
            val bukkitPlayer = playerProvider.get(it) ?: return@forEach

            bukkitPlayer.sendActionBar(
                Component.text(formattedTime)
                    .color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.BOLD)
            )
        }
    }
}