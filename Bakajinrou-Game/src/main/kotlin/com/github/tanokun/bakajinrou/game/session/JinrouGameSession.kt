package com.github.tanokun.bakajinrou.game.session

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.logging.Logger

class JinrouGameSession(
    private val game: JinrouGame,
    private val scheduler: GameScheduler,
    debug: Logger,
    topScope: CoroutineScope
) {
    private val job: Job = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        debug.severe(throwable.stackTraceToString())
    }

    val mainDispatcherScope: CoroutineScope = CoroutineScope(topScope.coroutineContext + exceptionHandler + job)

    private val _initFlow: MutableSharedFlow<ParticipantId> = MutableSharedFlow()

    init {
        require(game.judge() == null) {
            "始めるにあたって、不十分な役職配布です。"
        }

        topScope.launch {
            game.observeWin(topScope)
                .collect { finish() }
        }
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
        job.cancel()
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
            game.getCurrentParticipants().forEach {
                _initFlow.emit(it.participantId)
            }
        }

        scheduler.launch()
    }

    fun isFinished() = scheduler.getCurrentState() is ScheduleState.Cancelled

    /**
     * ゲームを強制終了することを通知します。
     */
    fun notifyWonBySystem() = mainDispatcherScope.launch { game.notifyWonBySystem() }

    /**
     * ゲームを市民の勝利で終了することを通知します。
     */
    fun notifyWonCitizen() = mainDispatcherScope.launch { game.notifyWonCitizen() }

    /**
     * 勝利条件が成立したタイミングで、勝者情報を通知するFlowを返します。
     *
     * このFlowは、参加者の状態が変化するたび評価し、
     * 勝者が決定した場合は、その情報を1回だけ通知します。
     * また、複数の購読者に対して同じ勝者情報を同時に共有します。
     *
     * 強制終了の場合、それを通知します。
     *
     * @param scope 監視するコルーチンスコープ
     *
     * @see JinrouGame.observeWin
     */
    fun observeWin(scope: CoroutineScope = mainDispatcherScope): Flow<WonInfo> = game.observeWin(scope)

    /**
     * 全参加者の初期化完了までの通知を返す [kotlinx.coroutines.flow.Flow] を提供します。
     *
     * この Flow は、全参加者を1度購読すると、自動的に購読を終了します。
     *
     * @return 初期化される各 [Participant] を通知する[kotlinx.coroutines.flow.Flow]
     */
    fun observeParticipantAtLaunched() = _initFlow
        .take(game.getCurrentParticipants().size)
        .shareIn(mainDispatcherScope, SharingStarted.Companion.Eagerly, replay = 1)
}