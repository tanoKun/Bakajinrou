package com.github.tanokun.bakajinrou.game.crafting

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant

data class CraftingInfo(val crafter: Participant, val style: CraftingStyle, val method: GrantedMethod) {
}