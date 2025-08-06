package com.github.tanokun.bakajinrou.api.participant.position

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ability.AbilityResultSource
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource

interface Position {
    val prefixSource: PrefixSource

    val abilityResult: AbilityResultSource

    /**
     * この役職が持つ固有の手段の一覧を返します。
     * 初期状態で必ず与えられる手段を表します。
     *
     * @param game ハンドラーとなるゲーム
     *
     * @return この役職に固有の GrantedMethod のリスト
     */
    fun inherentMethods(game: JinrouGame): List<GrantedMethod> = arrayListOf()
}