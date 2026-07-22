package com.github.tanokun.bakajinrou.game.session

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.position.Side
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenOvertime
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.game.state.GameStore
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.logging.Logger

class JinrouGameSession(
    private val game: GameStore,
    private val changes: GameChanges,
    private val scheduler: GameScheduler,
    debug: Logger,
    topScope: CoroutineScope,
) {
    private val job: CompletableJob = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        debug.severe(throwable.stackTraceToString())
    }

    val mainDispatcherScope: CoroutineScope =
        CoroutineScope(topScope.coroutineContext + exceptionHandler + job)

    private val _initFlow = MutableSharedFlow<ParticipantId>()
    private val _lifecycle = MutableStateFlow<GameLifecycle>(GameLifecycle.Preparing)

    val lifecycle: StateFlow<GameLifecycle> = _lifecycle.asStateFlow()

    init {
        require(game.current.judge() == null) {
            "始めるにあたって、不十分な役職配布です。"
        }

        mainDispatcherScope.launch(start = CoroutineStart.UNDISPATCHED) {
            changes.naturalWinner.collect(::finish)
        }

        // 時間切れは市民陣営の勝利で終了する。
        mainDispatcherScope.launch(start = CoroutineStart.UNDISPATCHED) {
            scheduler.observe(mainDispatcherScope)
                .whenOvertime()
                .collect { finish(WonInfo.Won(Side.VILLAGE, game.getCurrentParticipants())) }
        }
    }

    private fun finish(result: WonInfo) {
        if (_lifecycle.value is GameLifecycle.Finished) return

        _lifecycle.value = GameLifecycle.Finished(result)

        if (scheduler.isActive()) scheduler.abort()
        job.complete()
        job.cancel()
        mainDispatcherScope.cancel()
    }

    fun launch() {
        if (scheduler.isActive()) return

        _lifecycle.value = GameLifecycle.Running
        mainDispatcherScope.launch {
            game.getCurrentParticipants().forEach {
                _initFlow.emit(it.participantId)
            }
        }

        scheduler.launch()
    }

    fun isFinished(): Boolean = _lifecycle.value is GameLifecycle.Finished

    fun notifyWonBySystem() = mainDispatcherScope.launch {
        finish(WonInfo.System(game.getCurrentParticipants()))
    }

    fun observeWin(): Flow<WonInfo> = lifecycle
        .filterIsInstance<GameLifecycle.Finished>()
        .map { it.result }
        .take(1)

    fun observeParticipantAtLaunched(): Flow<ParticipantId> = _initFlow
        .take(game.getCurrentParticipants().size)
        .shareIn(mainDispatcherScope, SharingStarted.Eagerly, replay = 1)
}
