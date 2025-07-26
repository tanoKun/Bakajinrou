package com.github.tanokun.bakajinrou.plugin.formatter.display

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.formatter.getPositionColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.text

class PrefixModifier(
    val target: Participant
) {
    var comingOut: ComingOut? = null

    private val positionColor: TextColor = getPositionColor(position = target.position)

    /**
     * 役職別のプレフィックスと、カミングアウトを足し合わせたプレフィックスを作成します。
     * 主なルールは[com.github.tanokun.bakajinrou.api.participant.position.Prefix]を参照してください。
     *
     * @param viewer 観察者
     *
     * @return 統合したプレフィックス、または空のコンポーネント
     *
     * @see com.github.tanokun.bakajinrou.api.participant.position.Prefix
     */
    fun createPrefix(viewer: Participant): Component {
        val resolvedPrefix = target.resolvePrefix(viewer) ?: let {
            return applyPrefixOnlyComingOut()
        }

        return component {
            text("[") color gray

            text(resolvedPrefix) color positionColor.asHexString()

            comingOut?.let {
                text(", ") color gray
                raw { Component.text(it.displayName, it.color) }
            }

            text("]") color gray
        }
    }

    private fun applyPrefixOnlyComingOut(): Component = comingOut?.let {
        component {
            text("[") color gray
            text(it.displayName) color it.color.asHexString()
            text("]") color gray
        }

    } ?: Component.text("")
}