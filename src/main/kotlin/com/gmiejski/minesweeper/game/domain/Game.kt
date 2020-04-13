package com.gmiejski.minesweeper.game.domain

import java.util.stream.IntStream
import kotlin.streams.toList

enum class DiscoveryResult {
    BOMB, EMPTY
}

enum class GameStatus {
    IN_PROGRESS, EXPLODED, WON
}


data class Field(val position: FieldCoordinate, val isBomb: Boolean) {
    var discovered: Boolean = false
}


data class FieldCoordinate(val positionX: Int, val positionY: Int)

class Game(private val gameGrid: GameGrid) {
    private var currentStatus: GameStatus = GameStatus.IN_PROGRESS

    fun discover(fieldCoordinate: FieldCoordinate): DiscoveryResult {
        if (gameGrid.isBomb(fieldCoordinate)) {
            this.currentStatus = GameStatus.EXPLODED
            return DiscoveryResult.BOMB
        }
        gameGrid.discover(fieldCoordinate)

        return DiscoveryResult.EMPTY
    }

    fun status(): GameStatus {
        return this.currentStatus
    }

    fun getBoardView(): BoardView {
        return gameGrid.getView()
    }

}

class BoardView(val width: Int, val height: Int, map: List<FieldView>) {
    val visibleFields: List<FieldCoordinate> = map.filter { it.discovered }.map { it.coordinates }
}

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
}

class FieldView(val coordinates: FieldCoordinate, val discovered: Boolean) {

}


class GameBuilder(val height: Int, val width: Int) {
    private lateinit var bombsPositions: List<List<Field>>

    fun bombs(bombsPositions: Set<FieldCoordinate>): GameBuilder {
        val calculatePositions = IntStream.range(1, height + 1).mapToObj { h ->
            IntStream.range(1, width + 1).mapToObj { w ->
                val coordinate = FieldCoordinate(h, w)
                Field(coordinate, bombsPositions.contains(coordinate))
            }.toList()
        }.toList()

        this.bombsPositions = calculatePositions
        return this
    }

    fun build(): Game {
        val fields = bombsPositions.flatten().groupBy { it.position }.mapValues { it.value.first() }
        return Game(GameGrid(fields))
    }
}
