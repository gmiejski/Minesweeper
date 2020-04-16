package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.FieldCoordinate


fun coordinatesList(list: List<Pair<Int, Int>>): List<FieldCoordinate> {
    return list.map { FieldCoordinate(it.first, it.second) }
}
