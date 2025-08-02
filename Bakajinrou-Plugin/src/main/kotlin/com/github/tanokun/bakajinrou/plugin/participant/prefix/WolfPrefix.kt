package com.github.tanokun.bakajinrou.plugin.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import net.kyori.adventure.text.Component
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.text

/**
 * viewerを基準にして人狼のプレフィックスを決定します。順に優先度が高い条件です。
 * - 観察者が観戦または死亡 -> 人狼プレフィックス
 * - 観察者が人狼 -> 人狼プレフィックス
 * - 観察者が、唯一知っている狂人 -> 人狼プレフィックス
 * - 観察者が被観察者と一緒 -> 人狼プレフィックス
 **/
class WolfPrefix(private val knownByMadmans: ParticipantScope.NonSpectators): Prefix {
    private val prefix = component { text("人狼") color Positions.Wolf.color.asHexString() }

    init {
        if (knownByMadmans.excludePosition<MadmanPosition>().isNotEmpty())
            throw IllegalStateException("唯一知ることのできる参加者は「狂人」でないといけません。")
    }

    override fun resolvePrefix(viewer: Participant, target: Participant): Component? {
        if (isRevealed(viewer)) return prefix
        if (viewer.isPosition<WolfPosition>()) return prefix
        if (knownByMadmans.contains(viewer)) return prefix
        if (target == viewer) return prefix

        return null
    }
}