package com.github.tanokun.bakajinrou.plugin.setting.builder.game

import com.comphenix.protocol.ProtocolManager
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.StrategyIntegrity
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.plugin.participant.ParticipantStrategy
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.participant.position.other.SpectatorOtherPosition
import com.github.tanokun.bakajinrou.plugin.setting.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.builder.ParticipantBuilder
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import java.util.*
import kotlin.random.Random

class GameBuilder(
    random: Random,
    plugin: Plugin,
    server: Server,
    bukkitScheduler: BukkitScheduler,
    protocolManager: ProtocolManager
) {
    private val contexts = DIContext().apply {
        register(random)
        register(plugin)
        register(server)
        register(bukkitScheduler)
        register(protocolManager)
    }

    private var participants: ParticipantScope.All = listOf<Participant>().all()

    fun setGameMap(selected: SelectedMap): ParticipantAssigner {
        contexts.apply {
            register(selected)
            register(selected.gameMap)
        }

        return ParticipantAssignerImpl()
    }

    fun injection(dsl: DIContext.() -> Unit): GameBuilder {
        dsl(contexts)

        return this
    }

    private inner class ParticipantAssignerImpl: ParticipantAssigner
    {
        override fun assignParticipants(
            assignment: HashMap<Positions, Int>,
            candidates: Set<UUID>,
            integrity: StrategyIntegrity,
            dsl: (assigner: ParticipantBuilder) -> ParticipantScope.NonSpectators
        ): SpectatorAssigner {
            val participantBuilder = ParticipantBuilder(assignment, candidates, { ParticipantStrategy(it, integrity) }, contexts.get())
            val participants = dsl(participantBuilder)

            this@GameBuilder.participants = participants.all()

            contexts.apply {
                register(integrity)
                register<ParticipantScope.NonSpectators>(participants)
            }

            return SpectatorAssignerImpl()
        }
    }

    private inner class SpectatorAssignerImpl(): SpectatorAssigner {
        override fun assignSpectators(
            candidates: Set<UUID>,
        ): CreateLineBuilderDsl {
            val spectators = candidates.map {
                Participant(it, SpectatorOtherPosition, ParticipantStrategy(it, contexts.get()))
            }

            this@GameBuilder.participants = (this@GameBuilder.participants + spectators).all()

            contexts.register<ParticipantScope.All>(this@GameBuilder.participants)

            return CreateLineBuilderDsl(contexts)
        }
    }
}

interface ParticipantAssigner {
    fun assignParticipants(
        assignment: HashMap<Positions, Int>,
        candidates: Set<UUID>,
        integrity: StrategyIntegrity,
        dsl: (ParticipantBuilder) -> ParticipantScope.NonSpectators
    ): SpectatorAssigner
}

interface SpectatorAssigner {
    fun assignSpectators(candidates: Set<UUID>): CreateLineBuilderDsl
}

fun gameBuilder(
    random: Random = Random,
    plugin: Plugin,
    server: Server = Bukkit.getServer(),
    bukkitScheduler: BukkitScheduler = server.scheduler,
    protocolManager: ProtocolManager
) = GameBuilder(random, plugin, server, bukkitScheduler, protocolManager)