package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test


class GameTest {

    @Test
    fun testDiscoveringAFieldWithABombLossesAGame() {
        // given
        val game = generateGame(5, 5, setOf(FieldCoordinate(3, 3)))

        // when
        val result = game.discover(FieldCoordinate(3, 3))

        // then
        result.shouldBe(DiscoveryResult.BOMB)
        game.status().shouldBe(GameStatus.EXPLODED)
    }

    @Test
    fun getBoardView() {
        // given
        val game = generateGame(5, 5, setOf(FieldCoordinate(3, 3)))
        // when
        var view = game.getBoardView()

        // then
        view.visibleFields shouldBe emptyList()

        // when
        game.discover(FieldCoordinate(1, 1))

        // then
        view = game.getBoardView()
        view.visibleFields shouldBe listOf(FieldCoordinate(1, 1))
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


    private fun generateGame(height: Int, width: Int, bombsPositions: Set<FieldCoordinate>): Game {
        return GameBuilder(height, width).bombs(bombsPositions).build()
    }
}
