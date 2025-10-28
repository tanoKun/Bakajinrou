package com.github.tanokun.bakajinrou.plugin.interaction.game.finished.notification

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.position.*
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.coroutine.TopCoroutineScope
import com.github.tanokun.bakajinrou.plugin.common.formatter.ParticipantsFormatter
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.FormatKeys
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class AllPositionsNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val gameSession: JinrouGameSession,
    private val topScope: TopCoroutineScope,
): Observer {
    init {
        topScope.launch {
            gameSession.observeWin(topScope)
                .take(1)
                .collect(::notifyAllPositions)
        }
    }

    private fun notifyAllPositions(wonInfo: WonInfo) {
        val formatter = ParticipantsFormatter(wonInfo.participants.excludeSpectators(), translator)

        wonInfo.participants.forEach { participant ->
            val player = playerProvider.getAllowNull(participant.participantId) ?: return@forEach
            val locale = player.locale()

            player.sendMessage(formatter.format(locale, FormatKeys.Category.WOLF, ::isWolf to FormatKeys.Participant.WOLF))
            player.sendMessage(formatter.format(locale, FormatKeys.Category.MADMAN, ::isMadman to FormatKeys.Participant.MADMAN))
            player.sendMessage(formatter.format(locale, FormatKeys.Category.FORTUNE,
                ::isFortune to FormatKeys.Participant.Mystic.FORTUNE,
                ::isIdiotAsFortune to FormatKeys.Participant.Idiot.FORTUNE))
            player.sendMessage(formatter.format(locale, FormatKeys.Category.MEDIUM,
                ::isMedium to FormatKeys.Participant.Mystic.MEDIUM,
                ::isIdiotAsMedium to FormatKeys.Participant.Idiot.MEDIUM))
            player.sendMessage(formatter.format(locale, FormatKeys.Category.KNIGHT,
                ::isKnight to FormatKeys.Participant.Mystic.KNIGHT,
                ::isIdiotAsKnight to FormatKeys.Participant.Idiot.KNIGHT))
            player.sendMessage(formatter.format(locale, FormatKeys.Category.CITIZEN, ::isCitizen to FormatKeys.Participant.CITIZEN))
            player.sendMessage(formatter.format(locale, FormatKeys.Category.FOX, ::isFox to FormatKeys.Participant.FOX))
        }
    }
}