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
                } else if (cell.isToggled) {
                    BoardViewField(cell.coordinate, FLAGGED)
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

    fun toDTO(): BoardViewDTO {
        return BoardViewDTO(rows, columns, BoardViewGridDTO(gridRows.map { BoardViewRowDTO(it.fields.map { BoardViewFieldDTO(it.coordinate, it.state) }) }))
    }
}

data class BoardViewFieldDTO(val coordinate: FieldCoordinate, val state: FieldValue)
data class BoardViewRowDTO(val fields: List<BoardViewFieldDTO>)
data class BoardViewGridDTO(val rows: List<BoardViewRowDTO>)
class BoardViewDTO(val rows: Int, val columns: Int, val grid: BoardViewGridDTO)