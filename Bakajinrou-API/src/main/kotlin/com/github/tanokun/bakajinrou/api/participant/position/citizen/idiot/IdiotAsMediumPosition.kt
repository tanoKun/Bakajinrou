package com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ability.medium.FakeMediumAbility
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys

object IdiotAsMediumPosition: IdiotPosition(
    realKey = TranslationKeys.Prefix.Citizens.Mystic.MEDIUM,
    idiotKey = TranslationKeys.Prefix.Citizens.Idiot.MEDIUM
) {
    override fun inherentMethods(game: JinrouGame): List<GrantedMethod> {
        return listOf(FakeMediumAbility(game))
    }
}