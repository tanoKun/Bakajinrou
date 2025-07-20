package com.github.tanokun.bakajinrou.plugin

import com.github.tanokun.bakajinrou.plugin.logger.body.BodyPacket
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

open class BakaJinrou: JavaPlugin() {
    var bodyPacket: BodyPacket? = null

    override fun onEnable() {
        CommandAPICommand("spawn")
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                if (bodyPacket == null)
                    bodyPacket = BodyPacket(Bukkit.getServer(), sender).apply { sendBody(to = sender) }
                else
                    bodyPacket?.sendBody(to = sender)
            })
            .register()
    }
}