package com.github.tanokun.bakajinrou.game.cache

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import java.util.*

object PlayerNameCache {
    private val caches = hashMapOf<UUID, String>()

    fun put(uniqueId: UUID, name: String) {
        caches[uniqueId] = name
    }

    fun get(uniqueId: UUID): String? = caches[uniqueId]

    fun get(participant: Participant): String? = caches[participant.participantId.uniqueId]

    fun get(participantId: ParticipantId): String? = caches[participantId.uniqueId]
}