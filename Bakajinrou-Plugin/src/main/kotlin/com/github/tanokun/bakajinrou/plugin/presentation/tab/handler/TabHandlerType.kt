package com.github.tanokun.bakajinrou.plugin.presentation.tab.handler

import com.github.tanokun.bakajinrou.api.participant.ParticipantId

sealed interface TabHandlerType {
    data object ShareInLobby : TabHandlerType
    data object PreparedGame : TabHandlerType
    data object SharedObserverView : TabHandlerType

    data class EachParticipant(val participantId: ParticipantId) : TabHandlerType
}
