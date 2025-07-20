package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.ParticipantStates.*


/**
 * 参加者のプレイ状態を表します。また、状態遷移条件は以下の通りです。
 * - (生存)[SURVIVED] ⇒ (死亡)[DEAD]
 * - (ゲーム中断)[SUSPENDED] ⇒ (生存)[SURVIVED]
 */
enum class ParticipantStates {
    /**
     * 生存状態を表します。
     */
    SURVIVED,

    /**
     * 生存状態を前提として以下の状態を示します。
     * - スペクテイター状態
     * - ログアウト状態
     */
    SUSPENDED,

    /**
     * 殺害状態を示します。この状態でログアウトでも[SUSPENDED]に移行しません。
     */
    DEAD;
}