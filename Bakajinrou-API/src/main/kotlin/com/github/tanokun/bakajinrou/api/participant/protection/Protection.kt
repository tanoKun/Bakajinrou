package com.github.tanokun.bakajinrou.api.participant.protection

interface Protection {
    /**
     * プレイヤーが何らかの理由で攻撃を無効かできるか
     *
     * @return 防御できる理由
     */
    fun hasProtection(): ProtectionResult
}