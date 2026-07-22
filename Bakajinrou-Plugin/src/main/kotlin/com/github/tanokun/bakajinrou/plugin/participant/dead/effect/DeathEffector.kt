package com.github.tanokun.bakajinrou.plugin.participant.dead.effect

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.distinctUntilChangedByParticipantOf
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class DeathEffector(
    game: JinrouGame,
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    mainScope: CoroutineScope,
): Observer {
    init {
        mainScope.launch {
            game.observeParticipants(mainScope)
                .distinctUntilChangedByParticipantOf(Participant::isDead)
                .filter { it.after.isDead() }
                .map { it.after }
                .collect(::showDeathEffect)
        }
    }

    private fun showDeathEffect(dead: Participant) {
        val player = playerProvider.getAllowNull(dead) ?: return
        val title = translator.translate(GameKeys.Death.TITLE, player.locale())

        player.showTitle(Title.title(title, Component.empty()))
        player.playSound(player.location, Sound.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f)
    }
}
