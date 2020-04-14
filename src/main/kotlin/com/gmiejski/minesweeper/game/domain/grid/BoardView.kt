package com.gmiejski.minesweeper.game.domain.grid

import com.gmiejski.minesweeper.game.domain.FieldCoordinate

class BoardView(val width: Int, val height: Int, map: List<FieldView>) {
    val visibleFields: List<FieldCoordinate> = map.filter { it.discovered }.map { it.coordinates }
}