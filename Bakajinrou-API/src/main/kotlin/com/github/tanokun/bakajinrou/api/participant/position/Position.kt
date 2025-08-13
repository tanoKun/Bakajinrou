package com.github.tanokun.bakajinrou.api.participant.position

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.method.InitialMethod
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource

interface Position {
    val prefixSource: PrefixSource

    val abilityResult: ResultSource

    /**
     * この役職が持つ固有の手段の一覧を返します。
     * 初期状態で必ず与えられる手段を表します。
     *
     * @return この役職に固有の InitialMethod のリスト
     */
    fun inherentMethods(): List<InitialMethod> = arrayListOf()
}