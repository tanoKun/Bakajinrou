package com.github.tanokun.bakajinrou.game.scheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class GameScheduler(startTime: Duration) {
    private val _state = MutableStateFlow<ScheduleState>(ScheduleState.Pending(startTime))

    init {
        if (startTime < 0.seconds) throw IllegalArgumentException("スケジューラーは1秒以上動かせる必要があります。")
    }

    /**
     * スケジューラーを起動します。2重起動、再起動は出来ません。
     *
     * @throws IllegalStateException 2重起動時
     * @throws IllegalStateException 既に停止されている場合
     */
    open fun launch() {
        val previousState = _state.value

        if (previousState is ScheduleState.Active) throw IllegalStateException("二重起動はできません。")
        if (previousState is ScheduleState.Cancelled) throw IllegalStateException("既に停止されたスケジューラーです。")

        previousState as ScheduleState.Pending

        _state.value = previousState.launch()
    }

    /**
     * スケジューラーを停止します。非稼働時に停止はできません。
     *
     * @throws IllegalStateException 非稼働時
     * @throws IllegalStateException 既に停止されている場合
     */
    open fun abort() {
        val previousState = _state.value

        if (previousState is ScheduleState.Pending) throw IllegalStateException("まだ起動されていないスケジューラーです。")
        if (previousState is ScheduleState.Cancelled) throw IllegalStateException("既に停止されています。")

        previousState as ScheduleState.Active

        _state.value = previousState.abort()
    }

    /**
     * スケジューラーが時間切れで終了するときに呼び出されます。
     */
    abstract fun overtime()

    /**
     * スケジュールが実行中か判定します。
     * キャンセル、まだスタートされていないものは停止判定です。
     *
     * @return 実行中true 停止中false
     */
    fun isActive(): Boolean {
        val state = _state.value

        return (state is ScheduleState.Active)

    }

    protected fun advance(time: Duration) {
        if (!isActive()) throw IllegalStateException("このスケジューラーはアクティブではありません。")

        val previousState = _state.value as ScheduleState.Active
        _state.value = previousState.advance(time)

        if (_state.value is ScheduleState.Cancelled.Overtime) overtime()
    }

    /**
     * @return 現状のスケジュールの状態
     */
    fun getCurrentState() = _state.value

    fun observe(scope: CoroutineScope): Flow<ScheduleState> =
        _state.shareIn(scope, started = SharingStarted.Eagerly, replay = 1)

}