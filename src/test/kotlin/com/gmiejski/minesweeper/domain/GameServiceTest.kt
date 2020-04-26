package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.*
import com.gmiejski.minesweeper.game.domain.grid.FieldValue
import com.gmiejski.minesweeper.game.domain.grid.SAFE_FIELD
import com.gmiejski.minesweeper.game.domain.grid.UNKNOWN
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.*

class GameServiceTest {

    @Test
    fun errorWhenNoGameStarted() {
        // given
        val gameService = buildForTest()
        val notExistingGameID = UUID.randomUUID().toString()

        // expect
        val exception = shouldThrow<GameNotFound> {
            gameService.executeAction(notExistingGameID, DiscoverFieldCommand(FieldCoordinate(1, 1)))
        }
    }

    @Test
    fun cannotDiscoverSameFieldTwice() {
        // given
        val bombs = listOf(FieldCoordinate(2, 2))
        val gameService = buildForTest(PredictableBombsCoordinatesGenerator(bombs))
        val game = gameService.startGame(5, 5, 1)
        gameService.executeAction(game.gameID, DiscoverFieldCommand(FieldCoordinate(1, 1)))

        // expect
        val exception = shouldThrow<AlreadyDiscovered> {
            gameService.executeAction(game.gameID, DiscoverFieldCommand(FieldCoordinate(1, 1)))
        }
        exception.gameID shouldBe game.gameID
        exception.fieldCoordinate shouldBe FieldCoordinate(1, 1)
    }

    @Test
    fun gameGridShouldHaveProperSize() {
        // given
        val bombs = listOf(FieldCoordinate(3, 3))
        val gameService = buildForTest(PredictableBombsCoordinatesGenerator(bombs))

        // when
        val game = gameService.startGame(5, 5, 0)

        // then
        val grid = gameService.getGameView(game.gameID)
        grid.view.rows.shouldBe(5)
        grid.view.columns.shouldBe(5)
    }


    @Test
    fun discoverNumberFieldOnGrid() {
        // given
        val bombs = listOf(FieldCoordinate(3, 3))
        val gameService = buildForTest(PredictableBombsCoordinatesGenerator(bombs))
        val game = gameService.startGame(5, 5, 1)
        val fieldToDiscover = FieldCoordinate(3, 2)

        // when
        gameService.executeAction(game.gameID, DiscoverFieldCommand(fieldToDiscover))

        // then
        val grid = gameService.getGameView(game.gameID)

        grid.getFieldValue(fieldToDiscover).state.shouldBe(1)
        grid.view.gridRows.forEach { row ->
            row.fields.forEach { field ->
                if (field.coordinate != fieldToDiscover) {
                    field.state.shouldBe(UNKNOWN)
                }
            }
        }
    }

    @Test
    fun discoverSafeFieldOnGrid() {
        // given
        val bombs = listOf(FieldCoordinate(1, 1), FieldCoordinate(4, 4), FieldCoordinate(4, 5))
        val gameService = buildForTest(PredictableBombsCoordinatesGenerator(bombs))
        val game = gameService.startGame(5, 5, 1)
        val fieldToDiscover = FieldCoordinate(3, 2)
        val expectedSafeFields = coordinatesList(listOf(
            Pair(1, 3), Pair(1, 4), Pair(1, 5),
            Pair(2, 3), Pair(2, 4), Pair(2, 5),
            Pair(3, 1), Pair(3, 2),
            Pair(4, 1), Pair(4, 2),
            Pair(5, 1), Pair(5, 2)
        ))

        // when
        gameService.executeAction(game.gameID, DiscoverFieldCommand(fieldToDiscover))

        // then
        val grid = gameService.getGameView(game.gameID)
        DrawGrid().draw(grid)
        val allExpectedDiscovered = expectedSafeFields.all { grid.getFieldValue(it).state == SAFE_FIELD }
        val expectedHidden = coordinatesList(listOf(Pair(5, 4), Pair(5, 5)))
        expectState(grid, bombs.plus(expectedHidden), UNKNOWN)
        expectState(grid, expectedSafeFields, SAFE_FIELD)
        val allNumberFields = generateAllCoordinates(grid).minus(expectedSafeFields).minus(bombs).minus(expectedHidden)
        expectState(grid, allNumberFields, { it > SAFE_FIELD })
    }
}

fun generateAllCoordinates(boardView: GameView): Set<FieldCoordinate> {
    val result = mutableSetOf<FieldCoordinate>()
    IntRange(1, boardView.view.rows).forEach { row ->
        IntRange(1, boardView.view.columns).forEach { col ->
            result.add(FieldCoordinate(row, col))
        }
    }
    return result
}

fun expectState(boardView: GameView, fields: Collection<FieldCoordinate>, expectedState: FieldValue) {
    fields.forEach {
        boardView.getFieldValue(it).state shouldBe expectedState
    }
}

fun expectState(grid: GameView, fields: Set<FieldCoordinate>, function: (FieldValue) -> Boolean) {
    fields.forEach {
        if (!function(grid.getFieldValue(it).state)) {
            function(grid.getFieldValue(it).state) shouldBe true
        }
    }
}
