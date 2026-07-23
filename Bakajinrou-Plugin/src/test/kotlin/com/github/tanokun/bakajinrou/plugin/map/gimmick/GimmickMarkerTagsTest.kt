package com.github.tanokun.bakajinrou.plugin.map.gimmick

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class GimmickMarkerTagsTest : StringSpec({
    "転送ゲートのタグは統一した接頭辞を使用する" {
        GimmickMarkerTags.TRANSFER_GATE.startsWith("jinrou_gimmick_") shouldBe true
        GimmickMarkerTags.TRANSFER_GATE_ALL.startsWith("jinrou_gimmick_") shouldBe true
        GimmickMarkerTags.TRANSFER_GATE_FOX_ONLY.startsWith("jinrou_gimmick_") shouldBe true
    }
})
