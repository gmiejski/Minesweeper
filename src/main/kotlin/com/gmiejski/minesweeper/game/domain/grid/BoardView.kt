package com.gmiejski.minesweeper.game.domain.grid

import com.gmiejski.minesweeper.game.domain.Field
import com.gmiejski.minesweeper.game.domain.FieldCoordinate

class BoardView(val width: Int, val height: Int, fields: Map<FieldCoordinate, Field>) {
    val gridRows = fields.values
        .sortedWith(comparator())
        .chunked(fields.keys.map { it.row }.max() ?: 1) // Why 1?
        .map { BoardViewRow(it) }


    private fun comparator(): java.util.Comparator<Field> {
        return Comparator { o1, o2 ->
            if (o1.position.row == o2.position.row) {
                if (o1.position.column <= o2.position.column) {
                    return@Comparator -1
                } else {
                    return@Comparator 1
                }
            } else {
                return@Comparator if (o1.position.row < o2.position.row) -1 else 1
            }
        }
    }

}

class BoardViewRow(fields: List<Field>) {
    val fieldView = fields.map { BoardViewField(it.discovered, it.isBomb) }
}

data class BoardViewField(val discovered: Boolean, val isBomb: Boolean)