package com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.component

import com.github.tanokun.bakajinrou.plugin.rendering.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEntryComponent
import com.mojang.authlib.GameProfile
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.level.GameType
import org.bukkit.entity.Player
import java.util.*

class OfflineSurvivorComponent(
    override val dummyUuid: DummyUUID,
    name: String,
    uniqueId: UUID,
): TabEntryComponent {
    private val entry = run {
        return@run ClientboundPlayerInfoUpdatePacket.Entry(
            dummyUuid.uuid, GameProfile(uniqueId, name), true, 0, GameType.SURVIVAL,
            PaperAdventure.asVanilla(Component.text(name)), true, 0, null
        )
    }

    override fun toPacketEntry(viewer: Player) = entry
}