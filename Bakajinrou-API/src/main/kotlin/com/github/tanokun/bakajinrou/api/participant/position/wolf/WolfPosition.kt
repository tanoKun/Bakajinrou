package com.github.tanokun.bakajinrou.api.participant.position.wolf

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.isMadman
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.participant.prefix.WolfPrefix

/**
 * このクラスは、人狼という役職に固有のルールと、それに紐付くPrefixの表示ルールをカプセル化します。
 * 特に、人狼がどの狂人に知られているか、という依存関係を安全に解決し、
 * 不正な状態のオブジェクトが生成されることを防ぐ役割を担います。
 *
 * @property knownByMadmans この人狼の正体を知ることのできる、特定の狂人のリスト。
 *
 * @throws IllegalStateException [knownByMadmans]に狂人以外の役職が含まれていた場合
 */
class WolfPosition(knownByMadmans: ParticipantScope.NonSpectators): Position {

    init {
        if (knownByMadmans.excludes(::isMadman).isNotEmpty())
            throw IllegalStateException("唯一知ることのできる参加者は「狂人」でないといけません。")
    }

    override val prefixSource: PrefixSource = WolfPrefix(knownByMadmans)

    override val abilityResult: ResultSource = ResultSource.WOLF
}