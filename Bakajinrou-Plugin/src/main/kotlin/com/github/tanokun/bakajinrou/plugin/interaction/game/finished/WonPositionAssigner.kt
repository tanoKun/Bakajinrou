package com.github.tanokun.bakajinrou.plugin.interaction.game.finished

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenOvertime
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.coroutine.TopCoroutineScope
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.game.finished.finisher.SystemFinishNotifier
import com.github.tanokun.bakajinrou.plugin.interaction.game.finished.finisher.each.CitizenSideFinishNotifier
import com.github.tanokun.bakajinrou.plugin.interaction.game.finished.finisher.each.FoxSideFinishNotifier
import com.github.tanokun.bakajinrou.plugin.interaction.game.finished.finisher.each.WolfSideFinishNotifier
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 勝利を監視します。
 *
 * このオブザーバーのライフサイクルは、ゲームの持つライフサイクルではなく、その上のライフサイクルを持ちます。
 * つまり、順番によるデットコードが存在しません。
 *
 * 監視対象が "ゲーム終了" であるため、その時点で監視は自動終了します。
 **/
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class WonPositionAssigner(
    private val playerProvider: BukkitPlayerProvider,
    private val gameSession: JinrouGameSession,
    private val translator: JinrouTranslator,
    gameScheduler: GameScheduler,
    topScope: TopCoroutineScope
): Observer {
    init {
        topScope.launch {
            gameSession
                .observeWin(topScope)
                .collect(::observeWin)
        }

        gameSession.mainDispatcherScope.launch {
            gameScheduler
                .observe(gameSession.mainDispatcherScope)
                .whenOvertime()
                .collect { winAtOvertime() }
        }
    }


    private fun observeWin(wonInfo: WonInfo) {
        val finisher = when (wonInfo) {
            is WonInfo.Wolfs -> WolfSideFinishNotifier(playerProvider, translator)
            is WonInfo.Citizens -> CitizenSideFinishNotifier(playerProvider, translator)
            is WonInfo.Fox -> FoxSideFinishNotifier(playerProvider, translator)
            is WonInfo.System -> SystemFinishNotifier
        }

        finisher.notify(wonInfo)
    }

    fun winAtOvertime() = gameSession.notifyWonCitizen()
}