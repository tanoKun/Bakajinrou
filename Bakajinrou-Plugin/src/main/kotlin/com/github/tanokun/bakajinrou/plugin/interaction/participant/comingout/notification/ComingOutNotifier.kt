package com.github.tanokun.bakajinrou.plugin.interaction.participant.comingout.notification

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.distinctUntilChangedByParticipantOf
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class ComingOutNotifier(
    private val game: JinrouGame,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator
): Observer {
    init {
        mainScope.launch {
            game.observeParticipants(mainScope)
                .distinctUntilChangedByParticipantOf(Participant::comingOut)
                .map { it.after }
                .collect(::onComingOut)
        }
    }

    private fun onComingOut(participant: Participant) {
        val comingOut = participant.comingOut ?: return
        val name = PlayerNameCache.get(participant) ?: "unknown"

        playerProvider.getAllowNull(participant)?.playSound(
            Sound.sound(NamespacedKey("minecraft", "block.enchantment_table.use"), Sound.Source.PLAYER, 1.0f, 1.0f)
        )

        game.getCurrentParticipants()
            .mapNotNull { playerProvider.getAllowNull(it.participantId) }
            .forEach {
                val comingOutComponent = translator.translate(comingOut.translationKey, it.locale())
                val message = translator.translate(GameKeys.ComingOut.USING_MESSAGE, it.locale(), Component.text(name), comingOutComponent)

                it.sendMessage(message)
            }
    }
}