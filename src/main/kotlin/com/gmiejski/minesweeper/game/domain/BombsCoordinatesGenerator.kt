package com.gmiejski.minesweeper.game.domain

interface BombsCoordinatesGenerator {
    fun generate(rows: Int, columns: Int, bombsCount: Int): Set<FieldCoordinate>
}


class RandomBombsCoordinatesGenerator : BombsCoordinatesGenerator {
    override fun generate(rows: Int, columns: Int, bombsCount: Int): Set<FieldCoordinate> {
        val allPossibleCoordinates = this.allCoordinates(rows, columns)
        return allPossibleCoordinates.shuffled().take(bombsCount).toSet()
    }

    private fun allCoordinates(rows: Int, columns: Int): List<FieldCoordinate> {
        val result = mutableListOf<FieldCoordinate>()
        IntRange(1, rows).forEach { row ->
            IntRange(1, columns).forEach { col ->
                result.add(FieldCoordinate(row, col))
            }
        }
        return result
    }
}