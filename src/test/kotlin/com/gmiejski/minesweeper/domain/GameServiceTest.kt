package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.DiscoverFieldCommand
import com.gmiejski.minesweeper.game.domain.FieldCoordinate
import com.gmiejski.minesweeper.game.domain.GameNotFound
import com.gmiejski.minesweeper.game.domain.build
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import org.junit.jupiter.api.Test

class GameServiceTest {

    @Test
    fun errorWhenNoGameStarted() {
        // given
        val gameService = build()
        val notExistingGameID = 1

        // expect
        val exception = shouldThrow<GameNotFound> {
            gameService.executeAction(notExistingGameID, DiscoverFieldCommand(FieldCoordinate(1, 1)))
        }
    }


    @Test
    fun flagField() {
        return
        // given
        val bombs = listOf(FieldCoordinate(1, 1))
        val gameService = build()
        val game = gameService.startGame()

        // when
        gameService.executeAction(game.gameID, DiscoverFieldCommand(FieldCoordinate(1, 1)))

        // then
        val grid = gameService.getGameGrid(game.gameID)
//        grid.visibleFields.size.shouldBeGreaterThan(0)
    }
}

