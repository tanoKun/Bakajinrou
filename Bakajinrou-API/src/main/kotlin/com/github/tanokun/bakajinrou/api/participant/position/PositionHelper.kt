package com.github.tanokun.bakajinrou.api.participant.position

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.FortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.KnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.MediumPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.MysticPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition

/**
 * このファイルは、与えられたParticipantが特定の役職であるかどうかを判定するための、
 * トップレベルのヘルパー関数群を定義します。
 *
 * `participant.isPosition<T>()`というチェックを、より可読性の高い、
 * ドメイン固有の言語(例：`isWolf(participant)`)で表現することを目的としています。
 */

/**
 * 指定された参加者が、いずれかの「市民側」の役職であるかを確認します。
 * 
 * @param participant 判定対象の参加者
 */
fun isCitizens(participant: Participant): Boolean = participant.isPosition<CitizensPosition>()

/**
 * 指定された参加者が、いずれかの「市民側の能力者」の役職であるかを確認します。
 *
 * @param participant 判定対象の参加者
 */
fun isMystic(participant: Participant): Boolean = participant.isPosition<MysticPosition>()


/**
 * 指定された参加者が「妖狐」であるかを確認します。
 * 
 * @param participant 判定対象の参加者
 */
fun isFox(participant: Participant): Boolean = participant.isPosition<FoxPosition>()

/**
 * 指定された参加者が、いずれかの「バカ(Idiot)」系の役職であるかを確認します。
 * 
 * @param participant 判定対象の参加者
 */
fun isIdiot(participant: Participant): Boolean = participant.isPosition<IdiotPosition>()

/**
 * 指定された参加者が「市民」であるかを確認します。
 * 
 * @param participant 判定対象の参加者
 */
fun isCitizen(participant: Participant): Boolean = participant.isPosition<CitizenPosition>()

/**
 * 指定された参加者が「占い師」であるかを確認します。
 * (バカ占いは含まない)
 * 
 * @param participant 判定対象の参加者
 */
fun isFortune(participant: Participant): Boolean = participant.isPosition<FortunePosition>()

/**
 * 指定された参加者が「騎士」であるかを確認します。
 * (バカ騎士は含まない)
 * 
 * @param participant 判定対象の参加者
 */
fun isKnight(participant: Participant): Boolean = participant.isPosition<KnightPosition>()

/**
 * 指定された参加者が「霊媒師」であるかを確認します。
 * (バカ霊媒は含まない)
 * 
 * @param participant 判定対象の参加者
 */
fun isMedium(participant: Participant): Boolean = participant.isPosition<MediumPosition>()

/**
 * 指定された参加者が「人狼」であるかを確認します。
 * 
 * @param participant 判定対象の参加者
 */
fun isWolf(participant: Participant): Boolean = participant.isPosition<WolfPosition>()

/**
 * 指定された参加者が「狂人」であるかを確認します。
 * 
 * @param participant 判定対象の参加者
 */
fun isMadman(participant: Participant): Boolean = participant.isPosition<MadmanPosition>()

/**
 * 指定された参加者が「観戦者」であるかを確認します。
 * 
 * @param participant 判定対象の参加者
 */
fun isSpectator(participant: Participant): Boolean = participant.isPosition<SpectatorPosition>()


// --- バカ(Idiot)系の複合役職 ---

/**
 * 指定された参加者が「バカ占い」であるかを確認します。
 * 
 * @param participant 判定対象の参加者
 */
fun isIdiotAsFortune(participant: Participant): Boolean = participant.isPosition<IdiotAsFortunePosition>()

/**
 * 指定された参加者が「バカ騎士」であるかを確認します。
 * 
 * @param participant 判定対象の参加者
 */
fun isIdiotAsKnight(participant: Participant): Boolean = participant.isPosition<IdiotAsKnightPosition>()

/**
 * 指定された参加者が「バカ霊媒」であるかを確認します。
 * 
 * @param participant 判定対象の参加者
 */
fun isIdiotAsMedium(participant: Participant): Boolean = participant.isPosition<IdiotAsMediumPosition>()