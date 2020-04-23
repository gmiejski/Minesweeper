package com.gmiejski.minesweeper.game.domain

import com.gmiejski.minesweeper.game.domain.grid.BoardView
import com.gmiejski.minesweeper.game.domain.grid.GameGrid
import java.time.LocalDateTime
import kotlin.random.Random

enum class DiscoveryResult {
    BOMB, EMPTY
}

enum class GameStatus {
    NOT_INITIALIZED, IN_PROGRESS, EXPLODED, WON
}

data class FieldCoordinate(val row: Int, val column: Int)

data class GameCreatedEvent(override val target: GameID, val rows: Int, val columns: Int, val bombsCoordinates: Set<FieldCoordinate>) : DomainEvent(target)
data class GameEnded(override val target: GameID) : DomainEvent(target)
data class BombExploded(override val target: GameID, val bombField: FieldCoordinate) : DomainEvent(target)
data class FieldDiscoveredEvent(override val target: GameID, val discoveredField: FieldCoordinate, val allDiscoveredFields: List<FieldCoordinate>) : DomainEvent(target)
class FieldToggled(override val target: GameID, val coordinate: FieldCoordinate) : DomainEvent(target)

class Game(val gameID: GameID) {
    private var currentStatus: GameStatus = GameStatus.IN_PROGRESS
    private lateinit var gameGrid: GameGrid // TODO change for non-lateinit empty Grid

    fun discover(fieldCoordinate: FieldCoordinate): List<DomainEvent> {
        if (gameGrid.isDiscovered(fieldCoordinate)) throw AlreadyDiscovered(gameID, fieldCoordinate)
        if (gameGrid.isToggled(fieldCoordinate)) throw CannotDiscoverToggledField(gameID, fieldCoordinate)
        if (gameGrid.isBomb(fieldCoordinate)) {
            val now = LocalDateTime.now()
            return listOf(BombExploded(gameID, fieldCoordinate).occurredAt(now), GameEnded(gameID).occurredAt(now))
        }
        val allCoordinates = gameGrid.discoverAllAround(fieldCoordinate)
        return listOf(FieldDiscoveredEvent(gameID, fieldCoordinate, allCoordinates))
    }

    fun status(): GameStatus {
        return this.currentStatus
    }

    fun getBoardView(): BoardView {
        return gameGrid.getView()
    }

    fun initializeGame(rows: Int, columns: Int, bombsCoordinates: Set<FieldCoordinate>) {
        this.currentStatus = GameStatus.IN_PROGRESS
        this.gameGrid = GameGrid(rows, columns, bombsCoordinates)
    }

    fun canDiscover(fieldCoordinate: FieldCoordinate): DiscoverTry {
        return when (this.gameGrid.isDiscovered(fieldCoordinate)) { // TODO where to check if in bounds?
            true -> DiscoverTry.ALREADY_DISCOVERED
            false -> DiscoverTry.CAN_BE_DISCOVERED
        }
    }

    fun confirmDiscovery(allDiscoveredFields: List<FieldCoordinate>) {
        allDiscoveredFields.forEach { this.gameGrid.mapAsDiscovered(it) }
    }

    fun toggle(coordinate: FieldCoordinate): List<DomainEvent> {
        if (this.gameGrid.isDiscovered(coordinate)) {
            throw CannotToggleField(this.gameID, coordinate, "Cannot toggle discovered field")
        }
        this.gameGrid.toggle(coordinate)

        return listOf(FieldToggled(gameID, coordinate))
    }
}


enum class DiscoverTry {
    ALREADY_DISCOVERED, CAN_BE_DISCOVERED
}


class GameBuilder(val rows: Int, val cols: Int) {
    private lateinit var bombsCoordinates: Set<FieldCoordinate>

    fun bombs(bombsPositions: Set<FieldCoordinate>): GameBuilder {
        this.bombsCoordinates = bombsPositions
        return this
    }

    fun build(): Game {
        val gameCreatedEvent = GameCreatedEvent(Random.nextInt(), rows, cols, this.bombsCoordinates).occurredAt(LocalDateTime.now())
        return EventHandler().applyAll(Game(Random.nextInt()), listOf(gameCreatedEvent))// TODO Event handler should be hidden?
    }
}
