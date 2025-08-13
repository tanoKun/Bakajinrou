package com.github.tanokun.bakajinrou.game.crafting

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.ParticipantId

data class CraftingInfo(val crafterId: ParticipantId, val style: CraftingStyle, val method: GrantedMethod) {
}