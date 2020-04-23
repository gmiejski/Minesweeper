package com.gmiejski.minesweeper.game.domain.grid

import com.gmiejski.minesweeper.game.domain.FieldCoordinate


typealias FieldValue = Int // TODO refactor to enum


const val BOMB = -2
const val SAFE_FIELD = 0
const val UNKNOWN = -3
const val FLAGGED = -4

typealias Grid = List<GridRow>

fun Grid.getRow(row: Int): GridRow {
    return this[row - 1]
}
typealias GridRow = List<GridCell>

fun GridRow.getColumn(column: Int): GridCell {
    return this[column - 1]
}


data class GridCell(val coordinate: FieldCoordinate, val fieldValue: FieldValue, var isDiscovered: Boolean = false, var isToggled: Boolean = false)

class GameGrid(private val rows: Int, private val columns: Int, bombsCoordinates: Set<FieldCoordinate>) {
    private val grid = buildGrid(bombsCoordinates)

    private fun buildGrid(bombsCoordinates: Set<FieldCoordinate>): Grid {
        val result = mutableListOf<GridRow>()
        IntRange(1, rows).forEach { row ->
            val newRow = mutableListOf<GridCell>()
            IntRange(1, columns).forEach { col ->
                val coordinate = FieldCoordinate(row, col)
                val value = calculateFieldValue(coordinate, bombsCoordinates)
                newRow.add(GridCell(coordinate, value))
            }
            result.add(newRow)
        }
        return result
    }

    private fun calculateFieldValue(coordinate: FieldCoordinate, bombsCoordinates: Set<FieldCoordinate>): FieldValue {
        if (bombsCoordinates.contains(coordinate)) {
            return BOMB
        }
        return calculateNeighbouringMines(coordinate, bombsCoordinates)
    }

    private fun calculateNeighbouringMines(currentField: FieldCoordinate, bombsFields: Set<FieldCoordinate>): Int {
        var neighboursMines = 0
        IntRange(currentField.row - 1, currentField.row + 1).forEach { row ->
            IntRange(currentField.column - 1, currentField.column + 1).forEach { col ->
                val neighbourCoordinate = FieldCoordinate(row, col)
                if (neighbourCoordinate != currentField && this.validCoordinate(row, col)) {
                    if (bombsFields.contains(neighbourCoordinate)) {
                        neighboursMines += 1
                    }
                }
            }
        }
        return neighboursMines
    }

    fun isBomb(coordinate: FieldCoordinate): Boolean {
        return this.getFieldValue(coordinate) == BOMB
    }

    private fun getFieldValue(coordinate: FieldCoordinate): FieldValue {
        return findCell(coordinate).fieldValue
    }

    private fun findCell(coordinate: FieldCoordinate): GridCell {
        this.checkCoordinate(coordinate)
        return this.grid.getRow(coordinate.row).getColumn(coordinate.column)
    }

    fun getView(): BoardView {
        return BoardView(rows, columns, this.grid)
    }

    fun isDiscovered(coordinate: FieldCoordinate): Boolean {
        return findCell(coordinate).isDiscovered
    }


    fun discoverAllAround(fieldCoordinate: FieldCoordinate): List<FieldCoordinate> {
        if (this.isBomb(fieldCoordinate) || this.isDangerousField(fieldCoordinate)) {
            return listOf(fieldCoordinate)
        }
        val discoveredSoFar = mutableListOf<FieldCoordinate>()
        val queue = mutableListOf<FieldCoordinate>()
        queue.add(fieldCoordinate)
        while (queue.isNotEmpty()) {
            val currentField = queue.removeAt(queue.size - 1)
            if (discoveredSoFar.contains(currentField)) {
                continue
            }
            if (this.isBomb(currentField)) {
                continue
            }
            discoveredSoFar.add(currentField)
            if (this.isDangerousField(currentField)) {
                continue
            }
            val batchAdd = mutableListOf<FieldCoordinate>()
            IntRange(currentField.row - 1, currentField.row + 1).forEach { row ->
                IntRange(currentField.column - 1, currentField.column + 1).forEach { col ->
                    if (this.validCoordinate(row, col)) {
                        batchAdd.add(FieldCoordinate(row, col))
                    }
                }
            }
            batchAdd.remove(currentField)
            queue.addAll(batchAdd)
        }
        return discoveredSoFar
    }

    // Dangerous field is when it's neighbour is a bomb
    private fun isDangerousField(coordinate: FieldCoordinate): Boolean {
        return getFieldValue(coordinate) > SAFE_FIELD
    }

    private fun validCoordinate(row: Int, col: Int): Boolean {
        val rowOK = row in 1..rows
        val colOK = col in 1..columns
        return rowOK && colOK
    }

    private fun checkCoordinate(coordinate: FieldCoordinate) {
        if (this.validCoordinate(coordinate.row, coordinate.column).not()) {
            throw RuntimeException("cell coordinate out of game grid!")
        }
    }

    fun mapAsDiscovered(coordinate: FieldCoordinate) {
        this.findCell(coordinate).isDiscovered = true
    }

    fun toggle(coordinate: FieldCoordinate) {
        val cell = findCell(coordinate)
        cell.isToggled = !cell.isToggled
    }

    fun isToggled(coordinate: FieldCoordinate): Boolean {
        return findCell(coordinate).isToggled
    }
}

