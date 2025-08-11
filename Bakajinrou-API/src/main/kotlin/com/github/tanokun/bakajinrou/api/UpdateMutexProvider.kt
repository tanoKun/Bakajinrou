package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.ConcurrentHashMap

class UpdateMutexProvider {
    private val mutexes = ConcurrentHashMap<ParticipantId, Mutex>()

    /**
     * 指定されたIDに対応するMutexを取得します。
     * Mutexがまだ存在しない場合は、新しく生成し、cacheします。
     *
     * @return 参加者ごとの Mutex
     */
    fun get(id: ParticipantId): Mutex {
        return mutexes.computeIfAbsent(id) { Mutex() }
    }
}