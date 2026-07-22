package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.position.isCitizens
import com.github.tanokun.bakajinrou.api.participant.position.isFox
import com.github.tanokun.bakajinrou.api.participant.position.isWolf

/**
 * 人狼ゲームのドメイン状態と、その状態に対するルールを表します。
 *
 * 状態更新は新しい [JinrouGame] を返し、排他制御や変更通知は Game 層に委ねます。
 */
class JinrouGame(
    val participants: ParticipantScope.All,
) {
    fun updateParticipant(
        participantId: ParticipantId,
        transform: (current: Participant) -> Participant,
    ): JinrouGame {
        val current = getParticipant(participantId)
            ?: throw IllegalArgumentException("存在しない参加者です。")
        val updated = transform(current)

        if (updated.participantId != participantId) {
            throw IllegalArgumentException("異なる参加者は編集できません。")
        }

        return JinrouGame(
            participants
                .filterNot { it.participantId == participantId }
                .plus(updated)
                .all()
        )
    }

    fun addParticipant(participant: Participant): JinrouGame {
        if (existParticipant(participant.participantId)) {
            throw IllegalArgumentException("既に存在する参加者は追加できません。")
        }

        return JinrouGame((participants + participant).all())
    }

    /** 現在の参加者の状態に基づいてゲームの勝敗を判定します。 */
    fun judge(): WonInfo? {
        val survivors = participants.survivedOnly()
        val citizens = survivors.includes(::isCitizens)
        val wolfs = survivors.includes(::isWolf)
        val fox = survivors.includes(::isFox)

        if (wolfs.isEmpty() && fox.isEmpty()) return WonInfo.Citizens(participants)
        if (citizens.isEmpty() && fox.isEmpty()) return WonInfo.Wolfs(participants)
        if (wolfs.isEmpty() || citizens.isEmpty()) return WonInfo.Fox(participants)

        return null
    }

    fun getCurrentParticipants(): ParticipantScope.All = participants

    fun getParticipant(participantId: ParticipantId): Participant? =
        participants.firstOrNull { it.participantId == participantId }

    fun existParticipant(participantId: ParticipantId): Boolean =
        getParticipant(participantId) != null
}
