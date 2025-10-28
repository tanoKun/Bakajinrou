package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.drop

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.advantage.AdvantageMethod
import com.github.tanokun.bakajinrou.api.attacking.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.attacking.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.distinctUntilChangedByParticipantOf
import com.github.tanokun.bakajinrou.api.protection.method.ProtectiveMethod
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
    private val game: JinrouGame,
    private val playerProvider: BukkitPlayerProvider,
    private val mainScope: CoroutineScope,
): Observer {
    init {
        mainScope.launch {
            game.observeParticipants(mainScope)
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

                it.type == Material.QUARTZ
                    || (method is AttackMethod && method !is ArrowMethod)
                    || method is ProtectiveMethod
                    || method is AdvantageMethod
            }
            .forEach {
                world.dropItemNaturally(location, it)
            }


        player.inventory.clear()
    }
}