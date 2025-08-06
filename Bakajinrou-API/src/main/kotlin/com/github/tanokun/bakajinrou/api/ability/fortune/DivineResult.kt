package com.github.tanokun.bakajinrou.api.ability.fortune

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translate.TranslationKey

sealed interface DivineResult {
    data class FoundResult(val resultTranslationKey: TranslationKey, val target: Participant): DivineResult
    data object NotFoundError: DivineResult
}