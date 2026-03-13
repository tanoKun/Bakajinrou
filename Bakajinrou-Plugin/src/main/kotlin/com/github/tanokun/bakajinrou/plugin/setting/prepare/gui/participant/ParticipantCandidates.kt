package com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant

import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import java.util.UUID

class ParticipantCandidates(candidates: Set<UUID>) {
    private val participants: HashMap<UUID, Boolean> = hashMapOf()

    init {
        candidates.forEach {
            participants[it] = false
        }
    }

    fun toggleParticipant(uuid: UUID) {
        participants[uuid] = !(participants[uuid] ?: false)
    }

    fun getParticipants(): Set<UUID> = participants.filter { it.value }.map { it.key }.toSet()

    fun getSpectators(): Set<UUID> = participants.filter { !it.value }.map { it.key }.toSet()

    fun isParticipant(uuid: UUID): Boolean = participants[uuid] ?: false

    fun toSelected() = SelectedParticipants(getParticipants(), getSpectators())
}