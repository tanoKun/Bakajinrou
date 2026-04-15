package com.github.tanokun.bakajinrou.plugin.rendering.sidebar.component

import com.github.tanokun.bakajinrou.api.translation.PrefixKeys
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.white
import plutoproject.adventurekt.text.text

data class PrefixAmountSidebarComponent(
    private val prefixKeys: List<PrefixKeys>,
    private val amount: Int,
    private val translator: JinrouTranslator,
) : SidebarComponent {
    init {
        require(prefixKeys.isNotEmpty()) { "prefixKeys must not be empty" }
    }

    override fun getComponent(player: Player): List<Component> = listOf(component {
        text(" 「") color gray deco bold
        raw { buildPrefixComponent(player) } deco bold
        text("」→ ") color gray deco bold
        text("${amount}人") color white deco bold
    })

    private fun buildPrefixComponent(player: Player): Component {
        val translatedPrefixes = prefixKeys.map { translator.translate(it, player.locale()) }

        return translatedPrefixes.reduce { acc, next ->
            component {
                raw { acc }
                text(", ") color gray
                raw { next }
            }
        }
    }
}
