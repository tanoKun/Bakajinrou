package com.github.tanokun.bakajinrou.plugin.setting

import com.github.tanokun.bakajinrou.api.participant.Position
import com.github.tanokun.bakajinrou.plugin.position.citizen.FortunePosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.KnightPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.MediumPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.plugin.position.fox.FoxThirdPosition
import com.github.tanokun.bakajinrou.plugin.position.wolf.MadmanSecondPosition
import com.github.tanokun.bakajinrou.plugin.position.wolf.WolfSecondPosition

enum class Positions(val candidatePositions: List<Position>) {
    WOLF(listOf(WolfSecondPosition)),
    MADMAN(listOf(MadmanSecondPosition)),
    FOX(listOf(FoxThirdPosition)),
    IDIOT(listOf(IdiotAsFortunePosition, IdiotAsMediumPosition, IdiotAsKnightPosition)),
    FORTUNE(listOf(FortunePosition)),
    MEDIUM(listOf(MediumPosition)),
    KNIGHTS(listOf(KnightPosition))
    ;
}