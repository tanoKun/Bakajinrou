package com.github.tanokun.bakajinrou.plugin.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import net.kyori.adventure.text.Component

/**
 * 全ての参加者に対して、同一のプレフィックスを返します。
 **/
class LiteralPrefix(private val literal: Component): Prefix {
    override fun resolvePrefix(viewer: Participant, target: Participant): Component = literal
}