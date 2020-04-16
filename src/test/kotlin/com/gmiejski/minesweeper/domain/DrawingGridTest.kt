package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.EventHandler
import com.gmiejski.minesweeper.game.domain.FieldCoordinate
import com.gmiejski.minesweeper.game.domain.Game
import com.gmiejski.minesweeper.game.domain.GameBuilder
import org.junit.jupiter.api.Test

class DrawGridTest {


    @Test
    fun drawGrid() {
        // given
        val game = generateGame(5, 10, setOf(FieldCoordinate(3, 3)))

        // when
        val result = game.discover(FieldCoordinate(1, 1))
        val eve1 = game.discover(FieldCoordinate(3, 2))
        EventHandler().applyAll(game, listOf(result.first(), eve1.first()))
        val view = game.getBoardView()
        DrawGrid().draw(view)

    }

    private fun generateGame(height: Int, width: Int, bombsPositions: Set<FieldCoordinate>): Game {
        return GameBuilder(height, width).bombs(bombsPositions).build()
    }
}