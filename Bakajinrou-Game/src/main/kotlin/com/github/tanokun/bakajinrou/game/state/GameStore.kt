package com.github.tanokun.bakajinrou.game.state

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.ParticipantDifference
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Domain 状態の唯一の保持・更新口です。 */
class GameStore(initial: JinrouGame) {
    private val mutex = Mutex()
    private val _state = MutableStateFlow(initial)
    private val _participantChanges = MutableSharedFlow<ParticipantDifference>()

    val state: StateFlow<JinrouGame> = _state.asStateFlow()
    val participantChanges: Flow<ParticipantDifference> = _participantChanges.asSharedFlow()

    val current: JinrouGame
        get() = state.value

    suspend fun <T> transact(operation: (JinrouGame) -> GameTransition<T>): T = mutex.withLock {
        val previous = current
        val transition = operation(previous)
        _state.value = transition.game

        participantDifferences(previous, transition.game).forEach {
            _participantChanges.emit(it)
        }

        transition.result
    }

    suspend fun updateParticipant(
        participantId: ParticipantId,
        transform: (Participant) -> Participant,
    ) {
        transact { game ->
            GameTransition(game.updateParticipant(participantId, transform), Unit)
        }
    }

    suspend fun addParticipant(participant: Participant) {
        transact { game -> GameTransition(game.addParticipant(participant), Unit) }
    }

    fun getCurrentParticipants(): ParticipantScope.All = current.getCurrentParticipants()

    fun getParticipant(participantId: ParticipantId): Participant? = current.getParticipant(participantId)

    fun existParticipant(participantId: ParticipantId): Boolean = current.existParticipant(participantId)

    private fun participantDifferences(
        previous: JinrouGame,
        current: JinrouGame,
    ): List<ParticipantDifference> = current.participants.mapNotNull { after ->
        val before = previous.getParticipant(after.participantId)
        if (after.completelyEquals(before)) return@mapNotNull null
        ParticipantDifference(before, after)
    }
}
