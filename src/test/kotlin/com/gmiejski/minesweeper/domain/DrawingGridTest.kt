package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DrawGridTest {


    @Test
    fun testDiscoveringAFieldWithABombLossesAGame() {
        // given
        val game = generateGame(5, 10, setOf(FieldCoordinate(3, 3)))

        // when
        val result = game.discover(FieldCoordinate(1, 1))
        game.discover(FieldCoordinate(3, 3))
        game.discover(FieldCoordinate(3, 4))
        DrawGrid().draw(game.getBoardView())

    }

    private fun generateGame(height: Int, width: Int, bombsPositions: Set<FieldCoordinate>): Game {
        return GameBuilder(height, width).bombs(bombsPositions).build()
    }

}