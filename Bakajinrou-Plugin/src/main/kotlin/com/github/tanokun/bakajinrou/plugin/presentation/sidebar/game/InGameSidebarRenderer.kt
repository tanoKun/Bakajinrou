package com.github.tanokun.bakajinrou.plugin.presentation.sidebar.game

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.presentation.sidebar.Sidebar
import com.github.tanokun.bakajinrou.plugin.presentation.sidebar.SidebarContent
import com.github.tanokun.bakajinrou.plugin.presentation.sidebar.SidebarPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.green
import plutoproject.adventurekt.text.style.white
import plutoproject.adventurekt.text.text
import kotlin.time.Duration.Companion.seconds

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class InGameSidebarRenderer(
    private val game: JinrouGame,
    private val scheduler: GameScheduler,
    private val translator: JinrouTranslator,
    private val playerProvider: BukkitPlayerProvider,
    private val mainScope: CoroutineScope,
) : Observer {
    private val sidebar = Sidebar()

    init {
        mainScope.launch {
            scheduler.observe(mainScope)
                .whenLaunched()
                .take(1)
                .collect { cycle() }
        }
    }

    private suspend fun cycle() = sidebar.cycle(
        viewers = {
            game.getCurrentParticipants().mapNotNull { participant ->
                playerProvider.getAllowNull(participant.participantId)
            }
        },
        pages = listOf(
            SidebarPage(5.seconds, ::createPositionDistribution),
            SidebarPage(5.seconds, ::createParticipants),
        ),
    )

    private fun createPositionDistribution(player: Player): SidebarContent {
        val lines = createViewerHeader(player)
        lines.add(component { text("役職分配: ") color gray deco bold })

        val positionCounts = game.getCurrentParticipants()
            .excludeSpectators()
            .mapNotNull { participant -> participant.getOwnPrefix()?.let { it to participant } }
            .groupingBy { (prefix) -> prefix }
            .eachCount()

        positionCounts.forEach { (prefix, amount) ->
            lines.add(component {
                text(" 「") color gray deco bold
                raw { translator.translate(prefix, player.locale()) } deco bold
                text("」→ ") color gray deco bold
                text("${amount}人") color white deco bold
            })
        }

        return SidebarContent(
            title = component { text("役職分配") color green deco bold },
            lines = lines,
        )
    }

    private fun createParticipants(player: Player): SidebarContent {
        val lines = createViewerHeader(player)

        game.getCurrentParticipants().excludeSpectators().forEach { participant ->
            lines.add(component {
                text("・") color gray deco bold
                text(PlayerNameCache.get(participant.participantId.uniqueId) ?: "") color white deco bold
            })
        }

        return SidebarContent(
            title = component { text("参加者一覧") color green deco bold },
            lines = lines,
        )
    }

    private fun createViewerHeader(player: Player): MutableList<Component> {
        val self = game.getParticipant(player.uniqueId.asParticipantId())
        val positionName = self?.getOwnPrefix()?.let { translator.translate(it, player.locale()) }

        return mutableListOf(
            component {
                text("自分の役職: ") color gray deco bold
                if (positionName == null) text("不明") color white deco bold
                else raw { positionName } deco bold
            },
            Component.empty(),
        )
    }

    private fun Participant.getOwnPrefix(): PrefixKeys? = getPrefix(this)
}
