package com.github.tanokun.bakajinrou.plugin

import com.comphenix.protocol.ProtocolLibrary
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.finisher.CitizenSideFinisher
import com.github.tanokun.bakajinrou.plugin.finisher.FoxSideFinisher
import com.github.tanokun.bakajinrou.plugin.finisher.WolfSideFinisher
import com.github.tanokun.bakajinrou.plugin.listener.always.NonLifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.logger.JinrouLogger
import com.github.tanokun.bakajinrou.plugin.logger.body.BodyPacket
import com.github.tanokun.bakajinrou.plugin.logger.body.BukkitBodyHandler
import com.github.tanokun.bakajinrou.plugin.scheduler.JinrouGameScheduler
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.*
import com.github.tanokun.bakajinrou.plugin.setting.GamePlanner
import com.github.tanokun.bakajinrou.plugin.setting.factory.PositionAssigner
import com.github.tanokun.bakajinrou.plugin.setting.factory.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.map.GameMap
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

open class BakaJinrou: JavaPlugin() {
    var bodyPacket: BodyPacket? = null

    val positionAssigner = PositionAssigner(Random(0))

    val gamePlanner = GamePlanner(
        jinrouGameProvider = { JinrouGame(it, { CitizenSideFinisher(it) }, { WolfSideFinisher(it) }, { FoxSideFinisher(it) }) },
        loggerProvider = { JinrouLogger() },
        gameSchedulerProvider = { startTime, schedules, plugin -> JinrouGameScheduler(startTime, schedules, Bukkit.getScheduler(), plugin) },
        bodyHandlerProvider = { BukkitBodyHandler(Bukkit.getServer()) },
        positionAssigner = positionAssigner,
        gameLifecycleUI = GameLifecycleUI(::getPlayerByParticipant)
    )

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(NonLifecycleEventListener(), this)

        addQuartzRecipe()

        CommandAPICommand("test")
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                gamePlanner.candidates.clear()
                gamePlanner.candidates.addAll(Bukkit.getOnlinePlayers())

                val cache = PlayerNameCache()

                val gameMap = GameMap(sender.location, sender.location, 200, delayToGiveQuartz = 50.seconds)
                val selectedMap = SelectedMap(
                    gameMap,
                    TimeAnnouncer(::getPlayerByParticipant),
                    QuartzDistribute(::getPlayerByParticipant),
                    GrowingNotifier(::getPlayerByParticipant),
                    HiddenPositionAnnouncer(::getPlayerByParticipant, PlayerNameCache())
                )

                gamePlanner.selectedMap = selectedMap

                val (game, controller) = gamePlanner.createGame(this, cache, ProtocolLibrary.getProtocolManager())
                controller.launch()
                /*
                gamePlanner.selectedMap = GameMap(sender.location, sender.location, 200, delayToGiveQuartz = 50.seconds)

                val (game, controller) = gamePlanner.createGame(this, PlayerNameCache())
                controller.launch()

                game.participants.forEach {
                    it.grantMethod(AttackBySwordItem())
                    it.grantMethod(TotemProtectiveItem())
                    it.grantMethod(FakeTotemProtectiveItem())
                    it.grantMethod(AttackByArrow(controller))
                    it.grantMethod(AttackByDamagePotionEffect())
                    it.grantMethod(BowItem())
                }
*/
            })
            .register()
    }

    private fun addQuartzRecipe() {
        val recipeKey = NamespacedKey(this, "custom_end_crystal")
        val result = ItemStack(Material.END_CRYSTAL)
        val recipe = ShapedRecipe(recipeKey, result).apply {
            shape(
                "QQQ",
                "QQQ",
                "QQQ"
            )
            setIngredient('Q', Material.QUARTZ)
        }
        Bukkit.addRecipe(recipe)
    }

    fun getPlayerByParticipant(participant: Participant) = Bukkit.getPlayer(participant.uniqueId)
}