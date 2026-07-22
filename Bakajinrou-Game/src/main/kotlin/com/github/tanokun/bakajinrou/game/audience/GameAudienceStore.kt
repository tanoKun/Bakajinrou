package com.github.tanokun.bakajinrou.game.audience

import com.github.tanokun.bakajinrou.api.player.PlayerId
import com.github.tanokun.bakajinrou.game.state.GameStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** 観戦者の所属を保持し、途中参加と退出を直列化します。 */
class GameAudienceStore(
    private val game: GameStore,
    initial: GameAudience = GameAudience(emptySet()),
) {
    private val mutex = Mutex()
    private val _state: MutableStateFlow<GameAudience>
    private val _changes = MutableSharedFlow<AudienceChange>()

    val state: StateFlow<GameAudience>
    val changes: Flow<AudienceChange> = _changes.asSharedFlow()

    val current: GameAudience get() = state.value

    init {
        require(initial.spectators.none(::isParticipant)) {
            "ゲーム参加者を観戦者として登録することはできません。"
        }

        _state = MutableStateFlow(initial)
        state = _state.asStateFlow()
    }

    suspend fun joinSpectator(playerId: PlayerId): JoinSpectatorResult = mutex.withLock {
        if (isParticipant(playerId)) return JoinSpectatorResult.ParticipantCannotSpectate
        if (playerId in current.spectators) return JoinSpectatorResult.AlreadySpectating

        _state.value = current.copy(spectators = current.spectators + playerId)
        _changes.emit(AudienceChange.Joined(playerId))
        JoinSpectatorResult.Joined
    }

    suspend fun leaveSpectator(playerId: PlayerId): Boolean = mutex.withLock {
        if (playerId !in current.spectators) return false

        _state.value = current.copy(spectators = current.spectators - playerId)
        _changes.emit(AudienceChange.Left(playerId))
        true
    }

    private fun isParticipant(playerId: PlayerId): Boolean =
        game.getCurrentParticipants().any { it.playerId == playerId }
}
