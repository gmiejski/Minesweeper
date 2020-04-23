package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.*
import com.gmiejski.minesweeper.game.domain.grid.FLAGGED
import com.gmiejski.minesweeper.game.domain.grid.UNKNOWN
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


val emptyField = FieldCoordinate(1, 1)
val singleBombMap = listOf(FieldCoordinate(3, 3))

class GameFlaggingTest {

    @Test
    fun cannotToggleDiscoveredField() {
        // given
        val bombs = listOf(FieldCoordinate(2, 2))
        val gameService = buildForTest(PredictableBombsCoordinatesGenerator(bombs))
        val game = gameService.startGame(5, 5, 5)
        val discoveredField = FieldCoordinate(3, 3)
        gameService.executeAction(game.gameID, DiscoverFieldCommand(discoveredField))

        // expect
        assertThrows<CannotToggleField> {
            gameService.executeAction(game.gameID, ToggleFieldCommand(game.gameID, discoveredField))
        }
    }

    @Test
    fun toggleBackAField() {
        // given
        val bombs = listOf(FieldCoordinate(3, 3))
        val gameService = buildForTest(PredictableBombsCoordinatesGenerator(bombs))
        val game = gameService.startGame(5, 5, 5)

        // when
        gameService.executeAction(game.gameID, ToggleFieldCommand(game.gameID, emptyField))

        // then
        val gameGrid = gameService.getGameView(game.gameID)
        gameGrid.getFieldValue(emptyField).state shouldBe FLAGGED

        // when
        gameService.executeAction(game.gameID, ToggleFieldCommand(game.gameID, emptyField))

        // then
        val gameGrid2 = gameService.getGameView(game.gameID)
        gameGrid2.getFieldValue(emptyField).state shouldBe UNKNOWN
    }

    @Test
    fun cannotDiscoverToggledField() {
        // given
        val gameService = buildForTest(PredictableBombsCoordinatesGenerator(singleBombMap))
        val game = gameService.startGame(5, 5, 5)
        val discoveredField = FieldCoordinate(1, 1)
        gameService.executeAction(game.gameID, ToggleFieldCommand(game.gameID, discoveredField))


        // expect
        assertThrows<CannotDiscoverToggledField> {
            gameService.executeAction(game.gameID, DiscoverFieldCommand(discoveredField))
        }
    }
}