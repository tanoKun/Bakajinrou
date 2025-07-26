package com.github.tanokun.bakajinrou.plugin.position.fox

import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import java.util.*

object FoxThirdPosition: FoxPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "妖狐", defaultPrefix = "妖狐")

    override fun doAtStarting(uniqueId: UUID) {}
}