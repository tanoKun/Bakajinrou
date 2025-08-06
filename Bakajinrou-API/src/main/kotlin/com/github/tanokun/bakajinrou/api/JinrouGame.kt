package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*

class JinrouGame(
    participants: ParticipantScope.All,
) {
    private val _participants = MutableStateFlow(participants)

    private val _systemFinish = MutableSharedFlow<WonInfo>()

    /**
     * 勝利条件が成立したタイミングで、勝者情報を通知するFlowを返します。
     *
     * このFlowは、参加者の状態が変化するたび評価し、
     * 勝者が決定した場合は、その情報を1回だけ通知します。
     * また、複数の購読者に対して同じ勝者情報を同時に共有します。
     *
     * 強制終了の場合、それを通知します。
     *
     * @param scope 監視するコルーチンスコープ
     *
     * @return 最初に勝利条件が満たされたときに勝者情報 [WonInfo] を1回だけ通知するFlow
     */
    fun observeWin(scope: CoroutineScope): Flow<WonInfo> = merge(
        _systemFinish,
        _participants
            .map { judge() }
            .filterNotNull()
    )
        .take(1)
        .shareIn(scope, SharingStarted.Eagerly, replay = 1)

    /**
     * ゲームを強制終了することを通知します。
     */
    suspend fun notifySystemFinish() = _systemFinish.emit(WonInfo.System(_participants.value))

    /**
     * ゲームを市民の勝利で終了することを通知します。
     */
    suspend fun notifyWonCitizenFinish() = _systemFinish.emit(WonInfo.Citizens(_participants.value))

    /**
     * 現在の参加者の状態に基づいてゲームの勝敗を判定します。
     *
     * 勝利条件:
     * - 市民側の勝利: 生存者に人狼・妖狐が存在しない
     * - 人狼側の勝利: 生存者に市民・妖狐が存在しない
     * - 妖狐側の勝利: 生存者に人狼または市民のいずれかが存在しない
     *
     * いずれの条件も満たさない場合はゲーム継続とみなし、nullを返します。
     *
     * @return ゲームの勝利を表す [WonInfo]。勝敗未確定の場合は null
     */
    fun judge(): WonInfo? {

        val survivors = _participants.value.survivedOnly()
        val citizens = survivors.includes(::isCitizen)
        val wolfs = survivors.includes(::isWolf)
        val fox = survivors.includes(::isFox)

        //市民勝利
        if (wolfs.isEmpty() && fox.isEmpty())
            return WonInfo.Citizens(_participants.value)

        //人狼勝利
        if (citizens.isEmpty() && fox.isEmpty())
            return WonInfo.Wolfs(_participants.value)

        //妖狐勝利
        if (wolfs.isEmpty() || citizens.isEmpty())
            return WonInfo.Fox(_participants.value)

        return null
    }

    /**
     * 変更のあった参加者を監視します。
     *
     * @param scope 共有 CoroutineScope
     *
     * @return 状態が変更されたときに通知される [Flow]
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeParticipants(scope: CoroutineScope): Flow<Participant> =
        _participants
            .scan<ParticipantScope.All, Pair<ParticipantScope.All?, ParticipantScope.All>>(null to _participants.value) { acc, current ->
                acc.second to current
            }
            .drop(1)
            .flatMapConcat { (previous, current) ->
                val changed = current.filter { currentPart ->
                    val previousPart = previous?.find { it.uniqueId == currentPart.uniqueId }
                    previousPart != currentPart
                }

                changed.asFlow()
            }.shareIn(scope, SharingStarted.Eagerly, replay = 1)


    private fun updateParticipants(participantScope: ParticipantScope.All) {
        _participants.value = participantScope
    }

    fun updateParticipant(participant: Participant) {
        updateParticipants(listOf(participant))
    }

    /**
     * 指定された参加者の情報を更新します。
     *
     * @param participants 更新対象の参加者
     */
    fun updateParticipants(participants: List<Participant>) {
        val updateUuids = participants.map { it.uniqueId }
        val newParticipants = _participants.value.filterNot { updateUuids.contains(it.uniqueId) } + participants

        updateParticipants(newParticipants.all())
    }

    /**
     * すべての参加者を取得します。
     */
    fun getAllParticipants() = _participants.value

    /**
     * 指定された ID の参加者を取得します。
     *
     * @param uniqueId 対象のユニークID
     * @return 該当する参加者、存在しない場合は null
     */
    fun getParticipant(uniqueId: UUID) = _participants.value.firstOrNull { it.uniqueId == uniqueId }
}