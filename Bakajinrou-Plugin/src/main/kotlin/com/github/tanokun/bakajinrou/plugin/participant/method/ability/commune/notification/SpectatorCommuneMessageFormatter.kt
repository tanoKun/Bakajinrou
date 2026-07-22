package com.github.tanokun.bakajinrou.plugin.participant.method.ability.commune.notification

import com.github.tanokun.bakajinrou.game.ability.medium.CommuneResult
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.DisplayLoggingKeys
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import net.kyori.adventure.text.Component
import java.util.Locale

internal class SpectatorCommuneMessageFormatter(
    private val translator: JinrouTranslator,
) {
    fun format(
        result: CommuneResult.Success,
        mediumName: String,
        targetName: String,
        locale: Locale,
    ): Component {
        val targetNameComponent = Component.text(targetName)
        val resultComponent = when (result) {
            is CommuneResult.FoundResult -> translator.translate(result.source.resultKey, locale)
            is CommuneResult.IsNotDead -> translator.translate(
                GameKeys.Ability.Using.COMMUNE_FAILURE_MESSAGE,
                locale,
                targetNameComponent,
            )
        }

        return translator.translate(
            DisplayLoggingKeys.Use.COMMUNE,
            locale,
            Component.text(mediumName),
            targetNameComponent,
            resultComponent,
        )
    }
}
