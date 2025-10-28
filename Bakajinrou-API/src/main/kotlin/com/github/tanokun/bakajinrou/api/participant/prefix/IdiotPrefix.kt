package com.github.tanokun.bakajinrou.api.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

/**
 * viewerを基準にして人狼のプレフィックスを決定します。記述順に優先度が高いです。
 * - 観察者が観戦または死亡 -> バカのプレフィックス
 * - 観察者が観察者と一緒 -> 偽役職のプレフィックス
 * - その他 -> null
 **/
data class IdiotPrefix(
    private val realKey: PrefixKeys.Idiot,
    private val fakeKey: PrefixKeys.Mystic,
): PrefixSource {
    override fun getVisibleSource(viewer: Participant, target: Participant): PrefixKeys? {
        if (viewer.isDead()) return realKey
        if (viewer == target) return fakeKey

        return null
    }
}