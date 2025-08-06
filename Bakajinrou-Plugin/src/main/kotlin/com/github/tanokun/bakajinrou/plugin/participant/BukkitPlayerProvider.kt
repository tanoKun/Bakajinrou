package com.github.tanokun.bakajinrou.plugin.participant

import com.github.tanokun.bakajinrou.api.participant.Participant
import org.bukkit.Bukkit
import java.util.*

object BukkitPlayerProvider {
    fun get(participant: Participant) = Bukkit.getPlayer(participant.uniqueId)

    fun get(uniqueId: UUID) = Bukkit.getPlayer(uniqueId)
}