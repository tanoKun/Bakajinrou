package com.github.tanokun.bakajinrou.plugin.interaction.game.initialization

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.map.GameMap
import com.github.tanokun.bakajinrou.api.map.PointLocation
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.formatter.toTick
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class PreventFailureStarting(
    private val playerProvider: BukkitPlayerProvider,
    private val game: JinrouGame,
    private val gameScheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    private val gameMap: GameMap,
): Observer {
    private val period = 10.seconds

    init {
        mainScope.launch {
            gameScheduler
                .observe(mainScope)
                .whenLaunched()
                .take(1)
                .collect { atStartGame() }
        }
    }

    private fun atStartGame() = game.getCurrentParticipants().forEach { participant ->
        mainScope.launch {
            val player = playerProvider.waitPlayerOnline(participant.participantId)
            player.isInvisible = false

            if (participant.isAlive()) player.gameMode = GameMode.ADVENTURE
            else player.gameMode = GameMode.SPECTATOR

            val timeState = gameScheduler.getCurrentState() as? ScheduleState.Active ?: return@launch
            val period = period - timeState.passedTime

            if (period > Duration.ZERO) {
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, period.toTick(), 2, true, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, period.toTick(), 1, true, false))
            }

            player.teleport(gameMap.spawnPoint.asBukkit())
        }
    }
}


fun PointLocation.asBukkit(): Location {
    val world = Bukkit.getWorld(this.worldName) ?: Bukkit.getWorlds().first()

    return Location(world, this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}