package com.github.tanokun.bakajinrou.plugin.rendering.tab.lobby.component

import com.github.tanokun.bakajinrou.api.translation.PrefixKeys
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.rendering.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEntryComponent
import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.level.GameType
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.text

class SpectatorInLobbyComponent(
    override val dummyUuid: DummyUUID,
    target: Player,
    gameType: GameType,
    private val translator: JinrouTranslator,
): TabEntryComponent {
    private val entry = run {
        val gameProfile = createGameProfile(target as CraftPlayer)

        val latency = target.ping

        val displayName = component {
            raw { translator.translate(PrefixKeys.SPECTATOR, target.locale()) }
            text(" ${target.name}")
        }

        return@run ClientboundPlayerInfoUpdatePacket.Entry(
            dummyUuid.uuid, gameProfile, true, latency, gameType, PaperAdventure.asVanilla(displayName), true, -100, null
        )
    }

    override fun toPacketEntry() = entry
}