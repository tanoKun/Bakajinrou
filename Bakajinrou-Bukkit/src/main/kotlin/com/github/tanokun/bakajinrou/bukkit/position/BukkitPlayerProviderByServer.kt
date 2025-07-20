package com.github.tanokun.bakajinrou.bukkit.position

import com.github.tanokun.bakajinrou.api.participant.BukkitPlayerProvider
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class BukkitPlayerProviderByServer(private val uuid: UUID): BukkitPlayerProvider {
    override fun invoke(): Player? = Bukkit.getPlayer(uuid)
}