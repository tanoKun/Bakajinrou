package com.github.tanokun.bakajinrou.plugin.game.initialization.spectator

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.player.PlayerId
import com.github.tanokun.bakajinrou.game.audience.AudienceChange
import com.github.tanokun.bakajinrou.game.audience.GameAudienceStore
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.game.initialization.asBukkit
import com.github.tanokun.bakajinrou.plugin.map.GameMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.bukkit.GameMode
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/** 観戦者をゲーム領域へ参加させ、退出時にはロビーへ戻します。 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class SpectatorStarter(
    private val playerProvider: BukkitPlayerProvider,
    private val audience: GameAudienceStore,
    private val gameScheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    private val gameMap: GameMap,
): Observer {
    private var started = false

    init {
        mainScope.launch {
            gameScheduler
                .observe(mainScope)
                .whenLaunched()
                .take(1)
                .collect { startInitialSpectators() }
        }

        mainScope.launch {
            audience.changes.collect(::onAudienceChanged)
        }
    }

    private fun startInitialSpectators() {
        started = true
        audience.current.spectators.forEach { playerId ->
            mainScope.launch { startSpectating(playerId) }
        }
    }

    private suspend fun onAudienceChanged(change: AudienceChange) {
        if (!started) return

        when (change) {
            is AudienceChange.Joined -> startSpectating(change.playerId)
            is AudienceChange.Left -> stopSpectating(change.playerId)
        }
    }

    private suspend fun startSpectating(playerId: PlayerId) {
        val player = playerProvider.waitPlayerOnline(playerId)
        player.gameMode = GameMode.SPECTATOR
        player.teleport(gameMap.spawnPoint.asBukkit())
    }

    private fun stopSpectating(playerId: PlayerId) {
        val player = playerProvider.getAllowNull(playerId) ?: return
        player.gameMode = GameMode.ADVENTURE
        player.teleport(gameMap.lobbyPoint.asBukkit())
    }
}
