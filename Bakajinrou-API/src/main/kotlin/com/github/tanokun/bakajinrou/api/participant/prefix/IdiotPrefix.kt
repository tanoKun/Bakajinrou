package com.github.tanokun.bakajinrou.api.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translate.TranslationKey

/**
 * viewerを基準にして人狼のプレフィックスを決定します。記述順に優先度が高いです。
 * - 観察者が観戦または死亡 -> バカのプレフィックス
 * - 観察者が観察者と一緒 -> 偽役職のプレフィックス
 * - その他 -> null
 **/
class IdiotPrefix(
    private val realKey: TranslationKey,
    private val idiotKey: TranslationKey,
): PrefixSource {
    override fun getVisibleSource(viewer: Participant, target: Participant): TranslationKey? {
        if (viewer.isDead()) return realKey
        if (viewer == target) return idiotKey

        return null
    }
}