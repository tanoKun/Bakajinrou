package com.github.tanokun.bakajinrou.plugin.participant.method.ability.commune.notification
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.viewer.GameViewers
import com.github.tanokun.bakajinrou.game.audience.GameAudienceStore
import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.game.ability.medium.CommuneAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.medium.CommuneResult
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 霊媒の結果を、観戦者に通知します。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class SpectatorCommuneNotifier(
    private val playerProvider: BukkitPlayerProvider,
    translator: JinrouTranslator,
    private val game: GameStore,
    private val audience: GameAudienceStore,
    mainScope: CoroutineScope,
    executor: CommuneAbilityExecutor
): Observer {
    private val messageFormatter = SpectatorCommuneMessageFormatter(translator)

    init {
        mainScope.launch {
            executor
                .observeCommune()
                .filterIsInstance<CommuneResult.Success>()
                .collect(::communed)
        }
    }

    private fun communed(result: CommuneResult.Success) {
        val mediumName = PlayerNameCache.get(result.mediumId) ?: "unknown"
        val targetName = PlayerNameCache.get(result.targetId) ?: "unknown"

        GameViewers(game.current, audience.current).spectating
            .mapNotNull { playerProvider.getAllowNull(it.playerId) }
            .forEach {
                val message = messageFormatter.format(result, mediumName, targetName, it.locale())

                it.sendMessage(message)
            }

    }
}
