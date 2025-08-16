package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.entity.Relative
import net.minecraft.world.phys.Vec3
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.koin.core.annotation.Scope

@Scope(value = GameComponents::class)
class DisableHittingBody(
    private val game: JinrouGame,
    private val playerProvider: BukkitPlayerProvider
) {

    private val _ghostingPlayers = arrayListOf<Int>()

    val ghostingPlayers: List<Int>
        get() = _ghostingPlayers

    fun ghost(player: CraftPlayer) {
        _ghostingPlayers.add(player.handle.id)

        val packet = ClientboundTeleportEntityPacket.teleport(
            player.handle.id,
            PositionMoveRotation(Vec3(0.0, -100.0, 0.0), Vec3.ZERO, 0.0F, 0.0F),
            Relative.union(Relative.DELTA, Relative.ROTATION),
            false
        )

        game.getCurrentParticipants()
            .filterNot { it.participantId.uniqueId == player.uniqueId }
            .mapNotNull { playerProvider.getAllowNull(it) }
            .forEach {
                it as CraftPlayer
                it.handle.connection.sendPacket(packet)
            }
    }
}