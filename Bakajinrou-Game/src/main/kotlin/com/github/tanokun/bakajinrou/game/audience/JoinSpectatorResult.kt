package com.github.tanokun.bakajinrou.game.audience

sealed interface JoinSpectatorResult {
    data object Joined: JoinSpectatorResult
    data object AlreadySpectating: JoinSpectatorResult
    data object ParticipantCannotSpectate: JoinSpectatorResult
}
