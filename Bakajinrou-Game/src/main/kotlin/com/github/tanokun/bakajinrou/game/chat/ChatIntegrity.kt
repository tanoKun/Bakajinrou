package com.github.tanokun.bakajinrou.game.chat

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition

/**
 * 送信者 と 受信者 の状況によって、チャットを秘匿します。条件は以下の通りです。
 * - 送信者が死亡状態(観戦)、受信者が生存状態(非観戦)
 */
class ChatIntegrity {
    /**
     * [sender] と [receiver] の状態を比較して、送信すべきか判定します。
     *
     * @param sender チャット送信者
     * @param receiver チャット受信者
     */
    fun verify(sender: Participant, receiver: Participant): Boolean {
        if (!sender.isPosition<SpectatorPosition>() && sender.state != ParticipantStates.DEAD) return false
        if (receiver.state != ParticipantStates.SURVIVED) return false

        return true
    }
}