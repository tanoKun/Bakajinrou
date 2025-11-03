package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.attack.initialization

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attacking.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration.Companion.seconds

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class DelayAttackMethodDistribution(
    private val playerProvider: BukkitPlayerProvider,
    private val game: JinrouGame,
    private val gameScheduler: GameScheduler,
    private val mainScope: CoroutineScope,
): Observer {
    private val period = 10.seconds

    init {
        mainScope.launch {
            gameScheduler
                .observe(mainScope)
                .whenLaunched()
                .take(1)
                .collect { atStarted() }
        }
    }

    private fun atStarted() = game.getCurrentParticipants().forEach { participant ->
        mainScope.launch {
            delay(period)

            playerProvider.waitPlayerOnline(participant.participantId) { player ->
                player.inventory.addItem(
                    ItemStack.of(Material.BOW).apply { editMeta { meta ->
                        meta.isUnbreakable = true
                        meta.addEnchant(Enchantment.UNBREAKING, 1, true)

                        meta.addItemFlags(*ItemFlag.entries.toTypedArray())
                    } }
                )
            }

            game.updateParticipant(participant.participantId) { current ->
                current.grantMethod(ArrowMethod(reason = GrantedReason.INITIALIZED))
            }

            delay(10)
            playerProvider.waitPlayerOnline(participant.participantId)  { player ->
                player.inventory.addItem(ItemStack.of(Material.QUARTZ).apply { amount = 2 })
            }
        }
    }
}