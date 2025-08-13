package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.world.phys.Vec3
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class RerenderingTabHandler(
    plugin: Plugin, game: JinrouGame, playerProvider: BukkitPlayerProvider
): LifecycleEventListener(plugin, {
    register<PlayerJoinEvent> { event ->
        game.getCurrentParticipants()
            .filter(Participant::isDead)
            .mapNotNull { playerProvider.getAllowNull(it) }
            .forEach {
                it as CraftPlayer
                val packet = ClientboundAddEntityPacket(
                    it.handle.id, it.uniqueId, 0.0, -100.0, 0.0, 0f, 0f, it.handle.type, 0,
                    Vec3(0.0, 0.0, 0.0), 0.0
                )

                (event.player as CraftPlayer).handle.connection.sendPacket(packet)
            }
    }
})