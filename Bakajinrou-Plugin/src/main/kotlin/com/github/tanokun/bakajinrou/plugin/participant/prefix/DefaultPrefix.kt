package com.github.tanokun.bakajinrou.plugin.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.text

/**
 * viewerを基準にしてプレフィックスを決定します。順に優先度が高い条件です。
 * - 観察者が観戦または死亡 -> 完全なプレフィックス
 * - 観察者が被観察者と一緒 -> デフォルトプレフィックス
 **/
class DefaultPrefix(
    revealedPrefix: String,
    defaultPrefix: String,
    color: TextColor
): Prefix {
    val revealedPrefix = component { text(revealedPrefix) color color.asHexString() }
    val defaultPrefix = component { text(defaultPrefix) color color.asHexString() }

    /**
     * [viewer]と[target]の状況から、適するプレフィックスを選びます。
     * また、当てはまる条件が存在しない場合、nullを返します。
     *
     * @param viewer 観察者
     * @param target 非観察者
     *
     * @return 適するプレフィックス
     */
    override fun resolvePrefix(viewer: Participant, target: Participant): Component? {
        if (isRevealed(viewer)) return revealedPrefix
        if (target == viewer) return defaultPrefix

        return null
    }
}