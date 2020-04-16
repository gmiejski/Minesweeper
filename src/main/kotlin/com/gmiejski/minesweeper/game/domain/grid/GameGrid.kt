package com.gmiejski.minesweeper.game.domain.grid

import com.gmiejski.minesweeper.game.domain.Field
import com.gmiejski.minesweeper.game.domain.FieldCoordinate


typealias FieldValue = Int


const val BOMB = -2
const val EMPTY = 0

class GameGrid(private val fields: Map<FieldCoordinate, Field>) {
    private val rows = fields.keys.map { it.row }.max() ?: 0
    private val columns = fields.keys.map { it.column }.max() ?: 0
    private val fieldsContent = buildNumbersMap(fields)

    private fun buildNumbersMap(fields: Map<FieldCoordinate, Field>): Map<FieldCoordinate, FieldValue> {
        val result = mutableMapOf<FieldCoordinate, Int>()
        fields.forEach { (currentField, _) ->
            if (this.isBomb(currentField)) {
                result[currentField] = BOMB
            } else {
                val neighboursMines = calculateNeighbouringMines(currentField, fields)
                result[currentField] = neighboursMines
            }
        }
        return result
    }

    private fun calculateNeighbouringMines(currentField: FieldCoordinate, fields: Map<FieldCoordinate, Field>): Int {
        var neighboursMines = 0
        IntRange(currentField.row - 1, currentField.row + 1).forEach { row ->
            IntRange(currentField.column - 1, currentField.column + 1).forEach { col ->
                if ((row != currentField.row || col != currentField.column) && this.validGrid(row, col)) {
                    if (fields[FieldCoordinate(row, col)]?.isBomb == true) {
                        neighboursMines += 1
                    }
                }
            }
        }
        return neighboursMines
    }

    fun isBomb(fieldCoordinate: FieldCoordinate): Boolean {
        if (fields.containsKey(fieldCoordinate)) {
            return fields.get(fieldCoordinate)!!.isBomb
        }
        return false
    }

    fun getView(): BoardView {
        return BoardView(rows, columns, fields)
    }

    fun discover(fieldCoordinate: FieldCoordinate) {
        fields.get(fieldCoordinate)?.discovered = true
    }

    fun isDiscovered(fieldCoordinate: FieldCoordinate): Boolean {
        return this.fields.get(fieldCoordinate)?.discovered ?: false
    }

    fun discoverTry(fieldCoordinate: FieldCoordinate): List<FieldCoordinate> {
        if (this.isBomb(fieldCoordinate) || this.isNumber(fieldCoordinate)) {
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
            if (this.isNumber(currentField)) {
                continue
            }
            val batchAdd = mutableListOf<FieldCoordinate>()
            IntRange(currentField.row - 1, currentField.row + 1).forEach { row ->
                IntRange(currentField.column - 1, currentField.column + 1).forEach { col ->
                    if (this.validGrid(row, col)) {
                        batchAdd.add(FieldCoordinate(row, col))
                    }
                }
            }
            batchAdd.remove(currentField)
            queue.addAll(batchAdd)
        }
        return discoveredSoFar
    }

    private fun isNumber(fieldCoordinate: FieldCoordinate): Boolean {
        val value = this.fieldsContent[fieldCoordinate] ?: return false
        return value != BOMB && value != EMPTY
    }

    private fun validGrid(row: Int, col: Int): Boolean {
        val rowOK = 1 <= row && row <= rows
        val colOK = 1 <= col && col <= columns
        return rowOK && colOK
    }
}