package com.github.tanokun.bakajinrou.plugin.rendering.tab.authentication

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.plugin.Plugin

class TabListCensor(private val tabAuthenticator: TabAuthenticator, plugin: Plugin) : PacketAdapter(
    plugin, PacketType.Play.Server.PLAYER_INFO
) {

    override fun onPacketSending(event: PacketEvent) {
        val packet = event.packet
        val handle = packet.handle
        if (handle !is ClientboundPlayerInfoUpdatePacket) return

        if (tabAuthenticator.isAuthorizedPacket(handle)) return

        event.packet = event.packet.deepClone().apply {
            val actions = packet.playerInfoActions.read(0)
            this.playerInfoActions.write(0, actions - EnumWrappers.PlayerInfoAction.UPDATE_LISTED)
        }
    }
}