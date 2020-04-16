package com.gmiejski.minesweeper.domain

import com.gmiejski.minesweeper.game.domain.Field
import com.gmiejski.minesweeper.game.domain.FieldCoordinate
import com.gmiejski.minesweeper.game.domain.Generator
import com.gmiejski.minesweeper.game.domain.RandomGenerator


class PredictableGenerator : Generator {
    override fun generate(rows: Int, columns: Int, bombsCount: Int): Map<FieldCoordinate, Field> {
        TODO("Not yet implemented")
    }

}