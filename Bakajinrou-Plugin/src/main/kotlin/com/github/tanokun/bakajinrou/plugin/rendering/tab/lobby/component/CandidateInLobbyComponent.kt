package com.github.tanokun.bakajinrou.plugin.rendering.tab.lobby.component

import com.github.tanokun.bakajinrou.plugin.rendering.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEntryComponent
import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.level.GameType
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

class CandidateInLobbyComponent(
    override val dummyUuid: DummyUUID,
    target: Player,
    gameType: GameType
): TabEntryComponent {
    private val entry = run {
        val gameProfile = createGameProfile(target as CraftPlayer)

        val latency = target.ping

        ClientboundPlayerInfoUpdatePacket.Entry(
            dummyUuid.uuid, gameProfile, true, latency, gameType, PaperAdventure.asVanilla(target.displayName()), true, 0, null
        )
    }

    override fun toPacketEntry() = entry
}