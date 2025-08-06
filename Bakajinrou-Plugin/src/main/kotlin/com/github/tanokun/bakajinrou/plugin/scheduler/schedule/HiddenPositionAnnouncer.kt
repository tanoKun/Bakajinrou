/*
package com.github.tanokun.bakajinrou.plugin.scheduler.schedule

import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.formatter.ParticipantsFormatter
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider

class HiddenPositionAnnouncer(
    private val playerProvider: BukkitPlayerProvider
) {

    */
/**
     * 以下の役職の付与者一覧を、全ての参加者に表示します。
     * - 人狼
     * - 妖狐
     *
     * @param participants ゲームの全ての参加者
     *
     * @see com.github.tanokun.bakajinrou.plugin.formatter.ParticipantsFormatter
     *//*

     fun notifyWolfsAndFox(participants: ParticipantScope.All) {
        val formatter = ParticipantsFormatter(participants.excludeSpectators()) { BukkitPlayerProvider.get(it) }

        participants.forEach {
            val bukkitPlayer = playerProvider.get(it) ?: return@forEach

            bukkitPlayer.sendMessage(formatter.formatWolf())
            bukkitPlayer.sendMessage(formatter.formatFox())
        }
    }
}*/
