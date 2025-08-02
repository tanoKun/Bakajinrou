package com.github.tanokun.bakajinrou.plugin.logger.body

import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.protocol.game.*
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Pose
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Scoreboard
import net.minecraft.world.scores.Team.Visibility
import org.bukkit.Server
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*


class BodyPacket(server: Server, body: Player) {
    private val randomUuid = UUID.randomUUID()

    private val dummy = ServerPlayer(
        (server as CraftServer).server, (body.world as CraftWorld).handle, (body as CraftPlayer).profile, body.handle.clientInformation()
    ).apply {
        this.uuid = randomUuid
        this.setPos(body.location.x, body.location.y, body.location.z)
        yRot = body.location.yaw
        xRot = body.location.pitch

        this.pose = Pose.SWIMMING
    }

    fun sendBody(to: Player) {
        to as CraftPlayer

        val connection = to.handle.connection
        dummy.connection = connection

        val playerInfoPacket = ClientboundPlayerInfoUpdatePacket.createSinglePlayerInitializing(dummy, false)
        connection.send(playerInfoPacket)

        val addEntityPacket = createAddEntityPacket(dummy)
        connection.send(addEntityPacket)

        val dataPacket = ClientboundSetEntityDataPacket(dummy.id, dummy.entityData.packAll())
        connection.send(dataPacket)

        hiddenNameTag(to.handle)
    }

    fun remove(to: Player) {
        to as CraftPlayer

        val connection = to.handle.connection

        val removeEntitiesPacket = ClientboundRemoveEntitiesPacket(dummy.id)
        connection.send(removeEntitiesPacket)
    }


    private fun hiddenNameTag(to: ServerPlayer) {
        val dummyTeam = PlayerTeam(Scoreboard(), "hidden_team_${dummy.id}").apply {
            setNameTagVisibility(Visibility.NEVER)
            setColor(ChatFormatting.RESET)
        }

        val createTeamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(dummyTeam, true)
        to.connection.send(createTeamPacket)

        val joinTeamPacket = ClientboundSetPlayerTeamPacket.createPlayerPacket(dummyTeam, dummy.gameProfile.name, ClientboundSetPlayerTeamPacket.Action.ADD)
        to.connection.send(joinTeamPacket)
    }

    private fun createAddEntityPacket(dummy: ServerPlayer) = ClientboundAddEntityPacket(
        dummy.id, randomUuid, dummy.x, dummy.y, dummy.z, dummy.xRot, dummy.yRot,
        EntityType.PLAYER,
        BuiltInRegistries.ENTITY_TYPE.getId(EntityType.PLAYER),
        dummy.deltaMovement,
        dummy.yHeadRot.toDouble()
    )
}