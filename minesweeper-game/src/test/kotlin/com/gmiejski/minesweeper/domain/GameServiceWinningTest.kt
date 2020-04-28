package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.*
import io.kotest.data.Headers1
import io.kotest.data.Table1
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GameServiceWinningTest {

    @Test
    fun winGameInSingleDiscovery() {
        // given
        val bombs = listOf(FieldCoordinate(3, 3))
        val gameService = buildForTest(PredictableBombsCoordinatesGenerator(bombs))
        val game = gameService.startGame(5, 5, 1)
        gameService.executeAction(game.gameID, DiscoverFieldCommand(FieldCoordinate(5, 5)))

        // expect
        val grid = gameService.getGameView(game.gameID)
        grid.status shouldBe GameStatus.WON
    }


    @Test
    fun winGameAfterLastDiscovery() {
        // given
        val bombs = listOf(FieldCoordinate(2, 2))
        val gameService = buildForTest(PredictableBombsCoordinatesGenerator(bombs))
        val game = gameService.startGame(5, 5, 1)
        gameService.executeAction(game.gameID, DiscoverFieldCommand(FieldCoordinate(5, 5)))

        // expect
        var grid = gameService.getGameView(game.gameID)
        grid.status shouldBe GameStatus.IN_PROGRESS

        // when
        gameService.executeAction(game.gameID, DiscoverFieldCommand(FieldCoordinate(1, 1)))
        gameService.executeAction(game.gameID, DiscoverFieldCommand(FieldCoordinate(1, 2)))
        gameService.executeAction(game.gameID, DiscoverFieldCommand(FieldCoordinate(2, 1)))

        // then
        grid = gameService.getGameView(game.gameID)
        grid.status shouldBe GameStatus.WON
    }
}

class GameWonBlockOperationsTest {
    @Test
    fun name() {
        val (gameService, gameID) = getLostGame()
        val someField = FieldCoordinate(1, 2)
        forAll(
            Table1(Headers1("operation"), listOf(
                row(DiscoverFieldCommand(someField)),
                row(ToggleFieldCommand(gameID, someField))
            ))
        ) { command ->
            assertThrows<GameAlreadyEnded> {
                gameService.executeAction(gameID, command)
            }
        }
    }
}

fun getLostGame(): Pair<MineSweeperGameService, GameID> {
    val bombs = listOf(FieldCoordinate(3, 3))
    val gameService = buildForTest(PredictableBombsCoordinatesGenerator(bombs))
    val game = gameService.startGame(5, 5, 1)
    gameService.executeAction(game.gameID, DiscoverFieldCommand(FieldCoordinate(3, 3)))
    return Pair(gameService, game.gameID)
}

fun getWonGame(): Pair<MineSweeperGameService, GameID> {
    val bombs = listOf(FieldCoordinate(3, 3))
    val gameService = buildForTest(PredictableBombsCoordinatesGenerator(bombs))
    val game = gameService.startGame(5, 5, 1)
    gameService.executeAction(game.gameID, DiscoverFieldCommand(FieldCoordinate(5, 5)))
    return Pair(gameService, game.gameID)
}