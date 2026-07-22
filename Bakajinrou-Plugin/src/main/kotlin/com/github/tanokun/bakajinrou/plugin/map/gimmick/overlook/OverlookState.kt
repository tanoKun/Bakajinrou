package com.github.tanokun.bakajinrou.plugin.map.gimmick.overlook

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.plugin.participant.method.advantage.ExchangeTargetExclusions
import java.util.concurrent.ConcurrentHashMap

class OverlookState(
    private val exchangeTargetExclusions: ExchangeTargetExclusions,
) {
    private val active = ConcurrentHashMap.newKeySet<ParticipantId>()
    private val lastFailureNotice = ConcurrentHashMap<ParticipantId, Long>()

    fun isActive(participantId: ParticipantId): Boolean = participantId in active

    fun enter(participantId: ParticipantId): Boolean {
        if (!active.add(participantId)) return false
        exchangeTargetExclusions.add(participantId)
        return true
    }

    fun leave(participantId: ParticipantId) {
        active.remove(participantId)
        exchangeTargetExclusions.remove(participantId)
    }

    fun shouldNotifyFailure(participantId: ParticipantId, now: Long): Boolean {
        if (now - (lastFailureNotice[participantId] ?: 0L) < 2_000L) return false
        lastFailureNotice[participantId] = now
        return true
    }

    fun clear() {
        active.forEach(exchangeTargetExclusions::remove)
        active.clear()
        lastFailureNotice.clear()
    }
}
