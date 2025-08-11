package com.github.tanokun.bakajinrou.plugin.common.setting

import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.map.GameMap
import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.BindingListeners
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponentSession
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.AbilityUsersAssigner.Companion.assignAbilityUsers
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.CitizenAssigner.Companion.assignCitizens
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.FoxAssigner.Companion.assignFox
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.IdiotAssigner.Companion.assignIdiots
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.MadmanAssigner.Companion.assignMadmans
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.WolfAssigner.Companion.assignWolfs
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.bukkit.plugin.Plugin
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin
import java.util.*

class GameSettings(private val plugin: Plugin) {
    private val _candidates = hashSetOf<UUID>()

    private val _spectators = hashSetOf<UUID>()

    val candidates: Set<UUID>
        get() = _candidates

    val spectators: Set<UUID>
        get() = _spectators

    private val selectedPositions =
        hashMapOf(
            RequestedPositions.WOLF to 3,
            RequestedPositions.MADMAN to 1,
            RequestedPositions.IDIOT to 3,
            RequestedPositions.FORTUNE to 1,
            RequestedPositions.MEDIUM to 1,
            RequestedPositions.KNIGHT to 1,
            RequestedPositions.FOX to 1
        )

    var selectedMap: GameMap? = null

    fun getAmountBy(positions: RequestedPositions): Int {
        return selectedPositions[positions] ?: 0
    }


    fun updateAmount(positions: RequestedPositions, amount: Int) {
        if (amount < 0) throw IllegalArgumentException("予約役職を、0未満にはできません。")

        selectedPositions[positions] = amount
    }

    fun increase(positions: RequestedPositions) { updateAmount(positions, getAmountBy(positions) + 1) }

    fun decrease(positions: RequestedPositions) { updateAmount(positions, getAmountBy(positions) - 1) }

    fun addCandidate(uuid: UUID) {
        removeSpectator(uuid)

        _candidates.add(uuid)
    }

    fun addSpectator(uuid: UUID) {
        removeCandidate(uuid)

        _spectators.add(uuid)
    }

    fun removeCandidate(uuid: UUID) = _candidates.remove(uuid)

    fun removeSpectator(uuid: UUID) = _spectators.remove(uuid)

    fun buildGameSession(translator: JinrouTranslator): GameBuildResult {
        val selectedMap = selectedMap ?: return GameBuildResult.NotFoundSettingMap
        if (selectedPositions.values.sum() > candidates.size) return GameBuildResult.IllegalSelectedPositions

        val scope = getKoin().createScope<GameComponentSession>("name-${UUID.randomUUID()}").apply {
            declare(this)
            declare(translator)
            declare(selectedMap)

            val builder = get<ParticipantBuilder> { parametersOf(selectedPositions, candidates) }

            val participants = builder.assignMadmans()
                .assignWolfs(true)
                .assignIdiots(IdiotAsFortunePosition, IdiotAsMediumPosition, IdiotAsKnightPosition)
                .assignAbilityUsers()
                .assignFox()
                .assignCitizens() + createSpectators()

            get<JinrouGameSession> { parametersOf(participants.all(), plugin.scope, selectedMap.startTime) }

            get<BindingListeners>().apply { registerAll() }
            getAll<Observer>()

            plugin.scope.launch {
                get<GameScheduler>().observe(plugin.scope)
                    .filterIsInstance<ScheduleState.Cancelled>()
                    .collect {
                        this@apply.close()
                    }
            }
        }

        return GameBuildResult.SucceedCreation(scope.get(), scope.get())
    }

    private fun createSpectators() = spectators.map { Participant(it.asParticipantId(), SpectatorPosition, GrantedStrategy(mapOf())) }
}

sealed interface GameBuildResult {
    object NotFoundSettingMap: GameBuildResult
    object IllegalSelectedPositions: GameBuildResult
    class SucceedCreation(val game: JinrouGame, val gameSession: JinrouGameSession): GameBuildResult
}

