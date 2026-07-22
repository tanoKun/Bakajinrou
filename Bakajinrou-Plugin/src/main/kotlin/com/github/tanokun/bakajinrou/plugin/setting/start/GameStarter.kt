package com.github.tanokun.bakajinrou.plugin.setting.start

import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.player.asPlayerId
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.game.audience.GameAudience
import com.github.tanokun.bakajinrou.game.audience.GameViewers
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.BindingListeners
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.AbilityUsersAssigner.Companion.assignAbilityUsers
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.CitizenAssigner.Companion.assignCitizens
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.FoxAssigner.Companion.assignFox
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.IdiotAssigner.Companion.assignIdiots
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.MadmanAssigner.Companion.assignMadmans
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.WolfAssigner.Companion.assignWolfs
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedPositions
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Single
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin
import java.util.*

@Single
class GameStarter(
    private val translator: JinrouTranslator,
    private val plugin: Plugin
) {
    fun buildGameSession(
        selectedMap: SelectedMap,
        selectedParticipants: SelectedParticipants,
        selectedPositions: SelectedPositions,
    ): GameBuildResult {
        if (selectedPositions.totalCount() > selectedParticipants.participants.size) return GameBuildResult.Failure("役職配布が過剰です。")

        val candidates = selectedParticipants.participants

        val scope = getKoin().createScope<GameComponents>("name-${UUID.randomUUID()}").apply {
            declare(this)
            declare(translator)
            declare(selectedMap.map)

            val builder = get<ParticipantBuilder> { parametersOf(selectedPositions, candidates) }

            val participants = builder.assignMadmans()
                .assignWolfs(true)
                .assignIdiots(IdiotAsFortunePosition, IdiotAsMediumPosition, IdiotAsKnightPosition)
                .assignAbilityUsers()
                .assignFox()
                .assignCitizens()

            try {
                get<JinrouGameSession> { parametersOf(participants, plugin.scope, selectedMap.map.startTime) }
                get<GameAudience> {
                    parametersOf(selectedParticipants.spectators.mapTo(mutableSetOf()) { it.asPlayerId() })
                }
                get<GameViewers>()
            } catch (e: Exception) {
                this.close()
                return GameBuildResult.Failure("役職配布に問題があります: ${e.message}")
            }

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

        return GameBuildResult.SucceedCreation(scope.get(), scope.get(), scope.get())
    }

    sealed interface GameBuildResult {
        class Failure(val reason: String): GameBuildResult
        class SucceedCreation(
            val game: GameStore,
            val gameSession: JinrouGameSession,
            val audience: GameAudience,
        ): GameBuildResult
    }
}
