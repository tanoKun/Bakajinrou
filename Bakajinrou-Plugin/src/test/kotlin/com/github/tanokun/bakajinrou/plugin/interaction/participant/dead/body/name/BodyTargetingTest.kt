package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body.name

import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BodyTargetingTest {
    private val bodyBounds = BoundingBox(3.5, 0.0, -0.3, 4.1, 0.6, 0.3)
    private val eyePosition = Vector(0.0, 0.3, 0.0)
    private val eyeDirection = Vector(1.0, 0.0, 0.0)

    @Test
    @DisplayName("4ブロック以内で死体に照準が合う")
    fun hitsBodyWithinRange() {
        val distance = BodyTargeting.intersectionDistance(
            eyePosition,
            eyeDirection,
            bodyBounds,
            maxDistance = 4.0,
            obstructionDistance = null
        )

        assertEquals(3.5, distance)
    }

    @Test
    @DisplayName("最大距離より遠い死体には当たらない")
    fun missesBodyOutsideRange() {
        val distance = BodyTargeting.intersectionDistance(
            eyePosition,
            eyeDirection,
            bodyBounds,
            maxDistance = 3.4,
            obstructionDistance = null
        )

        assertNull(distance)
    }

    @Test
    @DisplayName("目線が死体から外れている場合は当たらない")
    fun missesBodyOutsideCrosshair() {
        val distance = BodyTargeting.intersectionDistance(
            eyePosition,
            Vector(1.0, 0.0, 1.0).normalize(),
            bodyBounds,
            maxDistance = 4.0,
            obstructionDistance = null
        )

        assertNull(distance)
    }

    @Test
    @DisplayName("死体より手前にブロックがある場合は当たらない")
    fun obstructionBlocksBody() {
        val distance = BodyTargeting.intersectionDistance(
            eyePosition,
            eyeDirection,
            bodyBounds,
            maxDistance = 4.0,
            obstructionDistance = 3.0
        )

        assertNull(distance)
    }

    @Test
    @DisplayName("死体より奥のブロックは照準を妨げない")
    fun obstructionBehindBodyDoesNotBlock() {
        val distance = BodyTargeting.intersectionDistance(
            eyePosition,
            eyeDirection,
            bodyBounds,
            maxDistance = 4.0,
            obstructionDistance = 3.8
        )

        assertEquals(3.5, distance)
    }
}
