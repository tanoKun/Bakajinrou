package com.github.tanokun.bakajinrou.api.ability.medium

import com.github.tanokun.bakajinrou.api.translate.TranslationKey

sealed interface CommuneResult {
    data class FoundResult(private val resultTranslationKey: TranslationKey): CommuneResult
    data object NotDeadError: CommuneResult
    data object NotFoundError: CommuneResult
}