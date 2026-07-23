package com.github.tanokun.bakajinrou.game.crafting

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.attacking.method.ScatterCrossbowMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.game.protection.ProtectVerificatorProvider
import com.github.tanokun.bakajinrou.game.state.GameStore
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.random.Random

class CraftingTest : StringSpec({
    fun store(participantId: ParticipantId) = GameStore(
        JinrouGame(
            listOf(
                Participant(
                    participantId,
                    mockk(),
                    GrantedStrategy(emptyMap()),
                    ParticipantStates.ALIVE,
                )
            ).all()
        )
    )

    "行商人限定の拡散クロスボウを指定してクラフトできる" {
        val participantId = ParticipantId(UUID.randomUUID())
        val store = store(participantId)
        val crafting = Crafting(store, Random(0), mockk<ProtectVerificatorProvider>())

        val crafted = runBlocking {
            crafting.craftMethod(
                participantId,
                CraftingProduct.SCATTER_CROSSBOW,
                CraftingStyle.SINGLE,
            )
        }

        crafted.shouldBeInstanceOf<ScatterCrossbowMethod>()
        store.getParticipant(participantId)!!.getGrantedMethods() shouldHaveSize 1
    }

    "ランダムクラフトでは行商人限定品を付与しない" {
        val participantId = ParticipantId(UUID.randomUUID())
        val store = store(participantId)
        val crafting = Crafting(
            store,
            object : Random() {
                override fun nextBits(bitCount: Int): Int = 0
                override fun nextInt(until: Int): Int = until - 1
            },
            mockk<ProtectVerificatorProvider>(relaxed = true),
        )

        runBlocking {
            crafting.randomlyCraftMethod(participantId, CraftingStyle.SINGLE)
        }

        store.getParticipant(participantId)!!
            .getGrantedMethods()
            .filterIsInstance<ScatterCrossbowMethod>() shouldHaveSize 0
    }
})
