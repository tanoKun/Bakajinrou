package com.github.tanokun.bakajinrou.game.protect

import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protect.ProtectVerificator

/**
 * 防御手段が有効であるかを検証するための、検証ロジックを提供します
 */
interface ProtectVerificatorProvider {

    /**
     * トーテム系の防御手段が有効であるかを検証する [ProtectVerificator] を取得します。
     *
     * @param participantId 検証対象となる参加者のId
     * @param methodId 検証対象となる手段Id
     *
     * @return トーテムの有効性を検証するインスタンス
     *
     */
    fun getTotemVerificator(participantId: ParticipantId, methodId: MethodId): ProtectVerificator

    /**
     * 盾系の防御手段が有効であるかを検証する [ProtectVerificator] を取得します。
     *
     * @param participantId 検証対象となる参加者のId
     * @param methodId 検証対象となる手段Id

     * @return 盾の有効性を検証するインスタンス
     */
    fun getShieldVerificator(participantId: ParticipantId, methodId: MethodId): ProtectVerificator

    /**
     * 耐性の防御手段が有効であるかを検証する [ProtectVerificator] を取得します。
     *
     * @param isValid この耐性が有効か
     *
     * @return 耐性の有効性を検証するインスタンス
     */
    fun getResistanceVerificator(isValid: Boolean): ProtectVerificator
}