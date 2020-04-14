package com.gmiejski.minesweeper.game.domain

import com.gmiejski.minesweeper.game.domain.grid.BoardView
import com.gmiejski.minesweeper.game.domain.grid.GameGrid
import java.time.LocalDateTime
import java.util.stream.IntStream
import kotlin.random.Random
import kotlin.streams.toList

enum class DiscoveryResult {
    BOMB, EMPTY
}

enum class GameStatus {
    NOT_INITIALIZED, IN_PROGRESS, EXPLODED, WON
}


data class Field(val position: FieldCoordinate, var isBomb: Boolean) {
    var discovered: Boolean = false
}


data class FieldCoordinate(val positionX: Int, val positionY: Int)


class Game(val gameID: GameID) {
    private var currentStatus: GameStatus = GameStatus.IN_PROGRESS
    private lateinit var gameGrid: GameGrid // TODO change for non-lateinit empty Grid

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

    fun initializeGame(fields: Map<FieldCoordinate, Field>) {
        this.currentStatus = GameStatus.IN_PROGRESS
        this.gameGrid = GameGrid(fields)
    }

    fun canDiscover(fieldCoordinate: FieldCoordinate): DiscoverTry {
        return when (this.gameGrid.isDiscovered(fieldCoordinate)) { // TODO where to check if in bounds?
            true -> DiscoverTry.ALREADY_DISCOVERED
            false -> DiscoverTry.CAN_BE_DISCOVERED
        }
    }
}

enum class DiscoverTry {
    ALREADY_DISCOVERED, CAN_BE_DISCOVERED
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
        return EventHandler().applyAll(Game(Random.nextInt()), listOf(GameCreatedEvent(Random.nextInt(), width, height, fields, LocalDateTime.now())))// TODO Event handler should be hidden?
    }
}
