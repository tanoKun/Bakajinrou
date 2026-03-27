package com.github.tanokun.bakajinrou.plugin.setting.prepare.desided

import com.github.tanokun.bakajinrou.plugin.common.serialize.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SelectedParticipants(
    val participants: Set<@Serializable(with = UUIDSerializer::class) UUID>,
    val spectators: Set<@Serializable(with = UUIDSerializer::class) UUID>
)