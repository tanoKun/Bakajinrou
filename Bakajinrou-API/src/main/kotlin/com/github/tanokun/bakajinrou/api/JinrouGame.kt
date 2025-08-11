package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.*
import com.github.tanokun.bakajinrou.api.participant.position.isCitizens
import com.github.tanokun.bakajinrou.api.participant.position.isFox
import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.withLock

class JinrouGame(
    private val mutexProvider: UpdateMutexProvider,
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
    suspend fun notifyWonBySystem() = _systemFinish.emit(WonInfo.System(_participants.value))

    /**
     * ゲームを市民の勝利で終了することを通知します。
     */
    suspend fun notifyWonCitizen() = _systemFinish.emit(WonInfo.Citizens(_participants.value))

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
        val citizens = survivors.includes(::isCitizens)
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
    fun observeParticipants(scope: CoroutineScope): Flow<ParticipantDifference> = _participants
        .scan<ParticipantScope.All, Pair<ParticipantScope.All?, ParticipantScope.All>>(null to _participants.value) { acc, current ->
            acc.second to current
        }
        .drop(1)
        .flatMapConcat { (previous, current) ->
            val changed = current.mapNotNull { currentPart ->
                val previousPart = previous?.find { it.participantId == currentPart.participantId }
                if (currentPart.completelyEquals(previousPart)) return@mapNotNull null

                return@mapNotNull ParticipantDifference(previousPart, currentPart)
            }

            changed.asFlow()
        }
        .shareIn(scope, SharingStarted.Eagerly, replay = 1)

    /**
     * 指定されたIDの参加者に対して、アトミックな更新処理をスレッドセーフに実行します。
     *
     * このメソッドは、対象となる参加者ID専用のロックを取得し、`transform`ラムダを実行します。
     * これにより、同じ参加者に対する複数の更新処理が同時に実行されるのを防ぎます。
     * また、内部で`StateFlow`の状態を更新する際も、アトミック性が保証されるように設計されています。
     * 異なる参加者IDに対する更新処理は、並行して実行可能です。

     * `transform`ラムダの内部で、**同じ、あるいは別の`updateParticipant`メソッドを再帰的に、または間接的に呼び出すことは絶対に避けてください。**
     * @param participantId 更新対象の参加者のId
     * @param transform 現在の参加者状態を受け取り、変更後の新しい状態を返す関数
     *
     * @throws IllegalArgumentException 指定されたIDの参加者が存在しない場合
     * @throws IllegalArgumentException 異なる参加者を編集した場合
     */
    suspend fun updateParticipant(participantId: ParticipantId, transform: (current: Participant) -> Participant) {
        mutexProvider.get(participantId).withLock {
            val current = getParticipant(participantId) ?: throw IllegalArgumentException("存在しない参加者です。")

            val update = transform(current)

            if (update.participantId != participantId) throw IllegalArgumentException("異なる参加者は編集できません。")

            val newParticipants = _participants.value.filterNot { participantId == it.participantId } + update

            _participants.value = newParticipants.all()
        }
    }

    suspend fun addParticipant(participant: Participant) {
        mutexProvider.get(participant.participantId).withLock {
            if (existParticipant(participant.participantId)) throw IllegalArgumentException("既に存在する参加者は追加できません。")

            val newParticipants = _participants.value + participant

            _participants.value = newParticipants.all()
        }
    }

    /**
     * すべての参加者を取得します。
     */
    fun getCurrentParticipants() = _participants.value

    /**
     * 指定された ID の参加者を取得します。
     *
     * @param participantId
     * @return 該当する参加者、存在しない場合は null
     */
    fun getParticipant(participantId: ParticipantId) = _participants.value.firstOrNull { it.participantId == participantId }

    fun existParticipant(participantId: ParticipantId) = getParticipant(participantId) != null
}