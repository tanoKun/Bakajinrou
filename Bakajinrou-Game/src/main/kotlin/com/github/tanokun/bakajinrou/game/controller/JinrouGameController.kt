package com.github.tanokun.bakajinrou.game.controller

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onCancellation
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

class JinrouGameController(
    private val game: JinrouGame,
    private val scheduler: GameScheduler,
    private val debug: Logger,
    uiDispatcher: CoroutineContext
) {
    private val job: Job = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        debug.severe(throwable.stackTraceToString())
    }

    val scope: CoroutineScope = CoroutineScope(job + uiDispatcher + exceptionHandler)

    init {
        require(game.judge() == null) {
            "始めるにあたって、不十分な役職配布です。"
        }

        scheduler.addSchedule(onCancellation {
            job.cancel()
        })
    }

    /**
     * ゲームを終了、スケジューラの停止処理を行います。
     * 基本的に、1つのゲームに対して1度しか呼び出されません。
     *
     * 前提条件:
     * - ゲームがアクティブであること
     *
     * 副作用：
     * - [GameFinisher] を使用し、ゲーム終了通知を送信
     * - ゲームスケジューラの停止
     *
     * @param finisher ゲームの終了を処理する
     */
    fun finish(finisher: GameFinisher) {
        if (!scheduler.isActive()) return

        finisher.notifyFinish()
        scheduler.cancel()
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

        scheduler.launch()
        game.participants.forEach { it.position.doAtStarting(it.uniqueId) }
    }
}