package com.github.tanokun.bakajinrou.plugin.participant.method.advantage

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import java.util.concurrent.ConcurrentHashMap
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [ExchangeTargetExclusions::class])
@Scope(value = GameComponents::class)
class ExchangeTargetExclusions {
    private val participantIds = ConcurrentHashMap.newKeySet<ParticipantId>()

    fun add(participantId: ParticipantId) {
        participantIds += participantId
    }

    fun remove(participantId: ParticipantId) {
        participantIds -= participantId
    }

    fun snapshot(): Set<ParticipantId> = participantIds.toSet()
}
