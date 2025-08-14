package com.github.tanokun.bakajinrou.plugin.observer.participant.strategy.grant

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.crafting.CraftingInfo
import com.github.tanokun.bakajinrou.game.crafting.CraftingStyle
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.interaction.participant.method.GrantedInventorySynchronizer
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class SyncInventoryObserverTest {
    private val grantedStrategiesPublisherMock: GrantedStrategiesPublisher = mockk()
    private val playerProviderMock: BukkitPlayerProvider = mockk()
    private val assetKeyMock = mockk<MethodAssetKeys> {
        every { key } returns "test.key"
    }

    private val craftingMock: Crafting = mockk()

    private val participantMock: Participant = mockk {
        every { participantId } returns mockk()
    }
    private val playerMock: Player = mockk(relaxed = true)

    private var result: Boolean = false

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = CoroutineScope(testDispatcher)


    inner class TestObserverSync(mainScope: CoroutineScope): GrantedInventorySynchronizer(
        grantedStrategiesPublisherMock, mainScope, playerProviderMock, craftingMock, mockk(relaxed = true), assetKeyMock
    ) {
            override fun createItem(player: Player, add: MethodDifference.Granted): ItemStack { result = true; return mockk() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("プレイヤーがオンラインであれば、次の処理に進む")
    fun test1() = runTest {
        val flow = MutableSharedFlow<MethodDifference>(replay = 1)

        grantedStrategiesPublisherMock.apply {
            every { observeDifference() } returns flow.shareIn(testScope, SharingStarted.Eagerly, replay = 1)
        }

         playerProviderMock.apply {
             coEvery { waitPlayerOnline(participantMock.participantId) } returns playerMock
         }

        val method = mockk<GrantedMethod> {
            every { assetKey } returns assetKeyMock
            every { reason } returns GrantedReason.INITIALIZED
        }
        val add = MethodDifference.Granted(participantMock.participantId, method)

        spyk<GrantedInventorySynchronizer>(TestObserverSync(testScope))

        flow.emit(add)
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        result shouldBe true
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("プレイヤー取得待機中、手段が削除されたら処理を停止する")
    fun test2() = runTest {
        val flow = MutableSharedFlow<MethodDifference>(replay = 1)

        grantedStrategiesPublisherMock.apply {
            every { observeDifference() } returns flow.shareIn(testScope, SharingStarted.Eagerly, replay = 1)
        }

        playerProviderMock.apply {
            coEvery { waitPlayerOnline(participantMock.participantId) } coAnswers {
                suspendCancellableCoroutine { }
            }
        }

        val method = mockk<GrantedMethod> {
            every { assetKey } returns assetKeyMock
            every { reason } returns GrantedReason.INITIALIZED
        }
        val add = MethodDifference.Granted(participantMock.participantId, method)
        val remove = MethodDifference.Removed(participantMock.participantId, method)

        spyk<GrantedInventorySynchronizer>(TestObserverSync(testScope))

        flow.emit(add)
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        result shouldBe false

        flow.emit(remove)
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        result shouldBe false
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("クラフトだった場合、情報を待機する")
    fun test3() = runTest {
        val diffFlow = MutableSharedFlow<MethodDifference>(replay = 1)
        val craftingFlow = MutableSharedFlow<CraftingInfo>(replay = 1)

        grantedStrategiesPublisherMock.apply {
            every { observeDifference() } returns diffFlow.shareIn(testScope, SharingStarted.Eagerly, replay = 1)
        }

        playerProviderMock.apply {
            coEvery { waitPlayerOnline(participantMock.participantId) } returns playerMock
        }

        craftingMock.apply {
            every { observeCrafting(any()) } answers {
                val scope = arg<CoroutineScope>(0)  // 0番目の引数を取得
                craftingFlow.shareIn(scope, SharingStarted.Eagerly, replay = 1)
            }
        }

        val methodMock = mockk<GrantedMethod> {
            every { assetKey } returns assetKeyMock
            every { reason } returns GrantedReason.CRAFTED
        }
        val add = MethodDifference.Granted(participantMock.participantId, methodMock)

        spyk<GrantedInventorySynchronizer>(TestObserverSync(testScope))

        diffFlow.emit(add)
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        result shouldBe false

        craftingFlow.emit(CraftingInfo(participantMock.participantId, CraftingStyle.BULK, methodMock))
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        result shouldBe true
    }
}