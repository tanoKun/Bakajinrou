package com.github.tanokun.bakajinrou.plugin.participant

import com.github.tanokun.bakajinrou.api.method.ActivationPriority
import com.github.tanokun.bakajinrou.api.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.StrategyIntegrity
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.inventory.ItemStackMock
import java.util.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class ParticipantStrategyTest {
    private lateinit var server: ServerMock

    private lateinit var plugin: Plugin

    private lateinit var playerMock: Player
    private lateinit var participantStrategy: ParticipantStrategy

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.createMockPlugin()

        playerMock = server.addPlayer()

        participantStrategy = ParticipantStrategy(playerMock.uniqueId, StrategyIntegrity())
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    @DisplayName("grant で AsBukkitItem を追加するとインベントリに追加される")
    fun grantAsBukkitItemTest() {
        val itemUniqueId = UUID.randomUUID()

        val itemStackMock: ItemStack = spyk(ItemStackMock(Material.STRING)) {
            every { persistentDataContainer.getOrDefault(any(), PersistentDataType.STRING, any()) } returns itemUniqueId.toString()
            every { clone() } returns this
        }

        val asBukkitItemMock: AsBukkitItem = mockk {
            every { createBukkitItem() } returns itemStackMock
        }

        participantStrategy.grant(asBukkitItemMock)

        verify(exactly = 1) { asBukkitItemMock.createBukkitItem() }
        assertTrue {
            playerMock.inventory.getItem(0) === itemStackMock
        }
    }

    @Test
    @DisplayName("remove で該当アイテムがインベントリから削除される")
    fun removeAsBukkitItemTest() {
        val itemUniqueId = UUID.randomUUID()

        val itemStackMock: ItemStack = spyk(ItemStackMock(Material.STRING)) {
            every { persistentDataContainer.getOrDefault(any(), PersistentDataType.STRING, any()) } returns itemUniqueId.toString()
            every { clone() } returns this
        }

        val asBukkitItemMock: AsBukkitItem = mockk {
            every { createBukkitItem() } returns itemStackMock
            every { uniqueId } returns itemUniqueId
        }

        participantStrategy.grant(asBukkitItemMock)
        participantStrategy.remove(asBukkitItemMock)

        verify(exactly = 1) { asBukkitItemMock.createBukkitItem() }
        assertTrue {
            itemStackMock.amount == 0
        }
    }

    @Test
    @DisplayName("getMethod で UUID に対応するメソッドを取得できる")
    fun getMethodTest() {
        val itemUniqueId = UUID.randomUUID()

        val mockMethod: GrantedMethod = mockk {
            every { uniqueId } returns itemUniqueId
        }

        participantStrategy.grant(mockMethod)
        assertTrue {
            participantStrategy.getMethod(itemUniqueId) === mockMethod
        }
    }

    @Test
    @DisplayName("getActiveProtectiveMethods で有効な保護手段のみ取得できる")
    fun getActiveProtectiveMethodsTest() {
        val participantMock: Participant = mockk()

        val mockOtherMethod1: GrantedMethod = mockk {
            every { uniqueId } returns UUID.randomUUID()
        }
        val mockOtherMethod2: AttackMethod = mockk {
            every { uniqueId } returns UUID.randomUUID()
        }
        val mockProtectiveMethod1: ProtectiveMethod = mockk {
            every { uniqueId } returns UUID.randomUUID()
            every { priority } returns ActivationPriority.LOW
            every { isActive(participantMock) } returns true
        }
        val mockProtectiveMethod2: ProtectiveMethod = mockk {
            every { uniqueId } returns UUID.randomUUID()
            every { priority } returns ActivationPriority.NORMAL
            every { isActive(participantMock) } returns true
        }
        val mockProtectiveMethod3: ProtectiveMethod = mockk {
            every { uniqueId } returns UUID.randomUUID()
            every { priority } returns ActivationPriority.HIGH
            every { isActive(participantMock) } returns true
        }
        val mockProtectiveMethod4: ProtectiveMethod = mockk {
            every { uniqueId } returns UUID.randomUUID()
            every { priority } returns ActivationPriority.HIGH
            every { isActive(participantMock) } returns false
        }

        participantStrategy.grant(mockOtherMethod1)
        participantStrategy.grant(mockOtherMethod2)
        participantStrategy.grant(mockProtectiveMethod1)
        participantStrategy.grant(mockProtectiveMethod2)
        participantStrategy.grant(mockProtectiveMethod3)
        participantStrategy.grant(mockProtectiveMethod4)

        assertContentEquals(
            expected = listOf(mockProtectiveMethod3, mockProtectiveMethod2, mockProtectiveMethod1),
            actual = participantStrategy.getActiveProtectiveMethods(participantMock)
        )
    }

}