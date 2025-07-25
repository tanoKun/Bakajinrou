package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import java.util.*

class JinrouGame(
    val participants: List<Participant>,
    val citizenSideFinisher: (JinrouGame) -> GameFinisher,
    val wolfSideFinisher: (JinrouGame) -> GameFinisher,
    val foxSideFinisher: (JinrouGame) -> GameFinisher
) {

    init {
        if (participants.distinctBy { it.uniqueId }.count() != participants.count())
            throw IllegalArgumentException("重複したプレイヤーが存在します。")
    }

    /**
     * [participants] の役職から、勝利条件を満たす参加者が存在する場合、
     * その陣営のフィニッシャーを返します。
     *
     * @return 勝利条件を満たしている陣営のフィニッシャー
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
}