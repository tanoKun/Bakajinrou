package com.github.tanokun.bakajinrou.plugin.rendering.tab.authentication

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TabAuthenticator {
    private val approvedPackets = arrayListOf<ClientboundPlayerInfoUpdatePacket>()

    fun approvePacket(packet: ClientboundPlayerInfoUpdatePacket) = approvedPackets.add(packet)

    /**
     * 許可されたパケットであればtrueを返し、リストから削除する。
     *
     * @return 許可されていた場合true、そうでなければfalse
     */
    fun isAuthorizedPacket(packet: ClientboundPlayerInfoUpdatePacket) = approvedPackets.removeAll { it === packet }
}