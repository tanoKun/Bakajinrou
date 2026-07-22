package com.github.tanokun.bakajinrou.plugin.participant.dead

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.viewer.GameViewers
import com.github.tanokun.bakajinrou.game.audience.GameAudienceStore
import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.game.attacking.AttackResolution
import com.github.tanokun.bakajinrou.game.attacking.Attacking
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.DisplayLoggingKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 参加者が死亡したことを、観戦者に通知します。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class SpectatorDeathNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val game: GameStore,
    private val audience: GameAudienceStore,
    mainScope: CoroutineScope,
    attacking: Attacking
): Observer {
    init {
        mainScope.launch {
            attacking
                .observeAttack()
                .filterIsInstance<AttackResolution.Killed>()
                .collect(::death)
        }
    }

    private fun death(result: AttackResolution.Killed) {
        val attackerName = PlayerNameCache.get(result.attackerId) ?: "unknown"
        val victimName = PlayerNameCache.get(result.victimId) ?: "unknown"

        GameViewers(game.current, audience.current).spectating
            .mapNotNull { playerProvider.getAllowNull(it.playerId) }
            .forEach {
                val message = translator.translate(
                    DisplayLoggingKeys.KILL, it.locale(), Component.text(attackerName), Component.text(victimName))

                it.sendMessage(message)
            }
    }
}
