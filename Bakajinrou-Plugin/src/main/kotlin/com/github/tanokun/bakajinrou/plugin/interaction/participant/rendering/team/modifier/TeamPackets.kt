package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.team.modifier

import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Scoreboard
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

class TeamPackets(teamName: String, players: List<String>, dsl: PlayerTeam.() -> Unit) {
    private val createTeamPacket: ClientboundSetPlayerTeamPacket

    private val joinTeamPacket: ClientboundSetPlayerTeamPacket

    init {
        val dummyTeam = PlayerTeam(Scoreboard(), "team_$teamName").apply(dsl)

        createTeamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(dummyTeam, true)

        joinTeamPacket = ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(
            dummyTeam,
            players,
            ClientboundSetPlayerTeamPacket.Action.ADD
        )
    }

    fun sendPackets(player: Player) {
        player as CraftPlayer

        player.handle.connection.sendPacket(createTeamPacket)
        player.handle.connection.sendPacket(joinTeamPacket)
    }
}