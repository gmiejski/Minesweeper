package com.gmiejski.minesweeper.game.domain.grid

import com.gmiejski.minesweeper.game.domain.FieldCoordinate


data class BoardViewRow(val fields: List<BoardViewField>)
data class BoardViewField(val coordinate: FieldCoordinate, val state: FieldValue)

class BoardView(val rows: Int, val columns: Int, originalGrid: Grid) {
    val gridRows = getGridRows(originalGrid)

    private fun getGridRows(grid: Grid): List<BoardViewRow> {
        val map = grid.map { gridRow ->
            BoardViewRow(gridRow.map { cell ->
                if (cell.isDiscovered) {
                    BoardViewField(cell.coordinate, cell.fieldValue)
                } else {
                    BoardViewField(cell.coordinate, UNKNOWN)
                }

            })
        }
        return map
    }

    fun getFieldValue(row: Int, column: Int): BoardViewField {
        return gridRows[row - 1].fields[column - 1]
    }

    fun getFieldValue(coordinate: FieldCoordinate): BoardViewField {
        return this.getFieldValue(coordinate.row, coordinate.column)
    }
}
