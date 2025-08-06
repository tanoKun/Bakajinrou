package com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ability.fortune.FakeFortuneAbility
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys

object IdiotAsFortunePosition: IdiotPosition(
    realKey = TranslationKeys.Prefix.Citizens.Mystic.FORTUNE,
    idiotKey = TranslationKeys.Prefix.Citizens.Idiot.FORTUNE
) {
    override fun inherentMethods(game: JinrouGame): List<GrantedMethod> {
        return listOf(FakeFortuneAbility(game))
    }
}