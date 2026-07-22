package com.github.tanokun.bakajinrou.plugin.participant.method.ability.commune.notification

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.ability.medium.CommuneResult
import com.github.tanokun.bakajinrou.plugin.common.formatter.ColorPallet
import com.github.tanokun.bakajinrou.plugin.localization.Dictionary
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.DisplayLoggingKeys
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Locale
import java.util.UUID

class SpectatorCommuneMessageFormatterTest {
    @Test
    fun `霊媒成功の観戦ログに未置換の引数を残さない`() {
        val formatter = SpectatorCommuneMessageFormatter(translator())
        val result = CommuneResult.FoundResult(
            source = ResultSource.CITIZENS,
            mediumId = UUID.randomUUID().asParticipantId(),
            targetId = UUID.randomUUID().asParticipantId(),
        )

        val message = formatter.format(result, "霊媒師", "対象者", Locale.JAPAN)

        assertEquals(
            "霊媒: 霊媒師 -> 対象者 (村人)",
            PlainTextComponentSerializer.plainText().serialize(message),
        )
    }

    @Test
    fun `霊媒失敗の観戦ログに未置換の引数を残さない`() {
        val formatter = SpectatorCommuneMessageFormatter(translator())
        val result = CommuneResult.IsNotDead(
            mediumId = UUID.randomUUID().asParticipantId(),
            targetId = UUID.randomUUID().asParticipantId(),
        )

        val message = formatter.format(result, "霊媒師", "対象者", Locale.JAPAN)

        assertEquals(
            "霊媒: 霊媒師 -> 対象者 (対象者は死亡していないため失敗)",
            PlainTextComponentSerializer.plainText().serialize(message),
        )
    }

    private fun translator() = JinrouTranslator(
        dictionaries = mapOf(
            Locale.JAPAN to Dictionary(
                mapOf(
                    DisplayLoggingKeys.Use.COMMUNE.key to "霊媒: <arg-0> -> <arg-1> (<arg-2>)",
                    GameKeys.Ability.Using.COMMUNE_FAILURE_MESSAGE.key to "<arg-0>は死亡していないため失敗",
                    ResultSource.CITIZENS.resultKey.key to "村人",
                )
            )
        ),
        colorPallet = ColorPallet(hashMapOf()),
    )
}
