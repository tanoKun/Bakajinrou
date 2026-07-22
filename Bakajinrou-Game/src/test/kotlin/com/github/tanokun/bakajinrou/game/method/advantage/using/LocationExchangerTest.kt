package com.github.tanokun.bakajinrou.game.method.advantage.using

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.advantage.ExchangeMethod
import com.github.tanokun.bakajinrou.api.advantage.using.ExchangeSelector
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.game.state.GameStore
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.util.UUID
import kotlin.random.Random

class LocationExchangerTest: StringSpec({
    fun participant(id: ParticipantId, method: ExchangeMethod? = null) = Participant(
        id,
        mockk(),
        GrantedStrategy(method?.let { mapOf(it.methodId to it) }.orEmpty()),
        ParticipantStates.ALIVE,
    )

    "交換対象がいる場合はアイテムを消費して成功情報を流す" {
        runTest {
            val userId = ParticipantId(UUID.randomUUID())
            val targetId = ParticipantId(UUID.randomUUID())
            val method = ExchangeMethod(reason = GrantedReason.SYSTEM)
            val store = GameStore(JinrouGame(listOf(
                participant(userId, method),
                participant(targetId),
            ).all()))
            val exchanger = LocationExchanger(store, ExchangeSelector(Random(0)))
            val info = async(start = CoroutineStart.UNDISPATCHED) {
                exchanger.observeExchanging().first()
            }

            exchanger.exchange(method, userId)

            info.await() shouldBe ExchangeInfo.Succeeded(userId, targetId)
            store.getParticipant(userId)?.hasGrantedMethod(method.methodId) shouldBe false
        }
    }

    "除外によって交換対象がいない場合もアイテムを消費して対象なし情報を流す" {
        runTest {
            val userId = ParticipantId(UUID.randomUUID())
            val excludedId = ParticipantId(UUID.randomUUID())
            val method = ExchangeMethod(reason = GrantedReason.SYSTEM)
            val store = GameStore(JinrouGame(listOf(
                participant(userId, method),
                participant(excludedId),
            ).all()))
            val exchanger = LocationExchanger(store, ExchangeSelector(Random(0)))
            val info = async(start = CoroutineStart.UNDISPATCHED) {
                exchanger.observeExchanging().first()
            }

            exchanger.exchange(method, userId, setOf(excludedId))

            info.await() shouldBe ExchangeInfo.NoTarget(userId)
            store.getParticipant(userId)?.hasGrantedMethod(method.methodId) shouldBe false
        }
    }
})
