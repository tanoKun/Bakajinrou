package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import java.util.*

class JinrouGame(
    private val participants: List<Participant>,
    val citizenSideFinisher: (JinrouGame) -> GameFinisher,
    val wolfSideFinisher: (JinrouGame) -> GameFinisher,
    val foxSideFinisher: (JinrouGame) -> GameFinisher
) {

    init {
        if (participants.distinctBy { it.uniqueId }.count() != participants.count())
            throw IllegalArgumentException("重複したプレイヤーが存在します。")
    }

    /**
     * 現在の参加者の状態に基づいてゲームの勝敗を判定します。
     *
     * 勝利条件:
     * - 市民側の勝利: 生存者に人狼も妖狐も存在しない場合
     * - 人狼側の勝利: 生存者に市民も妖狐も存在しない場合
     * - 妖狐側の勝利: 生存者に人狼または市民のどちらかが存在しない場合
     *
     * いずれの条件も満たさない場合はゲーム継続とみなし、nullを返します。
     *
     * @return ゲームの勝利を決定する [GameFinisher] オブジェクト。勝敗未確定の場合は null。
     */
    fun judge(): GameFinisher? {

        val survivors = participants.filter { it.state == ParticipantStates.SURVIVED }
        val citizens = survivors.map { it.position }.filterIsInstance<CitizensPosition>()
        val wolfs = survivors.map { it.position }.filterIsInstance<WolfPosition>()
        val fox = survivors.map { it.position }.filterIsInstance<FoxPosition>()

        //市民勝利
        if (wolfs.isEmpty() && fox.isEmpty())
            return citizenSideFinisher(this)

        //人狼勝利
        if (citizens.isEmpty() && fox.isEmpty())
            return wolfSideFinisher(this)

        //妖狐勝利
        if (wolfs.isEmpty() || citizens.isEmpty())
            return foxSideFinisher(this)

        return null
    }

    fun getParticipant(uniqueId: UUID): Participant? =
        participants.firstOrNull { it.uniqueId == uniqueId }

    fun getAllParticipants(): ParticipantScope.All = ParticipantScope.All(participants)
}