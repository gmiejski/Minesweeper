package com.gmiejski.minesweeper.game.domain

class PredictableBombsCoordinatesGenerator(val bombs: List<FieldCoordinate>) : BombsCoordinatesGenerator {
    override fun generate(rows: Int, columns: Int, bombsCount: Int): Set<FieldCoordinate> {
        return bombs.toSet()
    }
}