package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.*
import io.kotest.data.Headers2
import io.kotest.data.Table2
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail


class GameTest {

    @Test
    fun testDiscoveringAFieldWithABombLossesAGame() {
        // given
        val bombField = FieldCoordinate(3, 3)
        val game = generateGame(5, 5, setOf(bombField))

        // when
        val events = game.discover(bombField)

        // then
        events.size.shouldBe(2)
        events.first().shouldBe(BombExploded(game.gameID, bombField))
        events.last().shouldBe(GameEnded(game.gameID))
    }

    @Test
    fun discoveringEmptyFieldDiscoversAllPossibleFields() {
        // given
        val expectedDiscoveredFieldsEvents = fieldsList(listOf(
            Pair(1, 3), Pair(1, 4), Pair(1, 5),
            Pair(2, 2), Pair(2, 3), Pair(2, 4), Pair(2, 5),
            Pair(3, 1), Pair(3, 2),
            Pair(4, 1), Pair(4, 2),
            Pair(5, 1), Pair(5, 2)
        ))

        // when
        forAll(
            Table2(Headers2("row", "col"), listOf(
                row(1, 3), row(1, 4), row(1, 5),
                row(2, 3), row(2, 4), row(2, 5),
                row(3, 1), row(3, 2), row(4, 1),
                row(4, 2),
                row(5, 1), row(5, 2)))
        ) { row, col ->
            val game = generateGame(5, 5, setOf(FieldCoordinate(1, 1), FieldCoordinate(4, 4), FieldCoordinate(4, 5)))
            val events = game.discover(FieldCoordinate(row, col))
            events.size shouldBe 1
            val fieldDiscoveredEvent = events.first()
            if (fieldDiscoveredEvent is FieldDiscoveredEvent) {
                fieldDiscoveredEvent.allDiscoveredFields.shouldContainAll(expectedDiscoveredFieldsEvents)
            } else {
                fail("Wrong event received")
            }
        }
    }

    @Test
    fun discoveringEmptyFieldDiscoversAllPossibleFieldsButOnlyASingleArea() {
        // given
        val expectedDiscoveredFieldsEvents = fieldsList(listOf(
            Pair(1, 5), Pair(1, 6),
            Pair(2, 5), Pair(2, 6),
            Pair(3, 5), Pair(3, 6),
            Pair(4, 4), Pair(4, 5), Pair(4, 6),
            Pair(5, 4), Pair(5, 5), Pair(5, 6)
        ))

        // when
        forAll(
            Table2(Headers2("row", "col"), listOf(
                row(1, 6), row(2, 6), row(3, 6),
                row(4, 6),
                row(5, 5), row(5, 6)
            ))
        ) { row, col ->
            val game = generateGame(5, 6,
                setOf(FieldCoordinate(1, 4), FieldCoordinate(3, 3), FieldCoordinate(3, 4), FieldCoordinate(4, 3))
            )
            val events = game.discover(FieldCoordinate(row, col))
            events.size shouldBe 1
            val fieldDiscoveredEvent = events.first()
            if (fieldDiscoveredEvent is FieldDiscoveredEvent) {
                fieldDiscoveredEvent.allDiscoveredFields.shouldContainAll(expectedDiscoveredFieldsEvents)
            } else {
                fail("Wrong event received")
            }
        }
    }

    @Test
    fun discoveringNumberDiscoversSingleFieldOnly() {
        // when
        forAll(
            Table2(Headers2("row", "col"), listOf(
                row(2, 2), row(2, 3), row(2, 4),
                row(3, 2), row(3, 4),
                row(4, 2), row(4, 3), row(4, 4)
            ))
        ) { row, col ->
            val game = generateGame(5, 5,
                setOf(FieldCoordinate(3, 3))
            )
            val field = FieldCoordinate(row, col)
            val events = game.discover(field)
            events.size shouldBe 1
            events.shouldBe(listOf(FieldDiscoveredEvent(game.gameID, field, listOf(field))))
        }
    }

    private fun fieldsList(list: List<Pair<Int, Int>>): List<FieldCoordinate> {
        return list.map { FieldCoordinate(it.first, it.second) }
    }

    @Test
    fun getBoardView() {
        // given
        val game = generateGame(5, 5, setOf(FieldCoordinate(3, 3)))

        // when
        game.discover(FieldCoordinate(1, 1))

        // then
        val view = game.getBoardView()
//        view.visibleFields shouldBe listOf(FieldCoordinate(1, 1))
    }

    @Test
    fun discoveringABombFlagEmitsBombDiscoveredEvent() {
        return
        // given
        val game = generateGame(5, 5, setOf(FieldCoordinate(3, 3)))

        // when
        val result = game.discover(FieldCoordinate(3, 3))

        // then
        result.shouldBe(DiscoveryResult.BOMB)
        game.status().shouldBe(GameStatus.EXPLODED)
    }


    private fun generateGame(rows: Int, columns: Int, bombsPositions: Set<FieldCoordinate>): Game {
        return GameBuilder(rows, columns).bombs(bombsPositions).build()
    }
}


