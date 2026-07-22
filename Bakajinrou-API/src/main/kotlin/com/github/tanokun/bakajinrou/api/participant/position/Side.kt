package com.github.tanokun.bakajinrou.api.participant.position

/**
 * 参加者が所属する陣営を表します。
 *
 * 勝敗の帰属先となる単位であり、各役職は原則としていずれかの陣営に属します。
 * ゲームの勝敗に関与しない役職 (観戦者など) は、陣営を持ちません。
 */
enum class Side {
    /** 村人陣営。 */
    VILLAGE,

    /** 人狼陣営。 */
    WEREWOLF,

    /** 妖狐陣営。 */
    FOX,
}
