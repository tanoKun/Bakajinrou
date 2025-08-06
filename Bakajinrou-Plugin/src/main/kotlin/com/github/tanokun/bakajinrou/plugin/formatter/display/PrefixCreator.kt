package com.github.tanokun.bakajinrou.plugin.formatter.display

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.localization.UITranslationKeys.Formatter.Participant.State
import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.Translator
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.*
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import java.util.*

class PrefixCreator(private val translator: Translator) {
    /**
     * 役職別のプレフィックスと、カミングアウトを足し合わせたプレフィックスを作成します。
     * 主なルールは[com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource]を参照してください。
     *
     * @param viewer 観察者
     *
     * @return 統合したプレフィックス、または空のコンポーネント
     *
     * @see com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
     */
    fun createPrefix(viewer: Participant, target: Participant, locale: Locale): Component {
        val resolvedPrefix = target.getPrefix(viewer)?.let {
            translator.translate(Component.translatable(it.key), locale)
        }

        val comingOutPrefix = target.comingOut?.let {
            val prefixSource = it.getVisibleSource(viewer, target).key
            val prefix: Component = translator.translate(Component.translatable(prefixSource), locale) ?: return@let null
            component { raw { prefix } with bold }
        }

        val absentPrefix =
            if (target.isSuspended()) let {
                val translate = translator.translate(Component.translatable(State.SUSPENDED.key), locale) ?: return@let null

                component { raw { translate } deco bold }
            } else null

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