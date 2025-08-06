package com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ability.fortune.CorrectFortuneAbility
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys

object FortunePosition: MysticPosition() {
    override val prefixSource: PrefixSource = DefaultPrefix(TranslationKeys.Prefix.Citizens.Mystic.FORTUNE)

    override fun inherentMethods(game: JinrouGame): List<GrantedMethod> {
        return listOf(CorrectFortuneAbility(game))
    }
}