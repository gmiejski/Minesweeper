package com.gmiejski.minesweeper.domain

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
        game.discover(FieldCoordinate(1, 1))
        val view = game.getView()
        DrawGrid().draw(view)

    }

    private fun generateGame(height: Int, width: Int, bombsPositions: Set<FieldCoordinate>): Game {
        return GameBuilder(height, width).bombs(bombsPositions).build()
    }
}