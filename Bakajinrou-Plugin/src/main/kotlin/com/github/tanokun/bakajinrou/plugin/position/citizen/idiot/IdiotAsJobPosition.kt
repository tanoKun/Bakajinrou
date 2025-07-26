package com.github.tanokun.bakajinrou.plugin.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.citizen.IdiotPosition

abstract class IdiotAsJobPosition(fake: String): IdiotPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "$fake(バカ)", defaultPrefix = fake)
}