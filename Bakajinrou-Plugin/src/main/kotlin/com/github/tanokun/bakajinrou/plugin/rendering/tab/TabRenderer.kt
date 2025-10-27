package com.github.tanokun.bakajinrou.plugin.rendering.tab

import com.github.tanokun.bakajinrou.plugin.rendering.tab.authentication.TabAuthenticator
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

class TabRenderer(private val authenticator: TabAuthenticator, private val receiver: Player) {

    fun renderUpdate(components: Collection<TabEntryComponent>) {
        render(components, EnumSet.of(
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_HAT,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LIST_ORDER
        ))
    }

    fun renderInitialization(components: Collection<TabEntryComponent>) {
        render(components, EnumSet.of(
            ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_HAT,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LIST_ORDER
        ))
    }

    fun renderRemoval(uuids: List<UUID>) {
        val packet = ClientboundPlayerInfoRemovePacket(uuids)

        receiver as CraftPlayer
        receiver.handle.connection.send(packet)
    }

    private fun render(components: Collection<TabEntryComponent>, actions: EnumSet<ClientboundPlayerInfoUpdatePacket.Action>) {
        if (components.isEmpty()) return

        val entries = components.map { it.toPacketEntry() }
        val packet = ClientboundPlayerInfoUpdatePacket(actions, entries)

        authenticator.approvePacket(packet)

        receiver as CraftPlayer
        receiver.handle.connection.send(packet)
    }
}