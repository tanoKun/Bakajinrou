package com.github.tanokun.bakajinrou.plugin.system.game.finish.observe

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenOvertime
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.system.game.finish.notifier.SystemFinishNotifier
import com.github.tanokun.bakajinrou.plugin.system.game.finish.notifier.each.CitizenSideFinishNotifier
import com.github.tanokun.bakajinrou.plugin.system.game.finish.notifier.each.FoxSideFinishNotifier
import com.github.tanokun.bakajinrou.plugin.system.game.finish.notifier.each.WolfSideFinishNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 勝利を監視します。
 *
 * このオブザーバーのライフサイクルは、ゲームの持つライフサイクルではなく、その上のライフサイクルを持ちます。
 * つまり、順番によるデットコードが存在しません。
 *
 * 監視対象が "ゲーム終了" であるため、その時点で監視は自動終了します。
 **/
class JudgeGameObserver(
    private val playerProvider: BukkitPlayerProvider,
    private val gameSession: JinrouGameSession,
    private val translator: JinrouTranslator,
    gameScheduler: GameScheduler,
    topScope: CoroutineScope
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