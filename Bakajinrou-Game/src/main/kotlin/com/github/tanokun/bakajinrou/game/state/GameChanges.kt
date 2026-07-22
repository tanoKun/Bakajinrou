package com.github.tanokun.bakajinrou.game.state

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantDifference
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform

/** Domain 状態の遷移から、横断的な変更通知を導出します。 */
class GameChanges(store: GameStore) {
    val participantChanges: Flow<ParticipantDifference> = store.participantChanges

    val methodChanges: Flow<MethodDifference> = participantChanges.transform { difference ->
        methodDifferences(difference).forEach { emit(it) }
    }

    val naturalWinner: Flow<WonInfo> = participantChanges
        .mapNotNull { store.current.judge() }
        .take(1)

    private fun methodDifferences(difference: ParticipantDifference): List<MethodDifference> {
        val previousMethods = difference.before
            ?.getGrantedMethods()
            .orEmpty()
            .associateBy { it.methodId }
        val currentMethods = difference.after
            .getGrantedMethods()
            .associateBy { it.methodId }

        if (previousMethods == currentMethods) return emptyList()

        val granted = (currentMethods.keys - previousMethods.keys).mapNotNull { methodId ->
            MethodDifference.Granted(
                difference.after.participantId,
                currentMethods[methodId] ?: return@mapNotNull null,
            )
        }
        val removed = (previousMethods.keys - currentMethods.keys).mapNotNull { methodId ->
            MethodDifference.Removed(
                difference.after.participantId,
                previousMethods[methodId] ?: return@mapNotNull null,
            )
        }

        return granted + removed
    }
}
