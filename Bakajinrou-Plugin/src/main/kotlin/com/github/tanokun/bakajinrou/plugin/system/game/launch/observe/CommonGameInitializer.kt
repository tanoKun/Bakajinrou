package com.github.tanokun.bakajinrou.plugin.system.game.launch.observe

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.map.GameMap
import com.github.tanokun.bakajinrou.api.map.PointLocation
import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.formatter.toTick
import com.github.tanokun.bakajinrou.plugin.interaction.participant.state.update.view.updatePlayerListName
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class CommonGameInitializer(
    private val playerProvider: BukkitPlayerProvider,
    private val game: JinrouGame,
    private val gameScheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    private val gameMap: GameMap,
    private val translator: JinrouTranslator
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

            val timeState = gameScheduler.getCurrentState() as? ScheduleState.Active ?: return@launch
            val period = period - timeState.passedTime

            if (period > Duration.Companion.ZERO) {
                val startTitle = Title.title(
                    translator.translate(GameKeys.Start.TITLE, player.locale()),
                    translator.translate(GameKeys.Start.SUB_TITLE, player.locale())
                )
                player.showTitle(startTitle)
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, period.toTick(), 2, true, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, period.toTick(), 1, true, false))
            }

            player.inventory
                .filterNot { participant.hasGrantedMethod(it?.getMethodId() ?: return@filterNot false) }
                .forEach { player.inventory.remove(it) }

            player.teleport(gameMap.spawnPoint.asBukkit())
            player.updatePlayerListName()
        }

        mainScope.launch {
            delay(period)

            game.updateParticipant(participant.participantId) { current ->
                current.grantMethod(ArrowMethod(reason = GrantedReason.INITIALIZE))
            }

            val player = playerProvider.waitPlayerOnline(participant.participantId)
            player.inventory.addItem(ItemStack.of(Material.BOW))
        }
    }
}


fun PointLocation.asBukkit(): Location {
    val world = Bukkit.getWorld(this.worldName) ?: Bukkit.getWorlds().first()

    return Location(world, this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}