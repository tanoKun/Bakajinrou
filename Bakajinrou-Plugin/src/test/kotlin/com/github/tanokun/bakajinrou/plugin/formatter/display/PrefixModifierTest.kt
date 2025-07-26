package com.github.tanokun.bakajinrou.plugin.formatter.display

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.CitizenPosition
import io.mockk.every
import io.mockk.mockk
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PrefixModifierTest {
    private val serializer = LegacyComponentSerializer.legacySection()

    @Test
    @DisplayName("表示するプレフィックスが存在しない")
    fun nonPrefixTest() {
        val viewer = mockk<Participant>()
        val target = mockk<Participant> {
            every { resolvePrefix(viewer) } returns null
            every { position } returns mockk<CitizenPosition>()
        }

        val modifier = PrefixModifier(target)

        val result = modifier.createPrefix(viewer)

        assertEquals("", serializer.serialize(result))
    }

    @Test
    @DisplayName("表示するプレフィックスが、カミングアウトのみ")
    fun onlyComingOutPrefixTest() {
        val viewer = mockk<Participant>()
        val target = mockk<Participant> {
            every { resolvePrefix(viewer) } returns null
            every { position } returns mockk<CitizenPosition>()
        }

        val modifier = PrefixModifier(target).apply {
            comingOut = ComingOut.LAST_WOLF
        }

        val result = modifier.createPrefix(viewer)

        assertEquals("§7[§4ラスト人狼§7]", serializer.serialize(result))
    }

    @Test
    @DisplayName("表示するプレフィックスが、カミングアウトと明かされたプレフィックス")
    fun bothComingOutAndResolvedPrefixTest() {
        val viewer = mockk<Participant>()
        val target = mockk<Participant> {
            every { resolvePrefix(viewer) } returns "狂人"
            every { position } returns mockk<MadmanPosition>()
        }

        val modifier = PrefixModifier(target).apply {
            comingOut = ComingOut.FORTUNE
        }

        val result = modifier.createPrefix(viewer)

        assertEquals("§7[§c狂人§7, §x§8§7§c§e§f§a占い師§7]", serializer.serialize(result))
    }
}