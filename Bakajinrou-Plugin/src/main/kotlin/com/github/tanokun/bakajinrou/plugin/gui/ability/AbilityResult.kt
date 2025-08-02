package com.github.tanokun.bakajinrou.plugin.gui.ability

import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import net.kyori.adventure.text.Component
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.text

sealed class AbilityResult(val result: Component) {
    object Citizens: AbilityResult(component { text("市民") color Positions.Citizen.color.asHexString() })
    object Wolf: AbilityResult(component { text("人狼") color Positions.Wolf.color.asHexString() })
    object Fox: AbilityResult(component { text("妖狐") color Positions.Fox.color.asHexString() })
}