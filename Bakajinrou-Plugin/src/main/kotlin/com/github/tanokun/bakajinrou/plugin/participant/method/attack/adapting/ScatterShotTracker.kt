package com.github.tanokun.bakajinrou.plugin.participant.method.attack.adapting

import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import java.util.UUID

data class CompletedScatterShot(
    val shooterId: ParticipantId,
    val methodId: MethodId,
)

@Scoped
@Scope(value = GameComponents::class)
class ScatterShotTracker {
    private data class Shot(
        val shooterId: ParticipantId,
        val projectileIds: MutableSet<UUID> = mutableSetOf(),
    )

    private val shots = mutableMapOf<MethodId, Shot>()
    private val methodByProjectile = mutableMapOf<UUID, MethodId>()

    @Synchronized
    fun register(shooterId: ParticipantId, methodId: MethodId, projectileId: UUID): Boolean {
        val isFirstProjectile = methodId !in shots
        val shot = shots.getOrPut(methodId) { Shot(shooterId) }
        shot.projectileIds += projectileId
        methodByProjectile[projectileId] = methodId
        return isFirstProjectile
    }

    @Synchronized
    fun land(projectileId: UUID): CompletedScatterShot? {
        val methodId = methodByProjectile.remove(projectileId) ?: return null
        val shot = shots[methodId] ?: return null
        shot.projectileIds -= projectileId
        if (shot.projectileIds.isNotEmpty()) return null

        shots.remove(methodId)
        return CompletedScatterShot(shot.shooterId, methodId)
    }

    @Synchronized
    fun complete(methodId: MethodId): CompletedScatterShot? {
        val shot = shots.remove(methodId) ?: return null
        shot.projectileIds.forEach(methodByProjectile::remove)
        return CompletedScatterShot(shot.shooterId, methodId)
    }
}
