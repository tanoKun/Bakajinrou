package com.github.tanokun.bakajinrou.plugin.setting

import com.github.tanokun.bakajinrou.api.participant.Position
import com.github.tanokun.bakajinrou.bukkit.position.citizen.FortunePosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.KnightPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.MediumPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.bukkit.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.bukkit.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.bukkit.position.wolf.WolfPosition

enum class Positions(val candidatePositions: List<Position>) {
    WOLF(listOf(WolfPosition)),
    MADMAN(listOf(MadmanPosition)),
    FOX(listOf(FoxPosition)),
    IDIOT(listOf(IdiotAsFortunePosition, IdiotAsMediumPosition, IdiotAsKnightPosition)),
    FORTUNE(listOf(FortunePosition)),
    MEDIUM(listOf(MediumPosition)),
    KNIGHTS(listOf(KnightPosition))
    ;
}