package com.github.tanokun.bakajinrou.plugin.setting

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.bukkit.controller.JinrouGameController
import com.github.tanokun.bakajinrou.bukkit.finishing.JinrouGameFinishDecider
import com.github.tanokun.bakajinrou.bukkit.logger.BodyHandler
import com.github.tanokun.bakajinrou.bukkit.logger.GameActionLogger
import com.github.tanokun.bakajinrou.bukkit.position.BukkitPlayerProviderByServer
import com.github.tanokun.bakajinrou.bukkit.scheduler.JinrouGameScheduler
import com.github.tanokun.bakajinrou.bukkit.scheduler.schedule.TimeSchedule
import com.github.tanokun.bakajinrou.plugin.cache.BukkitPlayerNameCache
import com.github.tanokun.bakajinrou.plugin.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.plugin.schedule.GameSchedules
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.random.Random

typealias FinishDeciderProvider = () -> JinrouGameFinishDecider
typealias LoggerProvider = () -> GameActionLogger
typealias GameSchedulerProvider = (Long, List<TimeSchedule>, Plugin) -> JinrouGameScheduler
typealias BodyHandlerProvider = () -> BodyHandler
typealias GameSchedulePlannerProvider = () -> GameSchedules

class GamePlanner(
    private val random: Random,
    private val finishDeciderProvider: FinishDeciderProvider,
    private val loggerProvider: LoggerProvider,
    private val gameSchedulerProvider: GameSchedulerProvider,
    private val bodyHandlerProvider: BodyHandlerProvider,
    private val gameSchedulePlanner: GameSchedulePlannerProvider,
) {
    var selectedMap: GameMap? = null

    val candidates: MutableSet<Player> = mutableSetOf()

    val amountOfPosition =
        hashMapOf<Positions, Int>(
            Positions.WOLF to 3,
            Positions.MADMAN to 2,
            Positions.FOX to 1,
            Positions.IDIOT to 3,
            Positions.FORTUNE to 1,
            Positions.MEDIUM to 1,
            Positions.KNIGHTS to 1
        )

    fun createGame(plugin: Plugin, bukkitPlayerNameCache: BukkitPlayerNameCache): JinrouGameController {
        val selectedMap = selectedMap ?: throw IllegalStateException("マップが選択されていません。")

        if (getMinimumRequired() > candidates.size) throw IllegalStateException("現在の参加人数では、選択されている役職が多すぎます。")
        val jinrouGame = JinrouGame(participants = placePositions())

        val finishDecider = finishDeciderProvider()
        val logger = loggerProvider()
        val scheduler = gameSchedulerProvider(selectedMap.startTime, selectedMap.createSchedules(gameSchedulePlanner(), bukkitPlayerNameCache), plugin)
        val bodyHandler = bodyHandlerProvider()

        return JinrouGameController(jinrouGame, finishDecider, logger, scheduler, bodyHandler)
    }

    private fun placePositions(): List<Participant> {
        val participants = arrayListOf<Participant>()
        val shuffled = candidates.shuffled(random)

        var index = 0
        amountOfPosition.forEach { positionType, required ->
            repeat(required) {
                val position = positionType.candidatePositions.random(random)

                val uniqueId = shuffled[index].uniqueId
                val getBukkitPlayer = BukkitPlayerProviderByServer(uniqueId)

                participants.add(Participant(uniqueId, position, getBukkitPlayer))

                index++
            }
        }

        for (index in index..shuffled.lastIndex) {
            val uniqueId = shuffled[index].uniqueId
            val getBukkitPlayer = BukkitPlayerProviderByServer(uniqueId)

            participants.add(Participant(uniqueId, CitizenPosition, getBukkitPlayer))
        }

        return participants
    }

    private fun getMinimumRequired(): Int = amountOfPosition.values.sum()
}