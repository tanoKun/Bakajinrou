package com.github.tanokun.bakajinrou.api.ability.medium

import com.github.tanokun.bakajinrou.api.ability.ResultSource

sealed interface CommuneResultSource {
    data class FoundResult(val resultKey: ResultSource): CommuneResultSource
    data object NotDeadError: CommuneResultSource
}