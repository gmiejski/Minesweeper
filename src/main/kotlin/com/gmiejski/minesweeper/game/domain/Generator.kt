package com.gmiejski.minesweeper.game.domain

import java.util.stream.IntStream
import kotlin.random.Random
import kotlin.streams.toList

interface Generator {
    fun generate(rows: Int, columns: Int, bombsCount: Int): Map<FieldCoordinate, Field>
}


class RandomGenerator : Generator {
    override fun generate(rows: Int, columns: Int, bombsCount: Int): Map<FieldCoordinate, Field> {
        val all = IntStream.range(1, rows + 1).mapToObj { h ->
            IntStream.range(1, columns + 1).mapToObj { w ->
                val coordinate = FieldCoordinate(h, w)
                Field(coordinate, false)
            }.toList()
        }.toList()

        IntRange(0, bombsCount).forEach {
            var row: Int
            var column: Int
            do {
                row = Random.nextInt(1, rows)
                column = Random.nextInt(1, columns)

            } while (!all.get(row).get(column).isBomb)
            all.get(row).get(column).isBomb = true
        }
        return all.flatten().groupBy { it.position }.mapValues { it.value.first() }
    }
}