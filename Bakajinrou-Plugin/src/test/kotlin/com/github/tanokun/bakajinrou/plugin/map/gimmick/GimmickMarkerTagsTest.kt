package com.github.tanokun.bakajinrou.plugin.map.gimmick

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class GimmickMarkerTagsTest : StringSpec({
    "転送ゲートは統一後のタグを認識する" {
        GimmickMarkerTags.contains(
            setOf("jinrou_gimmick_transfer_gate"),
            GimmickMarkerTags.TRANSFER_GATE,
        ) shouldBe true
    }

    "既存マップの転送ゲートタグも互換名として認識する" {
        GimmickMarkerTags.contains(setOf("4_tp"), GimmickMarkerTags.TRANSFER_GATE) shouldBe true
        GimmickMarkerTags.contains(setOf("4_all"), GimmickMarkerTags.TRANSFER_GATE_ALL) shouldBe true
        GimmickMarkerTags.contains(setOf("4_foxOnly"), GimmickMarkerTags.TRANSFER_GATE_FOX_ONLY) shouldBe true
    }
})
