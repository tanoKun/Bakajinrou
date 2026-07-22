package com.github.tanokun.bakajinrou.plugin.participant.dead

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.state.distinctUntilChangedByParticipantOf
import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import java.util.*

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class DropHoldMethods(
    private val game: GameStore,
    private val playerProvider: BukkitPlayerProvider,
    private val mainScope: CoroutineScope,
): Observer {
    init {
        mainScope.launch {
            game.participantChanges
                .distinctUntilChangedByParticipantOf(Participant::isDead)
                .map { it.after }
                .collect(::onDeath)
        }
    }

    private fun onDeath(dead: Participant) = mainScope.launch {
        val player = playerProvider.waitPlayerOnline(dead)
        val location = player.location.add(0.0, 1.0, 0.0)
        val world = player.world

        player.inventory.contents
            .filterNotNull()
            .filter {
                val methodId = it.getMethodId() ?: UUID.randomUUID().asMethodId()
                val method = dead.getGrantedMethod(methodId)

                it.type == Material.QUARTZ || (method?.transportable == true)
            }
            .forEach {
                world.dropItemNaturally(location, it).apply {
                    thrower = player.uniqueId
                }
            }


        player.inventory.clear()
    }
}
