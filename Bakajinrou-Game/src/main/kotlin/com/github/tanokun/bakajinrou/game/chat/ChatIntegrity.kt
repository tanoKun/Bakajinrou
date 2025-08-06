package com.github.tanokun.bakajinrou.game.chat

import com.github.tanokun.bakajinrou.api.participant.Participant

/**
 * 送信者 と 受信者 の状況によって、チャットの送信が担保されることを検証します。
 * - 送信者と受信者が一致 -> 送信する
 * - 送信者が生存状態(非観戦) -> 送信する
 * - 送信者が死亡状態(観戦)、受信者が死亡状態(観戦) -> 送信する
 * - 送信者が死亡状態(観戦)、受信者が生存状態(非観戦) -> 送信しない
 */
object ChatIntegrity {
    /**
     * [sender] と [receiver] の状況が、条件を満たすか確認します。
     *
     * @param sender チャット送信者
     * @param receiver チャット受信者
     *
     * @return 送信する -> true、しない -> false
     */
    fun isSendingAllowed(sender: Participant, receiver: Participant): Boolean {
        if (sender == receiver) return true
        if (!sender.isDead()) return true
        if (receiver.isDead()) return true

        return false
    }
}