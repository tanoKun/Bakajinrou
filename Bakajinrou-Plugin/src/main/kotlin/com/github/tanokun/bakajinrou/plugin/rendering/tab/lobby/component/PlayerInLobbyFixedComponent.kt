package com.github.tanokun.bakajinrou.plugin.rendering.tab.lobby.component

import com.github.tanokun.bakajinrou.plugin.rendering.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEntryComponent
import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.level.GameType
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.text

class PlayerInLobbyFixedComponent(
    override val dummyUuid: DummyUUID,
    target: Player,
    gameType: GameType
): TabEntryComponent {
    private val entry = run {
        val gameProfile = (target as CraftPlayer).profile

        val latency = target.ping

        val displayName = component {
            text(target.name) color gray
        }

        ClientboundPlayerInfoUpdatePacket.Entry(
            dummyUuid.uuid, gameProfile, true, latency, gameType, PaperAdventure.asVanilla(displayName), true, 0, null
        )
    }

    override fun toPacketEntry(viewer: Player) = entry
}