package com.github.tanokun.bakajinrou.plugin.common.formatter 
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.excludeSpectators
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.FormatKeys
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.*

class ParticipantsFormatterTest : FunSpec({
    val participantKey1: FormatKeys.Participant = mockk()
    val participantKey2: FormatKeys.Participant = mockk()
    val participantKey3: FormatKeys.Participant = mockk()

    val categoryKey: FormatKeys.Category = mockk()

    val p1: Participant = mockk {
        every { position } returns mockk()
    }
    val p2: Participant = mockk {
        every { position } returns mockk()
    }
    val p3: Participant = mockk {
        every { position } returns mockk()
    }

    val filter1 = { p: Participant -> p === p1 } to participantKey1
    val filter2 = { p: Participant -> p === p2 } to participantKey2
    val filter3 = { p: Participant -> p === p3 } to participantKey3

    val pList = listOf(p1, p2, p3).excludeSpectators()

    val miniMessage = MiniMessage.miniMessage()

    val translator: JinrouTranslator = mockk {
        every { translate(participantKey1, Locale.JAPAN, any()) } returns Component.text("p1")
        every { translate(participantKey2, Locale.JAPAN, any()) } returns Component.text("p2")
        every { translate(participantKey3, Locale.JAPAN, any()) } returns Component.text("p3")
        every { translate(categoryKey, Locale.JAPAN) } returns Component.text("c")
    }

    val formatter = ParticipantsFormatter(pList, translator)

    beforeEach {
        mockkObject(PlayerNameCache)
        every { PlayerNameCache.get(any<Participant>()) } returns ""
    }

    test("表示する参加者が0人") {
        val result = formatter.format(Locale.JAPAN, categoryKey, { _: Participant -> false } to participantKey1)
        miniMessage.serialize(result) shouldBe "c<br>"
    }

    test("表示する参加者が1人") {
        val result = formatter.format(Locale.JAPAN, categoryKey, filter1)
        miniMessage.serialize(result) shouldBe "c<br>  p1"
    }

    test("表示する参加者が2人") {
        val result = formatter.format(Locale.JAPAN, categoryKey, filter1,  filter2)
        miniMessage.serialize(result) shouldBe "c<br>  p1<gray>, </gray>p2"
    }

    test("表示する参加者が3人かつ、順番が異なる") {
        val result = formatter.format(Locale.JAPAN, categoryKey, { p: Participant -> filter1.first(p) || filter3.first(p) } to participantKey1, filter2)
        miniMessage.serialize(result) shouldBe "c<br>  p1<gray>, </gray>p1<gray>, </gray>p2"
    }
})