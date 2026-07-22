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

/** ゲーム外から観戦しているプレイヤーの、動的な所属を管理します。 */
class GameAudience(
    private val game: GameStore,
    initialSpectators: Set<PlayerId> = emptySet(),
) {
    private val mutex = Mutex()
    private val _state: MutableStateFlow<GameAudienceState>
    private val _changes = MutableSharedFlow<AudienceChange>()

    val state: StateFlow<GameAudienceState>
    val changes: Flow<AudienceChange> = _changes.asSharedFlow()

    val current: GameAudienceState get() = state.value

    init {
        require(initialSpectators.none(::isParticipant)) {
            "ゲーム参加者を観戦者として登録することはできません。"
        }

        _state = MutableStateFlow(GameAudienceState(initialSpectators))
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

    fun isSpectator(playerId: PlayerId): Boolean = playerId in current.spectators

    private fun isParticipant(playerId: PlayerId): Boolean =
        game.getCurrentParticipants().any { it.playerId == playerId }
}
