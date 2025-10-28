package com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.component

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEntryComponent
import net.kyori.adventure.text.Component
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.*
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import java.util.*

abstract class ParticipantInfoInGameComponent(private val translator: JinrouTranslator): TabEntryComponent {
    abstract fun orderDecider(target: Participant): Int

    protected fun createDisplayName(viewer: Participant, target: Participant, name: String, locale: Locale): Component =
        component {
            val prefix = createPrefix(viewer, target, locale)

            if (prefix != Component.text("")) {
                raw { prefix }
                text(" ")
            }

            text(name)
        }

    private fun createPrefix(viewer: Participant, target: Participant, locale: Locale): Component {
        val resolvedPrefix = target.getPrefix(viewer)?.let {
            translator.translate(it, locale)
        }

        val comingOutPrefix = target.comingOut?.let {
            val prefixSource = it.getVisibleSource(viewer, target)
            val prefix: Component = translator.translate(prefixSource, locale)
            component { raw { prefix } with bold }
        }

        val absentPrefix =
            if (target.isSuspended()) let {
                val translate = translator.translate(PrefixKeys.SUSPENDED, locale)

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