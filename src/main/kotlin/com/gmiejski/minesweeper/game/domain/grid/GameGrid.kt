package com.gmiejski.minesweeper.game.domain.grid

import com.gmiejski.minesweeper.game.domain.Field
import com.gmiejski.minesweeper.game.domain.FieldCoordinate

class GameGrid(private val fields: Map<FieldCoordinate, Field>) {
    private val width = fields.keys.map { it.positionX }.max() ?: 0
    private val height = fields.keys.map { it.positionY }.max() ?: 0

    fun isBomb(fieldCoordinate: FieldCoordinate): Boolean {
        if (fields.containsKey(fieldCoordinate)) {
            return fields.get(fieldCoordinate)!!.isBomb
        }
        return false
    }

    fun getView(): BoardView {
        return BoardView(width, height, fields.map { FieldView(it.key, it.value.discovered) })
    }

    fun discover(fieldCoordinate: FieldCoordinate) {
        fields.get(fieldCoordinate)?.discovered = true
    }

    fun isDiscovered(fieldCoordinate: FieldCoordinate): Boolean {
        return this.fields.get(fieldCoordinate)?.discovered ?: false
    }
}