package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body.name

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.world.entity.Display.TextDisplay
import net.minecraft.world.entity.EntityType
import org.bukkit.Color
import org.bukkit.Server
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.entity.CraftTextDisplay
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Player
import java.util.UUID

internal class BodyNamePacket(server: Server, body: Player) {
    private val dummy = TextDisplay(EntityType.TEXT_DISPLAY, (body.world as CraftWorld).handle).apply {
        uuid = UUID.randomUUID()
        setPos(body.location.x, body.location.y + NAME_HEIGHT, body.location.z)
    }

    init {
        CraftTextDisplay(server as CraftServer, dummy).apply {
            text(Component.text(body.name, NamedTextColor.WHITE))
            billboard = Billboard.CENTER
            isShadowed = true
            backgroundColor = Color.fromARGB(0, 0, 0, 0)
            isSeeThrough = false
        }
    }

    val entityId: Int
        get() = dummy.id

    fun show(to: Player) {
        val connection = (to as CraftPlayer).handle.connection

        connection.send(createAddEntityPacket())
        connection.send(ClientboundSetEntityDataPacket(dummy.id, dummy.entityData.packAll()))
    }

    fun hide(to: Player) {
        (to as CraftPlayer).handle.connection.send(ClientboundRemoveEntitiesPacket(dummy.id))
    }

    private fun createAddEntityPacket() = ClientboundAddEntityPacket(
        dummy.id,
        dummy.uuid,
        dummy.x,
        dummy.y,
        dummy.z,
        dummy.xRot,
        dummy.yRot,
        EntityType.TEXT_DISPLAY,
        0,
        dummy.deltaMovement,
        dummy.yHeadRot.toDouble()
    )

    private companion object {
        const val NAME_HEIGHT = 0.8
    }
}
