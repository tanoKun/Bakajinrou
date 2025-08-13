package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.koin.core.annotation.Scope

@Scope(value = GameComponents::class)
class DisableHittingBody(
    private val game: JinrouGame,
    private val playerProvider: BukkitPlayerProvider
) {

    fun ghost(player: CraftPlayer) {
        val buf = createTeleportEntityPacketBuffer(player.handle.id, player.location.set(0.0, -100.0, 0.0))
        val packet = ClientboundTeleportEntityPacket.STREAM_CODEC.decode(buf)
        game.getCurrentParticipants()
            .filterNot { it.participantId.uniqueId == player.uniqueId }
            .mapNotNull { playerProvider.getAllowNull(it) }
            .forEach {
                it as CraftPlayer
                it.handle.connection.sendPacket(packet)
            }
    }


    private fun createTeleportEntityPacketBuffer(entityId: Int, location: Location): FriendlyByteBuf {
        val byteBuf = Unpooled.buffer()

        val friendlyByteBuf = FriendlyByteBuf(byteBuf)

        friendlyByteBuf.writeVarInt(entityId)
        friendlyByteBuf.writeDouble(location.x)
        friendlyByteBuf.writeDouble(location.y)
        friendlyByteBuf.writeDouble(location.z)
        val yRot = (location.yaw * 256.0f / 360.0f).toInt().toByte()
        friendlyByteBuf.writeByte(yRot.toInt())
        val xRot = (location.pitch * 256.0f / 360.0f).toInt().toByte()
        friendlyByteBuf.writeByte(xRot.toInt())
        friendlyByteBuf.writeBoolean(false)

        return friendlyByteBuf
    }
}