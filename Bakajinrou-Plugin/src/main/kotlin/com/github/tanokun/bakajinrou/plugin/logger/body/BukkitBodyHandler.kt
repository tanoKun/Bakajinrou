package com.github.tanokun.bakajinrou.plugin.logger.body

import com.github.tanokun.bakajinrou.game.logger.BodyHandler
import org.bukkit.Server
import java.util.*

class BukkitBodyHandler(
    private val server: Server
): BodyHandler {

    private val bodies = hashMapOf<UUID, BodyPacket>()

    override fun createBody(of: UUID) {

    }

    override fun deleteBodies() {
    }

    override fun showBodies(to: UUID) {
    }
}