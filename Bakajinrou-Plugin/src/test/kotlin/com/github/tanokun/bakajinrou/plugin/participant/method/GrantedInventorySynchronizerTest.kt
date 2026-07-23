package com.github.tanokun.bakajinrou.plugin.participant.method

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.crafting.CraftingInfo
import com.github.tanokun.bakajinrou.game.crafting.CraftingStyle
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.presentation.item.MethodItemPresenter
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.bukkit.entity.Player
import org.junit.jupiter.api.DisplayName
import java.util.Locale
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class GrantedInventorySynchronizerTest {
    private val gameChangesMock: GameChanges = mockk()
    private val playerProviderMock: BukkitPlayerProvider = mockk()
    private val craftingMock: Crafting = mockk()
    private val methodItemPresenterMock: MethodItemPresenter = mockk(relaxed = true)

    private val participantMock: Participant = mockk {
        every { participantId } returns mockk()
    }
    private val playerMock: Player = mockk(relaxed = true) {
        every { locale() } returns Locale.JAPAN
    }

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = CoroutineScope(testDispatcher)


    private fun createSynchronizer(mainScope: CoroutineScope) = GrantedInventorySynchronizer(
        gameChangesMock,
        mainScope,
        playerProviderMock,
        craftingMock,
        methodItemPresenterMock,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("プレイヤーがオンラインであれば、次の処理に進む")
    fun test1() = runTest {
        val flow = MutableSharedFlow<MethodDifference>(replay = 1)

        gameChangesMock.apply {
            every { methodChanges } returns flow.shareIn(testScope, SharingStarted.Eagerly, replay = 1)
        }

        playerProviderMock.apply {
            coEvery { waitPlayerOnline(participantMock.participantId) } returns playerMock
        }

        val method = mockk<GrantedMethod> {
            every { assetKey } returns MethodAssetKeys.Attack.SWORD
            every { reason } returns GrantedReason.INITIALIZED
        }
        val add = MethodDifference.Granted(participantMock.participantId, method)

        createSynchronizer(testScope)

        flow.emit(add)
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        verify(exactly = 1) { methodItemPresenterMock.sword(Locale.JAPAN) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("プレイヤー取得待機中、手段が削除されたら処理を停止する")
    fun test2() = runTest {
        val flow = MutableSharedFlow<MethodDifference>(replay = 1)

        gameChangesMock.apply {
            every { methodChanges } returns flow.shareIn(testScope, SharingStarted.Eagerly, replay = 1)
        }

        playerProviderMock.apply {
            coEvery { waitPlayerOnline(participantMock.participantId) } coAnswers {
                suspendCancellableCoroutine { }
            }
        }

        val method = mockk<GrantedMethod> {
            every { assetKey } returns MethodAssetKeys.Attack.SWORD
            every { reason } returns GrantedReason.INITIALIZED
        }
        val add = MethodDifference.Granted(participantMock.participantId, method)
        val remove = MethodDifference.Removed(participantMock.participantId, method)

        createSynchronizer(testScope)

        flow.emit(add)
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        verify(exactly = 0) { methodItemPresenterMock.sword(any()) }

        flow.emit(remove)
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        verify(exactly = 0) { methodItemPresenterMock.sword(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("クラフト通知後に購読しても最新情報を取得する")
    fun test3() = runTest {
        val diffFlow = MutableSharedFlow<MethodDifference>(replay = 1)
        val craftingFlow = MutableSharedFlow<CraftingInfo>(replay = 1)

        gameChangesMock.apply {
            every { methodChanges } returns diffFlow.shareIn(testScope, SharingStarted.Eagerly, replay = 1)
        }

        playerProviderMock.apply {
            coEvery { waitPlayerOnline(participantMock.participantId) } returns playerMock
        }

        craftingMock.apply {
            every { observeCrafting() } returns craftingFlow
        }

        val methodMock = mockk<GrantedMethod> {
            every { assetKey } returns MethodAssetKeys.Attack.SWORD
            every { reason } returns GrantedReason.CRAFTED
        }
        val add = MethodDifference.Granted(participantMock.participantId, methodMock)

        createSynchronizer(testScope)
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        craftingFlow.emit(CraftingInfo(participantMock.participantId, CraftingStyle.BULK, methodMock))
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        verify(exactly = 0) { methodItemPresenterMock.sword(any()) }

        diffFlow.emit(add)
        testDispatcher.scheduler.advanceTimeBy(1.seconds)

        verify(exactly = 1) { methodItemPresenterMock.sword(Locale.JAPAN) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("すべての手段を対応する表示関数へ委譲する")
    fun delegatesAllMethodsToPresenter() = runTest {
        val flow = MutableSharedFlow<MethodDifference>(replay = 1)

        every {
            gameChangesMock.methodChanges
        } returns flow.shareIn(testScope, SharingStarted.Eagerly, replay = 1)
        coEvery {
            playerProviderMock.waitPlayerOnline(participantMock.participantId)
        } returns playerMock

        createSynchronizer(testScope)

        val assetKeys = listOf(
            MethodAssetKeys.Attack.GAS,
            MethodAssetKeys.Attack.SWORD,
            MethodAssetKeys.Attack.ARROW,
            MethodAssetKeys.Attack.SCATTER_CROSSBOW,
            MethodAssetKeys.Protective.TOTEM,
            MethodAssetKeys.Protective.FAKE_TOTEM,
            MethodAssetKeys.Protective.SHIELD,
            MethodAssetKeys.Protective.RESISTANCE,
            MethodAssetKeys.Advantage.EXCHANGE,
            MethodAssetKeys.Advantage.SPEED,
            MethodAssetKeys.Advantage.INVISIBILITY,
            MethodAssetKeys.Ability.DIVINE,
            MethodAssetKeys.Ability.COMMUNE,
            MethodAssetKeys.Ability.PROTECT,
        )

        assetKeys.forEach { key ->
            val method = mockk<GrantedMethod> {
                every { assetKey } returns key
                every { reason } returns GrantedReason.INITIALIZED
            }
            flow.emit(MethodDifference.Granted(participantMock.participantId, method))
            testDispatcher.scheduler.advanceTimeBy(1.seconds)
        }

        verify(exactly = 1) { methodItemPresenterMock.gas(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.sword(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.arrow(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.scatterCrossbow(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.totem(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.fakeTotem(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.shield(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.resistance(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.exchange(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.speed(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.invisibility(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.divine(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.commune(Locale.JAPAN) }
        verify(exactly = 1) { methodItemPresenterMock.protect(Locale.JAPAN) }
    }
}
