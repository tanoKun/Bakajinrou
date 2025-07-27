package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.participant.Participant
import java.util.*

interface GrantedMethod {
    val uniqueId: UUID

    /**
     * この手段を消費したときに呼び出されます。
     */
    fun onConsume(consumer: Participant)
}