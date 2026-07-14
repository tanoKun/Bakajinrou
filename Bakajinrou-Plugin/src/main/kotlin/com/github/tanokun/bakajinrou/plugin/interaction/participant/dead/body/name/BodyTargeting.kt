package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body.name

import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

internal object BodyTargeting {
    fun intersectionDistance(
        eyePosition: Vector,
        eyeDirection: Vector,
        bodyBounds: BoundingBox,
        maxDistance: Double,
        obstructionDistance: Double?
    ): Double? {
        val hit = bodyBounds.rayTrace(eyePosition, eyeDirection, maxDistance) ?: return null
        val distance = hit.hitPosition.distance(eyePosition)

        if (obstructionDistance != null && obstructionDistance <= distance) return null

        return distance
    }
}
