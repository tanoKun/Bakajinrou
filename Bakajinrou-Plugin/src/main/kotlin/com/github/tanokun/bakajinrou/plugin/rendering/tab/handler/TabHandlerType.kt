package com.github.tanokun.bakajinrou.plugin.rendering.tab.handler

import com.github.tanokun.bakajinrou.api.participant.ParticipantId

sealed interface TabHandlerType {
    data object ShareInLobby : TabHandlerType
    data object SharedBySpectators : TabHandlerType

    data class EachPlayer(val participantId: ParticipantId) : TabHandlerType
}