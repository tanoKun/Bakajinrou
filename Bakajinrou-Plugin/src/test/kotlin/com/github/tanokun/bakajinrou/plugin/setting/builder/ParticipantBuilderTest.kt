package com.github.tanokun.bakajinrou.plugin.setting.builder

import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.FortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.KnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.MediumPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.AbilityUsersAssigner.Companion.assignAbilityUsers
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.CitizenAssigner.Companion.assignCitizens
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.FoxAssigner.Companion.assignFox
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.IdiotAssigner.Companion.assignIdiots
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.MadmanAssigner.Companion.assignMadmans
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.ParticipantBuilder.WolfAssigner.Companion.assignWolfs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.random.Random

class ParticipantBuilderTest {
    private val template =
        hashMapOf(
            RequestedPositions.WOLF to 3,
            RequestedPositions.MADMAN to 2,
            RequestedPositions.IDIOT to 3,
            RequestedPositions.FORTUNE to 1,
            RequestedPositions.MEDIUM to 1,
            RequestedPositions.KNIGHT to 1,
            RequestedPositions.FOX to 1
        )

    private val uuids = setOf(
        UUID.fromString("a8654d52-41bf-8c01-2756-c5ac19891ea0"),
        UUID.fromString("a5b6d303-e49c-6571-f28e-e20ed52510c9"),
        UUID.fromString("eec2ad3f-a3f8-1f64-6158-5d4f02fb5b7b"),
        UUID.fromString("eb6a48aa-fe4e-5bb3-17d1-7905ab89d06e"),
        UUID.fromString("7aac3407-b659-f845-9ab9-7a85937f2b56"),
        UUID.fromString("533fa42b-a7aa-8bd5-ae7e-edb30de9c745"),
        UUID.fromString("20ff420c-1bd3-1fb9-711e-dbe9bee2d3c5"),
        UUID.fromString("13b15448-2d44-c3f1-208d-c456ff8b810f"),
        UUID.fromString("4253297c-c3f5-3a91-8f74-af69f0660b2a"),
        UUID.fromString("4ec91eac-e203-f198-b38b-b64392e86850"),
        UUID.fromString("8106e789-4d2e-2a07-62b2-7f1f412011eb"),
        UUID.fromString("0161a168-cd49-f703-5ff0-bde663cf5470"),
        UUID.fromString("b04cd2d4-4669-9210-d6c7-5c60f2be1fc9"),
        UUID.fromString("9f462b5c-3229-b5ac-89a4-1589a34b60b7"),
        UUID.fromString("c2aa0121-2d39-a552-5956-4488f0fd2a9c"),
    )

    private val random = Random(0)

    @Test
    @DisplayName("正しい役職振り分けができているか")
    fun correctlyAssignsAllPositionsTest() {
        val participants = ParticipantBuilder(template, uuids, random)
            .assignMadmans()
            .assignWolfs(false)
            .assignIdiots(IdiotAsFortunePosition, IdiotAsMediumPosition, IdiotAsKnightPosition)
            .assignAbilityUsers()
            .assignFox()
            .assignCitizens()

        assertEquals(3, participants.count { it.isPosition<WolfPosition>() })
        assertEquals(2, participants.count { it.isPosition<MadmanPosition>() })
        assertEquals(3, participants.count { it.isPosition<IdiotPosition>() })
        assertEquals(1, participants.count { it.isPosition<FortunePosition>() })
        assertEquals(1, participants.count { it.isPosition<MediumPosition>() })
        assertEquals(1, participants.count { it.isPosition<KnightPosition>() })
        assertEquals(1, participants.count { it.isPosition<FoxPosition>() })
        assertEquals(3, participants.count { it.isPosition<CitizenPosition>() })
        assertEquals(15, participants.count())
    }
}