package com.github.tanokun.bakajinrou.game.controller

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.logging.Logger

class JinrouGameController(
    private val game: JinrouGame,
    private val scheduler: GameScheduler,
    debug: Logger,
    mainScope: CoroutineScope
) {
    private val job: Job = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        debug.severe(throwable.stackTraceToString())
    }

    val mainDispatcherScope: CoroutineScope = CoroutineScope(mainScope.coroutineContext + exceptionHandler + job)

    private val _initFlow: MutableSharedFlow<Participant> = MutableSharedFlow()

    init {
/*        require(game.judge() == null) {
            "始めるにあたって、不十分な役職配布です。"
        }*/
    }

    /**
     * ゲームを終了、スケジューラの停止処理を行います。
     * 基本的に、1つのゲームに対して1度しか呼び出されません。
     *
     * 前提条件:
     * - ゲームがアクティブであること
     */
    fun finish() {
        if (!scheduler.isActive()) return

        scheduler.abort()
    }

    /**
     * ゲームを開始、参加者の初期化を行います。
     * 基本的に、1つのゲームに対して1度しか呼び出されません。
     *
     * 副作用：
     * - ゲームスケジューラの開始
     * - 各参加者の初期化処理
     */
    fun launch() {
        if (scheduler.isActive()) return


        mainDispatcherScope.launch {
            game.getAllParticipants().forEach {
                _initFlow.emit(it)
            }
        }

        scheduler.launch()
    }

    /**
     * ゲームを強制終了します。
     */
    fun notifySystemFinish() = mainDispatcherScope.launch { game.notifySystemFinish() }

    /**
     * ゲームを市民の勝利で終了します。
     */
    fun finishWithWonCitizen() = mainDispatcherScope.launch { game.notifyWonCitizenFinish() }

    /**
     * 全参加者の初期化完了までの通知を返す [Flow] を提供します。
     *
     * この Flow は、全参加者を1度購読すると、自動的に購読を終了します。
     *
     * @return 初期化される各 [Participant] を通知する[Flow]
     */
    fun observeParticipantAtLaunched() = _initFlow
        .take(game.getAllParticipants().size)
        .shareIn(mainDispatcherScope, SharingStarted.Eagerly, replay = 1)
}