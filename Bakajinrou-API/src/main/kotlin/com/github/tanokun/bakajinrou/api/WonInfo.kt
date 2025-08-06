package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.ParticipantScope

sealed interface WonInfo {
    val participants: ParticipantScope.All

    data class Wolfs(override val participants: ParticipantScope.All): WonInfo
    data class Citizens(override val participants: ParticipantScope.All): WonInfo
    data class Fox(override val participants: ParticipantScope.All): WonInfo
    data class System(override val participants: ParticipantScope.All): WonInfo
}