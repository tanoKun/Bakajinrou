package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.participant.Participant
import java.util.*

/**
 * 行動を行うために必要な 手段 を表します。
 * 手段 には振る舞いが存在し、実際の処理を行います。
 */
interface GrantedMethod {
    val uniqueId: UUID

    /**
     * この手段を消費したときに呼び出されます。
     * 呼び出し時点で、手段が剝奪されているとは限りません。
     */
    fun onConsume(consumer: Participant)
}