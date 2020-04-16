package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.BombsCoordinatesGenerator
import com.gmiejski.minesweeper.game.domain.FieldCoordinate


class PredictableBombsCoordinatesGenerator(private val bombs: List<FieldCoordinate>) : BombsCoordinatesGenerator {
    override fun generate(rows: Int, columns: Int, bombsCount: Int): Set<FieldCoordinate> {
        return bombs.toSet()
    }
}