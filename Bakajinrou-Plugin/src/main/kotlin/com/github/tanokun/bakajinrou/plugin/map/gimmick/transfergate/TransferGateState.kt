package com.github.tanokun.bakajinrou.plugin.map.gimmick.transfergate

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class TransferGateState {
    private val disabledUntil = ConcurrentHashMap<UUID, Long>()
    private val lastFailureNotice = ConcurrentHashMap<UUID, Long>()

    fun refresh(now: Long) {
        disabledUntil.entries.removeIf { it.value <= now }
    }

    fun isAvailable(gateId: UUID, now: Long): Boolean = (disabledUntil[gateId] ?: 0L) <= now

    fun disable(gateId: UUID, until: Long) {
        disabledUntil[gateId] = until
    }

    fun shouldNotify(playerId: UUID, now: Long): Boolean {
        if (now - (lastFailureNotice[playerId] ?: 0L) < 2_000L) return false
        lastFailureNotice[playerId] = now
        return true
    }

    fun clear() {
        disabledUntil.clear()
        lastFailureNotice.clear()
    }
}
