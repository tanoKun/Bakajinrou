package com.github.tanokun.bakajinrou.plugin.rendering.tab

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.entity.Player

interface TabEntryComponent {
    val dummyUuid: DummyUUID

    fun toPacketEntry(viewer: Player): ClientboundPlayerInfoUpdatePacket.Entry
}