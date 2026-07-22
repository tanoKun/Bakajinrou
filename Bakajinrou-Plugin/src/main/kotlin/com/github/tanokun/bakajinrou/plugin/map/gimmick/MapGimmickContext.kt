package com.github.tanokun.bakajinrou.plugin.map.gimmick

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.audience.GameAudienceStore
import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.game.viewer.GameViewers
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.map.GameMap
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [MapGimmickContext::class])
@Scope(value = GameComponents::class)
class MapGimmickContext(
    val game: GameStore,
    private val audience: GameAudienceStore,
    private val playerProvider: BukkitPlayerProvider,
    private val gameMap: GameMap,
) {
    fun participant(player: Player): Participant? = game.getParticipant(player.uniqueId.asParticipantId())

    fun broadcast(message: Component) {
        GameViewers(game.current, audience.current).all.forEach { viewer ->
            playerProvider.getAllowNull(viewer.playerId)?.sendMessage(message)
        }
    }

    fun quartzCount(player: Player): Int = player.inventory.contents
        .filterNotNull()
        .filter { it.type == Material.QUARTZ }
        .sumOf(ItemStack::getAmount)

    fun consumeQuartz(player: Player, amount: Int): Boolean {
        if (quartzCount(player) < amount) return false

        player.inventory.removeItem(ItemStack(Material.QUARTZ, amount))
        return true
    }

    fun markers(tag: String): List<ArmorStand> {
        val world = Bukkit.getWorld(gameMap.spawnPoint.worldName) ?: return emptyList()
        return world.getEntitiesByClass(ArmorStand::class.java).filter { tag in it.scoreboardTags }
    }

    fun markerLocation(tag: String, legacy: Location): Location =
        markers(tag).firstOrNull()?.location ?: legacy

    fun legacyLocation(x: Double, y: Double, z: Double): Location {
        val world = Bukkit.getWorld(gameMap.spawnPoint.worldName) ?: Bukkit.getWorlds().first()
        return Location(world, x, y, z)
    }
}
