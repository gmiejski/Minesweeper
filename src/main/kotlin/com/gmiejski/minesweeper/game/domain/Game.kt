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


data class FieldCoordinate(val row: Int, val column: Int)

data class GameCreatedEvent(override val target: GameID, val rows: Int, val columns: Int, val fields: Map<FieldCoordinate, Field>) : DomainEvent(target)
data class GameEnded(override val target: GameID) : DomainEvent(target)
data class BombExploded(override val target: GameID, val bombField: FieldCoordinate) : DomainEvent(target)
data class FieldDiscoveredEvent(override val target: GameID, val discoveredField: FieldCoordinate, val allDiscoveredFields: List<FieldCoordinate>) : DomainEvent(target)

class Game(val gameID: GameID) {
    private var currentStatus: GameStatus = GameStatus.IN_PROGRESS
    private lateinit var gameGrid: GameGrid // TODO change for non-lateinit empty Grid

    fun discover(fieldCoordinate: FieldCoordinate): List<DomainEvent> {
        if (gameGrid.isBomb(fieldCoordinate)) {
            val now = LocalDateTime.now()
            return listOf(BombExploded(gameID, fieldCoordinate).occurredAt(now), GameEnded(gameID).occurredAt(now))
        }
        val allCoordinates = gameGrid.discoverTry(fieldCoordinate)
        return listOf(FieldDiscoveredEvent(gameID, fieldCoordinate, allCoordinates))
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


class GameBuilder(val rows: Int, val cols: Int) {
    private lateinit var bombsPositions: List<List<Field>>

    fun bombs(bombsPositions: Set<FieldCoordinate>): GameBuilder {
        val calculatePositions = IntStream.range(1, rows + 1).mapToObj { row ->
            IntStream.range(1, cols + 1).mapToObj { col ->
                val coordinate = FieldCoordinate(row, col)
                Field(coordinate, bombsPositions.contains(coordinate))
            }.toList()
        }.toList()

        this.bombsPositions = calculatePositions
        return this
    }

    fun build(): Game {
        val fields = bombsPositions.flatten().groupBy { it.position }.mapValues { it.value.first() }
        return EventHandler().applyAll(Game(Random.nextInt()), listOf(GameCreatedEvent(Random.nextInt(), rows, cols, fields).occurredAt(LocalDateTime.now())))// TODO Event handler should be hidden?
    }
}
