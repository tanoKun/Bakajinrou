package com.github.tanokun.bakajinrou.api.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.api.translate.TranslationKey
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys

/**
 * viewerを基準にして人狼のプレフィックスを決定します。
 * - 観察者が観戦または死亡 -> 人狼プレフィックス
 * - 観察者が人狼 -> 人狼プレフィックス
 * - 観察者が、唯一知っている狂人 -> 人狼プレフィックス
 * - 観察者が被観察者と一緒 -> 人狼プレフィックス
 * - その他 -> null
 **/
class WolfPrefix(private val knownByMadmans: ParticipantScope.NonSpectators): PrefixSource {
    private val translationKey: TranslationKey = TranslationKeys.Prefix.WOLF

    override fun getVisibleSource(viewer: Participant, target: Participant): TranslationKey? {
        if (viewer.isDead()) return translationKey
        if (viewer.isPosition<WolfPosition>()) return translationKey
        if (knownByMadmans.contains(viewer)) return translationKey
        if (target == viewer) return translationKey

        return null
    }
}