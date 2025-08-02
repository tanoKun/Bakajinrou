package com.github.tanokun.bakajinrou.plugin.formatter.display

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.participant.position.HasPrefix
import com.github.tanokun.bakajinrou.plugin.participant.prefix.Prefix
import net.kyori.adventure.text.Component
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.*
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gold
import plutoproject.adventurekt.text.style.gray

class PrefixModifier(
    val target: Participant
) {
    var comingOut: ComingOut? = null

    private val prefix: Prefix = (target.position as HasPrefix).prefix

    /**
     * 役職別のプレフィックスと、カミングアウトを足し合わせたプレフィックスを作成します。
     * 主なルールは[com.github.tanokun.bakajinrou.plugin.participant.prefix.DefaultPrefix]を参照してください。
     *
     * @param viewer 観察者
     *
     * @return 統合したプレフィックス、または空のコンポーネント
     *
     * @see com.github.tanokun.bakajinrou.plugin.participant.prefix.DefaultPrefix
     */
    fun createPrefix(viewer: Participant): Component {
        prefix.resolvePrefix(viewer, target)?.let {
            component { raw(it) with bold }
        }

        val resolvedPrefix = prefix.resolvePrefix(viewer, target)?.let {
            component { raw(it) with bold }
        }

        val comingOutPrefix = comingOut?.let {
            component { text(it.displayName) color it.color.asHexString() with bold }
        }

        val absentPrefix =
            if (target.state == ParticipantStates.SUSPENDED)
                component { text("退出中") color gold deco bold }
            else
                null

        return join(resolvedPrefix, comingOutPrefix, absentPrefix)
    }

    private fun join(vararg prefixes: Component?): Component {
        val prefixes = prefixes.filterNotNull()
        if (prefixes.isEmpty()) return Component.text("")

        return component {
            text("[") color gray

            val prefix = prefixes.reduce { acc, next ->
                component {
                    raw { acc }
                    text(", ") color gray
                    raw { next }
                }
            }

            raw { prefix }

            text("]") color gray
        }
    }
}